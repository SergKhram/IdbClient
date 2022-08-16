package io.github.sergkhram.idbClient.requests.management

import idb.LogRequest
import idb.LogRequest.Source
import idb.LogResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest
import kotlinx.coroutines.flow.Flow

class LogRequest(private val arguments: List<String> = emptyList()) : IdbRequest<Flow<LogResponse>>() {
    override suspend fun execute(client: GrpcClient): Flow<LogResponse> {
        return client.stub.log(
            LogRequest.newBuilder().setSourceValue(Source.COMPANION_VALUE).addAllArguments(arguments).build()
        )
    }
}