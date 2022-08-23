package io.github.sergkhram.idbClient.requests.crash

import idb.CrashLogResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.crash.CrashLogQueryRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class CrashListRequest(private val requestBody: CrashLogQueryRequestBody): IdbRequest<CrashLogResponse>() {
    override suspend fun execute(client: GrpcClient): CrashLogResponse {
        return client.stub.crashList(
            idb.CrashLogQuery.newBuilder()
                .setBefore(requestBody.before)
                .setBundleId(requestBody.bundleId)
                .setName(requestBody.name)
                .setSince(requestBody.since)
                .build()
        )
    }
}