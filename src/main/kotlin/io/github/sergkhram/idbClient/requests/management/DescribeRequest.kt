package io.github.sergkhram.idbClient.requests.management

import idb.TargetDescriptionRequest
import idb.TargetDescriptionResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.management.TargetDescriptionRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class DescribeRequest(
    private val requestBody: TargetDescriptionRequestBody = TargetDescriptionRequestBody(false)
) : IdbRequest<TargetDescriptionResponse>() {
    override suspend fun execute(client: GrpcClient): TargetDescriptionResponse {
        return client.stub.describe(
            TargetDescriptionRequest.newBuilder().setFetchDiagnostics(requestBody.fetchDiagnostics).build()
        )
    }
}