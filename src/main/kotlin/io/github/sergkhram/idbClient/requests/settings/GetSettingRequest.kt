package io.github.sergkhram.idbClient.requests.settings

import idb.GetSettingResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.settings.GetSettingRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class GetSettingRequest(private val setting: GetSettingRequestBody): IdbRequest<GetSettingResponse>() {
    override suspend fun execute(client: GrpcClient): GetSettingResponse {
        return client.stub.getSetting(
            setting.requestBody
        )
    }
}