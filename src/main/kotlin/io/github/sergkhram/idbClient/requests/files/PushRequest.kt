package io.github.sergkhram.idbClient.requests.files

import idb.PushResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.PayloadRequestBody
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.util.compress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.io.path.deleteIfExists


class PushRequest(
    private val dstPath: String,
    private val srcPath: String,
    val container: FileContainer
): IdbRequest<PushResponse>(){
    override suspend fun execute(client: GrpcClient): PushResponse {
        val zipPath = compress(srcPath)
        val listOfRequests = listOf(
            idb.PushRequest.newBuilder()
                .setInner(
                    idb.PushRequest.Inner.newBuilder()
                        .setDstPath(dstPath)
                        .setContainer(
                            container.toFileContainerProto()
                        )
                )
                .build(),
            idb.PushRequest.newBuilder()
                .setPayload(
                    PayloadRequestBody.DataPayload(zipPath.toFile()).requestBody
                )
                .build()
        )
        zipPath.deleteIfExists()

        val requestFlow = flow {
            listOfRequests.forEach {
                log.info { "Sending: $it" }
                emit(it)
                delay(timeMillis = 100L)
            }
        }

        return client.stub.push(
            requestFlow
        )
    }
}