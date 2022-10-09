package io.github.sergkhram.idbClient.entities.companion

class LocalCompanionData(val udid: String): CompanionData {
    override val isLocal = true
}