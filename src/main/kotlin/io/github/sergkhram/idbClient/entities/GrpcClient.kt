package io.github.sergkhram.idbClient.entities

import idb.CompanionServiceGrpcKt
import io.github.sergkhram.idbClient.Const.localGrpcStartTimeout
import io.github.sergkhram.idbClient.Const.localHost
import io.github.sergkhram.idbClient.entities.ProcessManager.available
import io.github.sergkhram.idbClient.entities.ProcessManager.startLocalCompanion
import io.github.sergkhram.idbClient.entities.address.TcpAddress
import io.github.sergkhram.idbClient.entities.companion.CompanionData
import io.github.sergkhram.idbClient.entities.companion.LocalCompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.util.prepareManagedChannel
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

internal class GrpcClient(
    private val companionData: CompanionData,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : Closeable {
    companion object {
        private val log = KLogger.logger
    }

    private lateinit var channel: ManagedChannel
    private var process: Process? = null

    val stub: CompanionServiceGrpcKt.CompanionServiceCoroutineStub by lazy {
        when (companionData) {
            is LocalCompanionData -> {
                val startResult = startLocalCompanion(companionData.udid)
                process = startResult.first
                runBlocking {
                    waitUntilLocalCompanionStarted(startResult.second, companionData.udid)
                }
                channel = prepareManagedChannel(
                    TcpAddress(localHost, startResult.second),
                    dispatcher
                )
            }
            is RemoteCompanionData -> {
                logChannelInfo(companionData)
                channel = companionData.getChannel()
            }
        }
        CompanionServiceGrpcKt.CompanionServiceCoroutineStub(channel)
    }

    @PreDestroy
    override fun close() {
        log.debug("gRPC client ${this.hashCode()} shutdown started")
        if (companionData.isLocal) {
            if (this::channel.isInitialized) channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
            process?.takeIf { it.isAlive }?.destroy()
        }
        log.debug("gRPC client ${this.hashCode()} shutdown completed")
    }

    private suspend fun waitUntilLocalCompanionStarted(port: Int, udid: String) {
        try {
            withTimeout(localGrpcStartTimeout) {
                while (available(port)) {
                    delay(100)
                }
            }
        } catch (e: TimeoutCancellationException) {
            process?.destroy()
            throw StatusException(
                Status.ABORTED.withDescription("Start local companion($udid) process failed")
            )
        }
    }

    private fun logChannelInfo(companionData: RemoteCompanionData) {
        log.debug("${companionData.address} - isShutdown = ${companionData.getChannel().isShutdown}")
        log.debug("${companionData.address} - isTerminated = ${companionData.getChannel().isTerminated}")
        log.debug("${companionData.address} - state = ${companionData.getChannel().getState(true)}")
    }
}
