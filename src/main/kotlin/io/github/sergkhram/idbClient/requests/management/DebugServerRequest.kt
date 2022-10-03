package io.github.sergkhram.idbClient.requests.management

import idb.DebugServerResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.management.DebugServerRequestBody
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DebugServerRequest(private val debugRequestBodyObject: DebugServerRequestBody): AsyncIdbRequest<Flow<DebugServerResponse>>() {
    override suspend fun execute(client: GrpcClient): Flow<DebugServerResponse> {
        return client.stub.debugserver(
            flow {
                emit(debugRequestBodyObject.requestBody)
            }
        )
    }
}