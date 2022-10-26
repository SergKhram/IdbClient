package io.github.sergkhram.idbClient.entities.requestsBody.settings

import idb.Setting
import idb.SettingRequest

/**
 * Sets a preference
 */
sealed class SettingRequestBody {
    abstract val requestBody: SettingRequest

    /**
     * @param enabled activate/deactivate HardwareKeyboard setting
     */
    class HardwareKeyboardSetting(enabled: Boolean) : SettingRequestBody() {
        override val requestBody: SettingRequest = SettingRequest.newBuilder()
            .setHardwareKeyboard(
                SettingRequest.HardwareKeyboard.newBuilder()
                    .setEnabled(enabled)
                    .build()
            )
            .build()
    }

    /**
     * @param localeIdentifier Preference value
     */
    class LocaleSetting(localeIdentifier: String) : SettingRequestBody() {
        override val requestBody: SettingRequest = SettingRequest.newBuilder()
            .setStringSetting(
                SettingRequest.StringSetting.newBuilder()
                    .setSetting(Setting.LOCALE)
                    .setValue(localeIdentifier)
                    .build()
            ).build()
    }

    /**
     * @param name Preference name
     * @param value Preference value
     * @param valueType Specifies the type of the value to be set, for supported types see 'defaults get help' defaults to string.
     * Example of usage: idb set --domain com.apple.suggestions.plist SuggestionsAppLibraryEnabled --type bool true
     * @param domain Preference domain, assumed to be Apple Global Domain if not specified
     */
    class AnySetting(name: String, value: String, valueType: String, domain: String = "") :
        SettingRequestBody() {
        override val requestBody: SettingRequest = SettingRequest.newBuilder()
            .setStringSetting(
                SettingRequest.StringSetting.newBuilder()
                    .setSetting(Setting.ANY)
                    .setName(name)
                    .setValue(value)
                    .setValueType(valueType)
                    .setDomain(domain)
                    .build()
            ).build()
    }
}
