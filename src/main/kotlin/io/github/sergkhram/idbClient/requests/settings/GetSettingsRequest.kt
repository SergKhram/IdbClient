package io.github.sergkhram.idbClient.requests.settings

import idb.ListSettingRequest
import idb.ListSettingResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.settings.SettingType
import io.github.sergkhram.idbClient.requests.IdbRequest

class GetSettingsRequest(private val settingType: SettingType): IdbRequest<ListSettingResponse>() {
    override suspend fun execute(client: GrpcClient): ListSettingResponse {
        return client.stub.listSettings(
            ListSettingRequest.newBuilder().setSettingValue(settingType.value).build()
        )
    }
}