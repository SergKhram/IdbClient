package io.github.sergkhram.idbClient.entities

import idb.CompanionServiceGrpcKt
import io.github.sergkhram.idbClient.logs.KLogger
import io.grpc.ManagedChannel
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

class GrpcClient(private val isLocal: Boolean = false, private val channelBuilder: () -> Pair<ManagedChannel, Process?>) {
    companion object {
        private val log = KLogger.logger
    }
    private lateinit var channel: ManagedChannel
    private var process: Process? = null

    val stub: CompanionServiceGrpcKt.CompanionServiceCoroutineStub by lazy {
        val executionResult = channelBuilder.invoke()
        channel = executionResult.first
        executionResult.second?.let {
            process = it
        }

        CompanionServiceGrpcKt.CompanionServiceCoroutineStub(channel)
    }

    @PreDestroy
    fun close() {
        log.info("Started gRPC client ${this.hashCode()} shutdown")
        if(isLocal) process?.takeIf { it.isAlive }?.destroy()
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        log.info("Completed gRPC client ${this.hashCode()} shutdown")
    }
}
