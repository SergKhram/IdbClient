package io.github.sergkhram.idbClient.entities.companion

import io.github.sergkhram.idbClient.entities.address.Address
import io.github.sergkhram.idbClient.util.prepareManagedChannel
import java.util.concurrent.TimeUnit

internal class RemoteCompanionData(val address: Address): CompanionData {
    override val isLocal = false
    val channel = prepareManagedChannel(address)

    internal fun shutdownChannel() {
        channel
            .shutdown()
            .awaitTermination(5, TimeUnit.SECONDS)
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownChannel()
        })
    }
}