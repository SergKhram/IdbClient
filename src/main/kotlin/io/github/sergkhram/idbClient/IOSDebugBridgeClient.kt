package io.github.sergkhram.idbClient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import idb.ConnectRequest
import idb.TargetDescription
import idb.TargetDescriptionRequest
import io.github.sergkhram.idbClient.entities.*
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import io.github.sergkhram.idbClient.util.JsonUtil
import io.grpc.ManagedChannelBuilder
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
        internal val clients: ConcurrentHashMap<String, CompanionData> = ConcurrentHashMap()
        private val log = KLogger.logger
        private val noCompanionWithUdid: (String) -> NoSuchElementException = {
            NoSuchElementException("There is no companion with udid $it")
        }
    }

    private fun JsonNode.convertJsonNodeToTargetDescription() = TargetDescription.newBuilder()
        .setUdid(this.get("udid").asText())
        .setName(this.get("name").asText())
        .setState(this.get("state").asText())
        .setTargetType(this.get("type").asText())
        .setOsVersion(this.get("os_version").asText())
        .setArchitecture(this.get("architecture").asText())
        .build()

    init {
        runBlocking {
            listOfCompanions.forEach { address ->
                connectToCompanion(address)
            }
        }
        if (withLocal && System.getProperty("os.name")
                .contains("mac", ignoreCase = true) && File("/usr/local/bin/idb_companion").exists()
        ) {
            var proc: Process? = null
            try {
                val cmd = listOf("/usr/local/bin/idb_companion", "--list", "1", "--json")
                proc = ProcessBuilder(cmd)
                    .directory(File(System.getProperty("user.home")))
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

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
                            {
                                val port = 10882
                                val runCompanionCmd =
                                    listOf("/usr/local/bin/idb_companion", "--udid", it.udid, "> /dev/null 2>&1")
                                val runCompanionProc = ProcessBuilder(runCompanionCmd)
                                    .directory(File(System.getProperty("user.home")))
                                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                                    .redirectError(ProcessBuilder.Redirect.PIPE)
                                    .start()
                                Pair(
                                    ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext()
                                        .executor(dispatcher.asExecutor()).build(),
                                    runCompanionProc
                                )
                            },
                            true
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

    @Throws(NoSuchElementException::class)
    suspend fun <T : Any?> execute(request: IdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion.channelBuilder, companion.isLocal).use { grpcClient ->
                request.execute(grpcClient)
            }
        } ?: throw noCompanionWithUdid(udid)
    }

    @Throws(NoSuchElementException::class)
    suspend fun <T : Any?> execute(request: AsyncIdbRequest<Flow<T>>, udid: String): Flow<T> {
        return clients[udid]?.let { companion ->
            val grpcClient = GrpcClient(companion.channelBuilder, companion.isLocal)
            return@let request.execute(grpcClient)
                .onCompletion {
                    grpcClient.close()
                }
        } ?: throw noCompanionWithUdid(udid)
    }

    @Throws(NoSuchElementException::class)
    suspend fun <T : Any?> execute(request: PredicateIdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            GrpcClient(companion.channelBuilder, companion.isLocal).use { grpcClient ->
                request.execute(grpcClient)
            }
        } ?: throw noCompanionWithUdid(udid)
    }

    suspend fun getTargetsList(): List<TargetDescription> {
        return clients.mapNotNull { client ->
            try {
                GrpcClient(client.value.channelBuilder, client.value.isLocal).use { grpcClient ->
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
        val remoteChannelBuilder = {
            Pair(
                if (address is TcpAddress) {
                    ManagedChannelBuilder.forAddress(address.host, address.port).usePlaintext()
                        .executor(dispatcher.asExecutor()).build()
                } else {
                    val domainAddress = address as DomainSocketAddress
                    ManagedChannelBuilder.forTarget(domainAddress.path).usePlaintext()
                        .executor(dispatcher.asExecutor())
                        .build()
                },
                null
            )
        }

        try {
            val grpcClient = GrpcClient(remoteChannelBuilder)
            val connectionResponse = grpcClient.use {
                it.stub.connect(
                    ConnectRequest.getDefaultInstance()
                )
            }
            udid = connectionResponse.companion.udid
            clients[udid] = CompanionData(remoteChannelBuilder, false)
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
}
