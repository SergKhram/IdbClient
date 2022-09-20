package io.github.sergkhram.idbClient.requests.files

import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Copy a file inside an application's container
 * @param srcPath - Relative Container source path
 * @param container - File container(Default is ROOT)
 */
class PullRequest(
    private val srcPath: String,
    private val container: FileContainer = FileContainer()
): AsyncIdbRequest<Flow<ByteArray>>() {
    override suspend fun execute(client: GrpcClient): Flow<ByteArray> {
        return client.stub.pull(
            idb.PullRequest.newBuilder()
                .setContainer(
                    container.toFileContainerProto()
                )
                .setSrcPath(srcPath)
                .build()
        ).map { it.payload.data.toByteArray() }
    }
}