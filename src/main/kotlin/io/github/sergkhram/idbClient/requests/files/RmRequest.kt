package io.github.sergkhram.idbClient.requests.files

import idb.RmResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest

/**
 * Remove an item inside a container
 * @param paths Path of item to remove (A directory will be recursively deleted)
 * @param container File container(Default is ROOT)
 */
class RmRequest(
    private val paths: List<String>,
    private val container: FileContainer = FileContainer()
): IdbRequest<RmResponse>() {
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