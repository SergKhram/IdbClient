package io.github.sergkhram.idbClient.requests.management

import idb.DebugServerRequest
import idb.DebugServerResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.management.DebugServerRequestBody
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class DebugServerRequest(private val requestBody: Flow<DebugServerRequest>): AsyncIdbRequest<Flow<DebugServerResponse>>() {
    constructor(requestBody: List<DebugServerRequestBody>) : this(requestBody.map { it.requestBody }.asFlow())

    override suspend fun execute(client: GrpcClient): Flow<DebugServerResponse> {
        return client.stub.debugserver(
            requestBody
        )
    }
}