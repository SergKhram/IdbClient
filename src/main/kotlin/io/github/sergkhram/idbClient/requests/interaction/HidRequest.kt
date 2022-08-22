package io.github.sergkhram.idbClient.requests.interaction

import idb.HIDResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.HidRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class HidRequest(private val hid: HidRequestBody): IdbRequest<HIDResponse>() {
    override suspend fun execute(client: GrpcClient): HIDResponse {
        return client.stub.hid(
            hid.requestBody
        )
    }
}