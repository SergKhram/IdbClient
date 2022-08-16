package io.github.sergkhram.idbClient.entities.requestsBody

import idb.GetSettingRequest
import idb.Setting

sealed class GetSettingRequestBody {
    abstract val requestBody: GetSettingRequest

    data class LocaleSetting(val localeIdentifier: String) : GetSettingRequestBody() {
        override val requestBody = GetSettingRequest.newBuilder().setSetting(Setting.LOCALE).build()
    }

    data class AnySetting(val name: String, val value: String, val valueType: String, val domain: String?) :
        GetSettingRequestBody() {
        override val requestBody = GetSettingRequest.newBuilder()
            .setSetting(Setting.ANY)
            .setName(name)
            .setDomain(domain)
            .build()
    }
}
