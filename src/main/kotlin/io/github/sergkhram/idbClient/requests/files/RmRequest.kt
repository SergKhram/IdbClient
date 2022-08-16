package io.github.sergkhram.idbClient.requests.files

import idb.RmResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest

class RmRequest(private val paths: List<String>, private val container: FileContainer): IdbRequest<RmResponse>() {
    override suspend fun execute(client: GrpcClient): RmResponse {
        return client.stub.rm(
            idb.RmRequest.newBuilder()
                .setContainer(
                    container.toFileContainerProto()
                )
                .addAllPaths(paths)
                .build()
        )
    }
}