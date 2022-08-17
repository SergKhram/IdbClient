package io.github.sergkhram.idbClient.requests.files

import idb.PullResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.toFileContainerProto
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.flow.Flow

class PullRequest(private val srcPath: String, private val destPath: String, private val container: FileContainer): AsyncIdbRequest<Flow<PullResponse>>() {
    override suspend fun execute(client: GrpcClient): Flow<PullResponse> {
        return client.stub.pull(
            idb.PullRequest.newBuilder()
                .setContainer(
                    container.toFileContainerProto()
                )
                .setDstPath(destPath)
                .setSrcPath(srcPath)
                .build()
        )
    }
}