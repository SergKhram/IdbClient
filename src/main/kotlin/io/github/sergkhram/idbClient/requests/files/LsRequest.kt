package io.github.sergkhram.idbClient.requests.files

import idb.LsResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.LsRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

/**
 * List a path inside an application's container
 */
class LsRequest(
    private val lsRequestBody: LsRequestBody
): IdbRequest<LsResponse>() {
    override suspend fun execute(client: GrpcClient): LsResponse {
        return client.stub.ls(
            lsRequestBody.requestBody
        )
    }
}