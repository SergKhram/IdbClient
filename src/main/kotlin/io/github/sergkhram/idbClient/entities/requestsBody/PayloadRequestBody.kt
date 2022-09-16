package io.github.sergkhram.idbClient.entities.requestsBody

import com.google.protobuf.ByteString
import idb.Payload
import io.github.sergkhram.idbClient.util.compress
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.io.path.deleteIfExists

sealed class PayloadRequestBody {
    abstract val requestBody: Payload

    data class UpdateContactsPayload(val contactsDbFile: File): PayloadRequestBody() {
        override val requestBody: Payload = Payload.newBuilder().setData(
            ByteString.copyFrom(
                FileUtils.readFileToByteArray(contactsDbFile)
            )
        ).build()
    }

    class DataPayload(file: File): PayloadRequestBody() {
        override val requestBody: Payload = Payload.newBuilder()
            .setData(
                ByteString.copyFrom(
                    FileUtils.readFileToByteArray(file)
                )
            )
            .build()
    }

    class IpaPayload(file: File): PayloadRequestBody() {
        override val requestBody: Payload = Payload.newBuilder()
            .setData(
                ByteString.copyFrom(
                    FileUtils.readFileToByteArray(file)
                )
            )
            .build()
    }

    class AppPayload(file: File): PayloadRequestBody() {
        override val requestBody: Payload = preparePayload(file)

        private fun preparePayload(file: File): Payload {
            val zipPath = compress(file.path)
            val payload = Payload.newBuilder()
                .setData(
                    ByteString.copyFrom(
                        FileUtils.readFileToByteArray(zipPath.toFile())
                    )
                )
                .build()
            zipPath.deleteIfExists()
            return payload
        }
    }
}