package io.github.sergkhram.idbClient.entities

import idb.CompanionServiceGrpcKt
import io.github.sergkhram.idbClient.logs.KLogger
import io.grpc.ManagedChannel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.io.Closeable
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

class GrpcClient(
    private val channelBuilder: () -> Pair<ManagedChannel, ProcessBuilder?>, private val isLocal: Boolean = false
): Closeable {
    companion object {
        private val log = KLogger.logger
    }
    private lateinit var channel: ManagedChannel
    private var process: Process? = null

    val stub: CompanionServiceGrpcKt.CompanionServiceCoroutineStub by lazy {
        val executionResult = channelBuilder.invoke()
        channel = executionResult.first
        executionResult.second?.let { processBuilder ->
            process = processBuilder.start()
            process?.let { proc ->
                runBlocking {
                    withTimeoutOrNull(Duration.ofSeconds(5).toMillis()) {
                        proc.inputStream.bufferedReader().useLines { lines ->
                            lines.forEach {
                                if(it.contains("{\"grpc_port\":")) return@withTimeoutOrNull
                            }
                        }
                    }
                }
            }
        }

        CompanionServiceGrpcKt.CompanionServiceCoroutineStub(channel)
    }

    @PreDestroy
    override fun close() {
        log.debug("Started gRPC client ${this.hashCode()} shutdown")
        if(isLocal) process?.takeIf { it.isAlive }?.destroy()
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        log.debug("Completed gRPC client ${this.hashCode()} shutdown")
    }
}
