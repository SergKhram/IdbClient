package io.github.sergkhram.idbClient.requests.files

import idb.MvRequest
import idb.MvResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest

class MvRequest(private val srcPaths: List<String>, private val destPath: String, private val container: FileContainer): IdbRequest<MvResponse>() {
    override suspend fun execute(client: GrpcClient): MvResponse {
        return client.stub.mv(
            MvRequest.newBuilder()
                .setContainer(
                    container.toFileContainerProto()
                )
                .setDstPath(destPath)
                .addAllSrcPaths(srcPaths)
                .build()
        )
    }
}