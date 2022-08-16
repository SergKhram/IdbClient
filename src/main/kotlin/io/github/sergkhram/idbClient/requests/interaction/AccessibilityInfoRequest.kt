package io.github.sergkhram.idbClient.requests.interaction

import idb.AccessibilityInfoRequest
import idb.AccessibilityInfoResponse
import idb.Point
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.AccessibilityInfoRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class AccessibilityInfoRequest(private val requestBody: AccessibilityInfoRequestBody): IdbRequest<AccessibilityInfoResponse>() {
    override suspend fun execute(client: GrpcClient): AccessibilityInfoResponse {
        return client.stub.accessibilityInfo(
            AccessibilityInfoRequest.newBuilder()
                .setFormatValue(requestBody.format.value)
                .setPoint(
                    Point.newBuilder().setX(requestBody.point.x).setY(requestBody.point.y).build()
                )
                .build()
        )
    }
}