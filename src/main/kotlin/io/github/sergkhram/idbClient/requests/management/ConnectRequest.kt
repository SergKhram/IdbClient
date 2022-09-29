package io.github.sergkhram.idbClient.requests.management

import idb.ConnectResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.management.ConnectRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest

class ConnectRequest(
    private val requestBody: ConnectRequestBody = ConnectRequestBody()
): IdbRequest<ConnectResponse>() {
    override suspend fun execute(client: GrpcClient): ConnectResponse {
        return client.stub.connect(
            idb.ConnectRequest.newBuilder().setLocalFilePath(requestBody.localeFilePath).build()
        )
    }
}