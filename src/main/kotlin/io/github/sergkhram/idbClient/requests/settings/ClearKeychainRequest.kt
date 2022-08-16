package io.github.sergkhram.idbClient.requests.settings

import idb.ClearKeychainRequest
import idb.ClearKeychainResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class ClearKeychainRequest: IdbRequest<ClearKeychainResponse>() {
    override suspend fun execute(client: GrpcClient): ClearKeychainResponse {
        return client.stub.clearKeychain(
            ClearKeychainRequest.getDefaultInstance()
        )
    }
}