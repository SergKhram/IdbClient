package io.github.sergkhram.idbClient.requests.interaction

import idb.SendNotificationRequest
import idb.SendNotificationResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class SendNotificationRequest(private val bundleId: String = "", private val jsonPayload: String = ""): IdbRequest<SendNotificationResponse>() {
    override suspend fun execute(client: GrpcClient): SendNotificationResponse {
        return client.stub.sendNotification(
            SendNotificationRequest.newBuilder().setBundleId(bundleId).setJsonPayload(jsonPayload).build()
        )
    }
}