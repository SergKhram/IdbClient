package io.github.sergkhram.idbClient

import idb.ConnectRequest
import io.github.sergkhram.idbClient.Const.localIdbCompanionPath
import io.github.sergkhram.idbClient.Const.localTargetsListCmd
import io.github.sergkhram.idbClient.Const.startCompanionCmd
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.ProcessManager.getLocalTargetsJson
import io.github.sergkhram.idbClient.entities.address.Address
import io.github.sergkhram.idbClient.entities.companion.CompanionData
import io.github.sergkhram.idbClient.entities.companion.LocalCompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.entities.response.DescribeKtResponse
import io.github.sergkhram.idbClient.handlers.GrpcErrorHandler.handle
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import io.github.sergkhram.idbClient.requests.management.DescribeRequest
import io.github.sergkhram.idbClient.ssh.SSHConfig
import io.github.sergkhram.idbClient.ssh.SSHExecutor
import io.github.sergkhram.idbClient.util.*
import io.github.sergkhram.idbClient.util.beautifyJsonString
import io.github.sergkhram.idbClient.util.getUdids
import io.github.sergkhram.idbClient.util.isStartedOnMac
import io.grpc.StatusException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import java.io.File
import java.nio.channels.ClosedChannelException
import java.util.concurrent.ConcurrentHashMap

/**
 * @param dispatcher Kotlin dispatcher, will be used for grpc channels
 * @param listOfCompanions addresses, will be used to connect to at the start of client
 * @param withLocal additionally use local targets in case of starting on Mac
 */
class IOSDebugBridgeClient(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val listOfCompanions: List<Address> = emptyList(),
    withLocal: Boolean = false
) {
    companion object {
        private val log = KLogger.logger
    }

    private val clients: ConcurrentHashMap<String, CompanionData> = ConcurrentHashMap()

    init {
        runBlocking {
            val connectJobs = mutableListOf<Job>()
            listOfCompanions.forEach { address ->
                connectJobs.add(
                    launch {
                        connectToCompanion(address)
                    }
                )
            }
            connectJobs.joinAll()
        }
        if (withLocal
            && isStartedOnMac()
            && File(localIdbCompanionPath).exists()
        ) {
            val localTargetsJson = getLocalTargetsJson()
            localTargetsJson?.let {json ->
                json.getUdids().forEach {
                        clients[it] = LocalCompanionData(it)
                    }
            }
        }
    }

    @Throws(NoCompanionWithUdidException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: IdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion, dispatcher).use { grpcClient ->
                handle(
                    { (companion as RemoteCompanionData).rebuildChannel() },
                    { clients.containsKey(udid) && companion is RemoteCompanionData },
                    ClosedChannelException::class
                ) {
                    request.execute(grpcClient)
                }
            }
        } ?: throw NoCompanionWithUdidException(udid)
    }

    @Throws(NoCompanionWithUdidException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: AsyncIdbRequest<Flow<T>>, udid: String): Flow<T> {
        return clients[udid]?.let { companion ->
            val grpcClient = GrpcClient(companion, dispatcher)
            handle(
                { (companion as RemoteCompanionData).rebuildChannel() },
                { clients.containsKey(udid) && companion is RemoteCompanionData },
                ClosedChannelException::class
            ) {
                request.execute(grpcClient)
                    .onCompletion {
                        grpcClient.close()
                    }
            }
        } ?: throw NoCompanionWithUdidException(udid)
    }

    @Throws(NoCompanionWithUdidException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: PredicateIdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion, dispatcher).use { grpcClient ->
                handle(
                    { (companion as RemoteCompanionData).rebuildChannel() },
                    { clients.containsKey(udid) && companion is RemoteCompanionData },
                    ClosedChannelException::class
                ) {
                    request.execute(grpcClient)
                }
            }
        } ?: throw NoCompanionWithUdidException(udid)
    }

    suspend fun getTargetsList(): List<DescribeKtResponse> {
        return clients.pMap { client ->
            try {
                GrpcClient(client.value, dispatcher).use { grpcClient ->
                    val describeResponse = DescribeRequest().execute(grpcClient)
                    DescribeKtResponse(
                        describeResponse,
                        client.value
                            .takeIf {
                                !it.isLocal
                            }?.let {
                                (it as RemoteCompanionData).address
                            }
                    )
                }
            } catch (e: StatusException) {
                log.info("Connection refused for ${client.key}", e)
                null
            }
        }.filterNotNull()
    }

    suspend fun connectToCompanion(address: Address, dispatcher: CoroutineDispatcher = this.dispatcher): String? {
        log.debug("Connecting $address companion started")
        var udid: String? = null
        val remoteCompanionData = RemoteCompanionData(address, dispatcher)
        try {
            val grpcClient = GrpcClient(remoteCompanionData)
            val connectionResponse = grpcClient.use {
                it.stub.connect(
                    ConnectRequest.getDefaultInstance()
                )
            }
            udid = connectionResponse.companion.udid
            clients[udid] = remoteCompanionData
            log.debug("Connecting $address companion finished")
        } catch (e: StatusException) {
            log.info("Connection refused for $address", e)
        }
        return udid
    }

    fun disconnectCompanion(udid: String) {
        clients.entries.firstOrNull { it.key == udid }?.let {
            log.debug("Disconnecting $udid companion started")
            it.value.takeIf { client -> !client.isLocal }?.let { remote ->
                (remote as RemoteCompanionData).shutdownChannel()
            }
            clients.remove(udid)
            log.debug("Disconnecting $udid companion finished")
        }
    }

    fun disconnectCompanion(address: Address) {
        clients.entries.firstOrNull {
            !it.value.isLocal && (it.value as RemoteCompanionData).address == address
        }?.let { entry ->
            log.debug("Disconnecting $address companion started")
            (entry.value as RemoteCompanionData).shutdownChannel()
            clients.remove(entry.key)
            log.debug("Disconnecting $address companion finished")
        }
    }

    @IdbExperimental
    fun getHostTargets(sshConfig: SSHConfig): List<String> {
        with(sshConfig) {
            val cmdResult = SSHExecutor.execute(this, localTargetsListCmd.joinToString(" "))
            if(!cmdResult.error.isNullOrEmpty())
                throw GetHostTargetsException(host, port, cmdResult.error!!)
            else {
                return cmdResult.output
                    ?.beautifyJsonString()
                    ?.getUdids() ?: throw NoSimulatorsOnHostException(host)
            }
        }
    }

    @IdbExperimental
    fun startRemoteTargetCompanion(sshConfig: SSHConfig, udid: String, companionPort: Int = 10882): Boolean {
        with(sshConfig) {
            val startRemoteCompanionCmd = startCompanionCmd(
                udid,
                companionPort
            ).plus(">/dev/null 2>&1 &")
            val cmdResult = SSHExecutor.execute(this, startRemoteCompanionCmd.joinToString(" "))
            return if(
                !cmdResult.error.isNullOrEmpty() ||
                (cmdResult.exitCode!=null && cmdResult.exitCode!!>0)
            ) {
                log.warn("Start remote target process for $udid failed: ${cmdResult.error}")
                false
            }
            else true
        }
    }

    private suspend fun <K, V, B> ConcurrentHashMap<K, V>.pMap(f: suspend (Map.Entry<K, V>) -> B?): List<B?> =
        coroutineScope {
            map { async { f(it) } }.awaitAll()
        }
}
