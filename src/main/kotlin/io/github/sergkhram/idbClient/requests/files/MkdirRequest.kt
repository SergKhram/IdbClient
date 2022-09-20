package io.github.sergkhram.idbClient.requests.files

import idb.MkdirRequest
import idb.MkdirResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest

/**
 * Make a directory inside an application's container
 * @param path - Path to directory to create
 * @param container - File container(Default is ROOT)
 */
class MkdirRequest(
    private val path: String,
    private val container: FileContainer = FileContainer()
): IdbRequest<MkdirResponse>() {
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