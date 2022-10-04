package io.github.sergkhram.idbClient.entities.response

import idb.CompanionInfo
import idb.TargetDescription
import idb.TargetDescriptionResponse
import io.github.sergkhram.idbClient.entities.Address

class TargetDescriptionKtResponse(
    targetDescriptionResponse: TargetDescriptionResponse,
    val address: Address?
) {
    val targetDescription: TargetDescription = targetDescriptionResponse.targetDescription
    val companion: CompanionInfo = targetDescriptionResponse.companion
}