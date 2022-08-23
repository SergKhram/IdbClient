package io.github.sergkhram.idbClient.entities.requestsBody.settings

import idb.Setting
import idb.SettingRequest

sealed class SettingRequestBody {
    abstract val requestBody: SettingRequest

    data class HardwareKeyboardSetting(val enabled: Boolean) : SettingRequestBody() {
        override val requestBody: SettingRequest = SettingRequest.newBuilder()
            .setHardwareKeyboard(SettingRequest.HardwareKeyboard.newBuilder().setEnabled(enabled).build()).build()
    }

    data class LocaleSetting(val localeIdentifier: String) : SettingRequestBody() {
        override val requestBody: SettingRequest = SettingRequest.newBuilder().setStringSetting(
            SettingRequest.StringSetting.newBuilder().setSetting(Setting.LOCALE).setValue(localeIdentifier).build()
        ).build()
    }

    data class AnySetting(val name: String, val value: String, val valueType: String, val domain: String?) :
        SettingRequestBody() {
        override val requestBody: SettingRequest = SettingRequest.newBuilder().setStringSetting(
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
