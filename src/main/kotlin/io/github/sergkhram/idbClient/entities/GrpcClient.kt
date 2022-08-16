package io.github.sergkhram.idbClient.entities

import idb.CompanionServiceGrpcKt
import io.grpc.ManagedChannel
import java.io.Closeable
import java.io.File
import java.util.concurrent.TimeUnit

class GrpcClient(private val isLocal: Boolean = false, private val channelBuilder: () -> Pair<ManagedChannel, Process?>): Closeable {
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

    override fun close() {
        if(isLocal) process?.takeIf { it.isAlive }?.destroy()
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
