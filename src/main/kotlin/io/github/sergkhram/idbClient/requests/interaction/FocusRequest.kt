package io.github.sergkhram.idbClient.requests.interaction

import idb.FocusResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class FocusRequest: IdbRequest<FocusResponse>() {
    override suspend fun execute(client: GrpcClient): FocusResponse {
        return client.stub.focus(
            idb.FocusRequest.getDefaultInstance()
        )
    }
}