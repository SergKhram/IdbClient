package io.github.sergkhram.idbClient.requests.media

import io.github.sergkhram.idbClient.entities.response.ScreenshotResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class ScreenshotRequest: IdbRequest<ScreenshotResponse>() {
    override suspend fun execute(client: GrpcClient): ScreenshotResponse {
        val result = client.stub.screenshot(
            idb.ScreenshotRequest.getDefaultInstance()
        )
        return ScreenshotResponse(
            result.imageData.toByteArray(),
            result.imageFormat
        )
    }
}