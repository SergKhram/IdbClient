package io.github.sergkhram.idbClient.entities.requestsBody.settings

import idb.GetSettingRequest
import idb.Setting

sealed class GetSettingRequestBody {
    abstract val requestBody: GetSettingRequest

    /**
     * Gets a local preference value
     */
    class LocaleSetting : GetSettingRequestBody() {
        override val requestBody: GetSettingRequest = GetSettingRequest.newBuilder().setSetting(Setting.LOCALE).build()
    }

    /**
     * Gets a preference value by name
     * @param name - Preference name
     * @param domain - Preference domain, assumed to be Apple Global Domain if not specified
     */
    data class AnySetting(val name: String, val domain: String = "") :
        GetSettingRequestBody() {
        override val requestBody: GetSettingRequest = GetSettingRequest.newBuilder()
            .setSetting(Setting.ANY)
            .setName(name)
            .setDomain(domain)
            .build()
    }
}
