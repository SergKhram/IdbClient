package io.github.sergkhram.idbClient.requests.crash

import idb.CrashShowResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class CrashShowRequest(private val name: String): IdbRequest<CrashShowResponse>() {
    override suspend fun execute(client: GrpcClient): CrashShowResponse {
        return client.stub.crashShow(
            idb.CrashShowRequest.newBuilder().setName(name).build()
        )
    }
}