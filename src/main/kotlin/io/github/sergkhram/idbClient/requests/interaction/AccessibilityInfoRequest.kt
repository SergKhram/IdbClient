package io.github.sergkhram.idbClient.requests.interaction

import idb.AccessibilityInfoResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.interaction.AccessibilityInfoRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class AccessibilityInfoRequest(private val accessibilityInfo: AccessibilityInfoRequestBody): IdbRequest<AccessibilityInfoResponse>() {
    override suspend fun execute(client: GrpcClient): AccessibilityInfoResponse {
        return client.stub.accessibilityInfo(
            accessibilityInfo.requestBody
        )
    }
}