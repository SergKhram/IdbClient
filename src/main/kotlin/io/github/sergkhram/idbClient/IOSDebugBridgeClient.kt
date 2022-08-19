package io.github.sergkhram.idbClient

import com.fasterxml.jackson.databind.node.ArrayNode
import idb.ConnectRequest
import idb.TargetDescription
import idb.TargetDescriptionRequest
import io.github.sergkhram.idbClient.Const.localIdbCompanionPath
import io.github.sergkhram.idbClient.Const.localTargetsListCmd
import io.github.sergkhram.idbClient.Const.noCompanionWithUdid
import io.github.sergkhram.idbClient.Const.startLocalCompanionCmd
import io.github.sergkhram.idbClient.entities.*
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import io.github.sergkhram.idbClient.util.JsonUtil
import io.github.sergkhram.idbClient.util.processBuilder
import io.github.sergkhram.idbClient.util.convertJsonNodeToTargetDescription
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import java.io.File
import java.io.IOException
import java.net.ServerSocket
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
                proc = processBuilder(localTargetsListCmd).start()
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
                        clients[it.udid] = CompanionData(
                            TcpAddress("127.0.0.1"),
                            true,
                        )
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
            GrpcClient(companion.address, companion.isLocal, udid = udid).use { grpcClient ->
                request.execute(grpcClient)
            }
        } ?: throw noCompanionWithUdid(udid)
    }

    @Throws(NoSuchElementException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: AsyncIdbRequest<Flow<T>>, udid: String): Flow<T> {
        return clients[udid]?.let { companion ->
            val grpcClient = GrpcClient(companion.address, companion.isLocal, udid = udid)
            return@let request.execute(grpcClient)
                .onCompletion {
                    grpcClient.close()
                }
        } ?: throw noCompanionWithUdid(udid)
    }

    @Throws(NoSuchElementException::class, StatusException::class)
    suspend fun <T : Any?> execute(request: PredicateIdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion.address, companion.isLocal, udid = udid).use { grpcClient ->
                request.execute(grpcClient)
            }
        } ?: throw noCompanionWithUdid(udid)
    }

    suspend fun getTargetsList(): List<TargetDescription> {
        return clients.mapNotNull { client ->
            try {
                GrpcClient(client.value.address, client.value.isLocal, udid = client.key).use { grpcClient ->
                    grpcClient.stub.describe(
                        TargetDescriptionRequest.getDefaultInstance()
                    ).targetDescription
                }
            } catch (e: StatusException) {
                log.info("Connection refused for ${client.key}", e)
                null
            }
        }
    }

    suspend fun connectToCompanion(address: Address, dispatcher: CoroutineDispatcher = this.dispatcher): String? {
        var udid: String? = null
        try {
            val grpcClient = GrpcClient(address, dispatcher = dispatcher)
            val connectionResponse = grpcClient.use {
                it.stub.connect(
                    ConnectRequest.getDefaultInstance()
                )
            }
            udid = connectionResponse.companion.udid
            clients[udid] = CompanionData(address)
        } catch (e: StatusException) {
            val addressString = if(address is TcpAddress) {
                address.host + ":" + address.port
            } else {
                (address as DomainSocketAddress).path
            }
            log.info("Connection refused for $addressString", e)
        }
        return udid
    }

    fun disconnectCompanion(udid: String) {
        clients.remove(udid)
    }
}
