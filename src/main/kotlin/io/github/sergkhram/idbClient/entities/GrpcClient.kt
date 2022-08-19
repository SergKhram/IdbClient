package io.github.sergkhram.idbClient.entities

import idb.CompanionServiceGrpcKt
import io.github.sergkhram.idbClient.Const
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.util.processBuilder
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable
import java.net.ServerSocket
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

class GrpcClient(
    private val address: Address,
    private val isLocal: Boolean = false,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val udid: String = ""
): Closeable {
    companion object {
        private val log = KLogger.logger
    }
    private lateinit var channel: ManagedChannel
    private var process: Process? = null

    val stub: CompanionServiceGrpcKt.CompanionServiceCoroutineStub by lazy {
        if(isLocal) {
            val port = ServerSocket(0).use { socket -> socket.localPort }
            process = processBuilder(
                Const.startLocalCompanionCmd(
                    Pair(udid, port)
                )
            ).start()
            channel = ManagedChannelBuilder.forAddress((address as TcpAddress).host, port).usePlaintext()
                .executor(dispatcher.asExecutor()).build()
        } else {
            channel = if (address is TcpAddress) {
                ManagedChannelBuilder.forAddress(address.host, address.port).usePlaintext()
                    .executor(dispatcher.asExecutor()).build()
            } else {
                val domainAddress = address as DomainSocketAddress
                ManagedChannelBuilder.forTarget(domainAddress.path).usePlaintext()
                    .executor(dispatcher.asExecutor())
                    .build()
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
