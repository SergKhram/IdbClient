package io.github.sergkhram.idbClient.requests.files

import idb.MkdirRequest
import idb.MkdirResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest

class MkdirRequest(private val path: String, private val container: FileContainer): IdbRequest<MkdirResponse>() {
    override suspend fun execute(client: GrpcClient): MkdirResponse {
        return client.stub.mkdir(
            MkdirRequest.newBuilder()
                .setContainer(
                    container.toFileContainerProto()
                )
                .setPath(path)
                .build()
        )
    }
}