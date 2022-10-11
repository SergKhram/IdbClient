package io.github.sergkhram.idbClient.entities.companion

internal class LocalCompanionData(val udid: String): CompanionData {
    override val isLocal = true
}