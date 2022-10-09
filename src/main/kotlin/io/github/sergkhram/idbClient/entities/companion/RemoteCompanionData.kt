package io.github.sergkhram.idbClient.entities.companion

import io.github.sergkhram.idbClient.entities.Address

class RemoteCompanionData(val address: Address): CompanionData {
    override val isLocal = false
}