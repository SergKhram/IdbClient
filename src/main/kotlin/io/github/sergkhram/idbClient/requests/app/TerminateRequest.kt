package io.github.sergkhram.idbClient.requests.app

import idb.TerminateResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class TerminateRequest(private val bundleId: String): IdbRequest<TerminateResponse>() {
    override suspend fun execute(client: GrpcClient): TerminateResponse {
        return client.stub.terminate(
            idb.TerminateRequest.newBuilder().setBundleId(bundleId).build()
        )
    }
}