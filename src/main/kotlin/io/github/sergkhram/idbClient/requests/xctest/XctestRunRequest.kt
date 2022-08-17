package io.github.sergkhram.idbClient.requests.xctest

import idb.XctestRunResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.XctestRunRequestBody
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.flow.Flow

class XctestRunRequest(private val xctestRunRequest: XctestRunRequestBody): AsyncIdbRequest<Flow<XctestRunResponse>>() {
    override suspend fun execute(client: GrpcClient): Flow<XctestRunResponse> {
        return client.stub.xctestRun(
            xctestRunRequest.requestBody
        )
    }
}