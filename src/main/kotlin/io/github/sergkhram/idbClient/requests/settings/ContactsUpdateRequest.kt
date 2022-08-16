package io.github.sergkhram.idbClient.requests.settings

import idb.ContactsUpdateRequest
import idb.ContactsUpdateResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.PayloadRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest
import java.io.File

class ContactsUpdateRequest(private val contactsDbFile: File): IdbRequest<ContactsUpdateResponse>() {
    override suspend fun execute(client: GrpcClient): ContactsUpdateResponse {
        return client.stub.contactsUpdate(
            ContactsUpdateRequest.newBuilder().setPayload(
                PayloadRequestBody.UpdateContactsPayload(contactsDbFile).requestBody
            ).build()
        )
    }
}