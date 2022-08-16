package io.github.sergkhram.idbClient.requests.media

import idb.ScreenshotResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class ScreenshotRequest: IdbRequest<ScreenshotResponse>() {
    override suspend fun execute(client: GrpcClient): ScreenshotResponse {
        return client.stub.screenshot(
            idb.ScreenshotRequest.getDefaultInstance()
        )
    }
}