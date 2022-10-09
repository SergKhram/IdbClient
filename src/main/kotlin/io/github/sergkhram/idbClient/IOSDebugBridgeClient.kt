package io.github.sergkhram.idbClient

import com.fasterxml.jackson.databind.node.ArrayNode
import idb.ConnectRequest
import io.github.sergkhram.idbClient.Const.localIdbCompanionPath
import io.github.sergkhram.idbClient.Const.localTargetsListCmd
import io.github.sergkhram.idbClient.Const.noCompanionWithUdid
import io.github.sergkhram.idbClient.entities.*
import io.github.sergkhram.idbClient.entities.companion.CompanionData
import io.github.sergkhram.idbClient.entities.companion.LocalCompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.entities.response.DescribeKtResponse
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import io.github.sergkhram.idbClient.requests.management.DescribeRequest
import io.github.sergkhram.idbClient.util.JsonUtil
import io.github.sergkhram.idbClient.util.asString
import io.github.sergkhram.idbClient.util.cmdBuilder
import io.github.sergkhram.idbClient.util.convertJsonNodeToTargetDescription
import io.grpc.StatusException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class IOSDebugBridgeClient(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val listOfCompanions: List<Address> = emptyList(),
    withLocal: Boolean = false
) {
    companion object {
        private val log = KLogger.logger
    }

    private val clients: ConcurrentHashMap<String, CompanionData> = ConcurrentHashMap()

    init {
        runBlocking {
            listOfCompanions.forEach { address ->
                connectToCompanion(address)
            }
        }
        if (withLocal
            && System.getProperty("os.name").contains("mac", ignoreCase = true)
            && File(localIdbCompanionPath).exists()
        ) {
            var proc: Process? = null
            try {
                proc = cmdBuilder(localTargetsListCmd).start()
                proc.waitFor()
                val output = proc.inputStream.bufferedReader().readText()

                val outputAsJsonNode = JsonUtil.convertStringToJsonNode(
                    "[${
                        output.replace(
                            "}\n" +
                                    "{", "},{"
                        )
                    }]"
                )

                val localTargets = (outputAsJsonNode as ArrayNode?)?.map { it.convertJsonNodeToTargetDescription() }
                localTargets?.forEach { target ->
                    target.let {
                        clients[it.udid] = LocalCompanionData(it.udid)
                    }
                }
            } catch (e: IOException) {
                log.info(e.localizedMessage, e)
            } finally {
                proc?.takeIf { proc.isAlive }?.destroy()
            }
        }
    }

    @Throws(NoSuchElementException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: IdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion, dispatcher).use { grpcClient ->
                request.execute(grpcClient)
            }
        } ?: throw noCompanionWithUdid(udid)
    }

    @Throws(NoSuchElementException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: AsyncIdbRequest<Flow<T>>, udid: String): Flow<T> {
        return clients[udid]?.let { companion ->
            val grpcClient = GrpcClient(companion, dispatcher)
            return@let request.execute(grpcClient)
                .onCompletion {
                    grpcClient.close()
                }
        } ?: throw noCompanionWithUdid(udid)
    }

    @Throws(NoSuchElementException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: PredicateIdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion, dispatcher).use { grpcClient ->
                request.execute(grpcClient)
            }
        } ?: throw noCompanionWithUdid(udid)
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
        val remoteCompanionData = RemoteCompanionData(address)
        try {
            val grpcClient = GrpcClient(remoteCompanionData, dispatcher)
            val connectionResponse = grpcClient.use {
                it.stub.connect(
                    ConnectRequest.getDefaultInstance()
                )
            }
            udid = connectionResponse.companion.udid
            clients[udid] = remoteCompanionData
            log.debug("Connecting $address companion finished")
        } catch (e: StatusException) {
            log.info("Connection refused for ${address.asString()}", e)
        }
        return udid
    }

    fun disconnectCompanion(udid: String) {
        clients.takeIf { it.containsKey(udid) }?.let {
            log.debug("Disconnecting $udid companion started")
            it.remove(udid)
            log.debug("Disconnecting $udid companion finished")
        }
    }

    fun disconnectCompanion(host: String?, port: Int?) {
        clients.entries.firstOrNull {
            it.value.takeIf { value -> !value.isLocal }?.let { companionData ->
                companionData as RemoteCompanionData
                companionData.address is TcpAddress
                    && companionData.address.host == host
                    && companionData.address.port == port
            } ?: false
        }?.let { entry ->
            log.debug("Disconnecting $host:$port companion started")
            clients.remove(entry.key)
            log.debug("Disconnecting $host:$port companion finished")
        }
    }

    private suspend fun <K, V, B> ConcurrentHashMap<K, V>.pMap(f: suspend (Map.Entry<K, V>) -> B?): List<B?> = coroutineScope {
        map { async { f(it) } }.awaitAll()
    }
}
