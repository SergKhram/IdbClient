package io.github.sergkhram.idbClient.entities

import idb.CompanionServiceGrpcKt
import io.github.sergkhram.idbClient.Const.localGrpcStartTimeout
import io.github.sergkhram.idbClient.Const.localHost
import io.github.sergkhram.idbClient.Const.startLocalCompanionCmd
import io.github.sergkhram.idbClient.entities.companion.CompanionData
import io.github.sergkhram.idbClient.entities.companion.LocalCompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.util.cmdBuilder
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
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): Closeable {
    companion object {
        private val log = KLogger.logger
    }
    private lateinit var channel: ManagedChannel
    private var process: Process? = null

    val stub: CompanionServiceGrpcKt.CompanionServiceCoroutineStub by lazy {
        if(companionData.isLocal) {
            companionData as LocalCompanionData
            val port = PortRegistry.getFreePort()
            val processBuilder = cmdBuilder(
                startLocalCompanionCmd(
                    companionData.udid,
                    port
                )
            )
            process = processBuilder.start()
            runBlocking {
                waitUntilLocalCompanionStarted(port, companionData.udid)
            }
            channel = prepareManagedChannel(
                TcpAddress(localHost, port),
                dispatcher
            )
        } else {
            companionData as RemoteCompanionData
            channel = prepareManagedChannel(companionData.address, dispatcher)
        }
        CompanionServiceGrpcKt.CompanionServiceCoroutineStub(channel)
    }

    @PreDestroy
    override fun close() {
        log.debug("Started gRPC client ${this.hashCode()} shutdown")
        if(companionData.isLocal) {
            process?.takeIf { it.isAlive }?.destroy()
        }
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        log.debug("Completed gRPC client ${this.hashCode()} shutdown")
    }

    private suspend fun waitUntilLocalCompanionStarted(port: Int, udid: String) {
        try {
            withTimeout(localGrpcStartTimeout) {
                while (PortRegistry.available(port)) {
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
}
