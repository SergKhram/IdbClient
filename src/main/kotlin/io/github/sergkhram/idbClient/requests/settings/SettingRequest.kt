package io.github.sergkhram.idbClient.requests.settings

import idb.SettingResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.settings.SettingRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class SettingRequest(private val setting: SettingRequestBody): IdbRequest<SettingResponse>() {
    override suspend fun execute(client: GrpcClient): SettingResponse {
        return client.stub.setting(
            setting.requestBody
        )
    }
}