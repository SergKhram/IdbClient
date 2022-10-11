package io.github.sergkhram.idbClient.entities.response

import idb.CompanionInfo
import idb.TargetDescription
import idb.TargetDescriptionResponse as DescribeResponse
import io.github.sergkhram.idbClient.entities.address.Address

class DescribeKtResponse(
    describeResponse: DescribeResponse,
    val address: Address?
) {
    val targetDescription: TargetDescription = describeResponse.targetDescription
    val companion: CompanionInfo = describeResponse.companion
}