package io.github.sergkhram.idbClient.entities.requestsBody

import com.google.protobuf.ByteString
import idb.Payload
import org.apache.commons.io.FileUtils
import java.io.File

sealed class PayloadRequestBody {
    abstract val requestBody: Payload

    data class UpdateContactsPayload(val contactsDbFile: File): PayloadRequestBody() {
        override val requestBody: Payload = Payload.newBuilder().setData(
            ByteString.copyFrom(
                FileUtils.readFileToByteArray(contactsDbFile)
            )
        ).build()
    }
}