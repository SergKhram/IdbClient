package io.github.sergkhram.idbClient.entities.companion

import io.github.sergkhram.idbClient.entities.address.Address
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.util.prepareManagedChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit

internal class RemoteCompanionData(
    val address: Address,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): CompanionData {
    companion object {
        private val log = KLogger.logger
    }

    override val isLocal = false
    val channel = prepareManagedChannel(address, dispatcher)

    internal fun shutdownChannel() {
        channel
            .shutdown()
            .awaitTermination(5, TimeUnit.SECONDS)
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            log.info("[$address] channel shutdown")
            shutdownChannel()
        })
    }
}