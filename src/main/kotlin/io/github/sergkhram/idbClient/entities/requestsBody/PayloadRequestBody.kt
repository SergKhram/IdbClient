package io.github.sergkhram.idbClient.entities.requestsBody

import com.google.protobuf.ByteString
import idb.Payload
import io.github.sergkhram.idbClient.util.compress
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.io.path.deleteIfExists

sealed class PayloadRequestBody {
    abstract val requestBody: Payload

    class UpdateContactsPayload(contactsDbFile: File): PayloadRequestBody() {
        override val requestBody: Payload = buildPayloadObject(contactsDbFile)
    }

    class DataPayload(file: File): PayloadRequestBody() {
        override val requestBody: Payload = buildPayloadObject(file)
    }

    class IpaPayload(file: File): PayloadRequestBody() {
        override val requestBody: Payload = buildPayloadObject(file)
    }

    class AppPayload(file: File): PayloadRequestBody() {
        override val requestBody: Payload = prepareAppPayload(file)

        private fun prepareAppPayload(file: File): Payload {
            val zipPath = compress(file.path)
            val payload = buildPayloadObject(zipPath.toFile())
            zipPath.deleteIfExists()
            return payload
        }
    }

    protected fun buildPayloadObject(file: File): Payload = Payload.newBuilder()
        .setData(
            ByteString.copyFrom(
                FileUtils.readFileToByteArray(file)
            )
        )
        .build()
}