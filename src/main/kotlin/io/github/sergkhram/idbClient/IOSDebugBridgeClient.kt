package io.github.sergkhram.idbClient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import idb.ConnectRequest
import idb.TargetDescription
import idb.TargetDescriptionRequest
import io.github.sergkhram.idbClient.entities.*
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.util.JsonUtil
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap

class IOSDebugBridgeClient(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val listOfCompanions: List<Address> = emptyList(),
    private val withLocal: Boolean = false
) {
    companion object {
        internal val clients: ConcurrentHashMap<String, CompanionData> = ConcurrentHashMap()
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
        listOfCompanions.forEach { address ->
            val grpcClient = GrpcClient {
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
            runBlocking {
                val tempFile = withContext(dispatcher) {
                    Files.createTempFile("temp", ".tmp")
                }.toFile().also {
                    it.setReadable(true)
                    it.setWritable(true)
                }

                val connectResponse = grpcClient.stub.connect(
                    ConnectRequest.newBuilder().setLocalFilePath(tempFile.absolutePath).build()
                )
                clients[connectResponse.companion.udid] = CompanionData(grpcClient, false)
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
                            GrpcClient(true) {
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
                e.printStackTrace()
            } finally {
                proc?.destroy()
            }
        }
    }

    suspend fun <T : Any?> execute(request: IdbRequest<T>, udid: String): T {
        return clients[udid]?.let { companion ->
            return@let request.execute(companion.grpcClient)
        } ?: throw NoSuchElementException("There is no companion with udid $udid")
    }

    suspend fun getTargetsList(): List<TargetDescription> {
        return clients.elements().toList().map { companion ->
            companion.grpcClient.stub.describe(
                TargetDescriptionRequest.getDefaultInstance()
            ).targetDescription
        }
    }
}
