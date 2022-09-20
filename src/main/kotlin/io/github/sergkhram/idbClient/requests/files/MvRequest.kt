package io.github.sergkhram.idbClient.requests.files

import idb.MvRequest
import idb.MvResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest

/**
 * Move a path inside an application's container
 * @param srcPaths - Source paths relative to Container
 * @param dstPath - Destination path relative to Container
 * @param container - File container(Default is ROOT)
 */
class MvRequest(
    private val srcPaths: List<String>,
    private val dstPath: String,
    private val container: FileContainer = FileContainer()
): IdbRequest<MvResponse>() {
    override suspend fun execute(client: GrpcClient): MvResponse {
        return client.stub.mv(
            MvRequest.newBuilder()
                .setContainer(
                    container.toFileContainerProto()
                )
                .setDstPath(dstPath)
                .addAllSrcPaths(srcPaths)
                .build()
        )
    }
}