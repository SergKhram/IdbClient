package io.github.sergkhram.idbClient.requests.media

import idb.AddMediaResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.PayloadRequestBody
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.util.compress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.io.path.deleteIfExists

/**
 * Add photos/videos to the target
 * @param filePaths - Paths to all media files to add
 */
class AddMediaRequest(private val filePaths: List<String>): IdbRequest<AddMediaResponse>(){
    override suspend fun execute(client: GrpcClient): AddMediaResponse {
        val listOfRequests = filePaths.map {
            val zipPath = compress(it)
            val request = idb.AddMediaRequest.newBuilder()
                .setPayload(
                    PayloadRequestBody.DataPayload(zipPath.toFile()).requestBody
                )
                .build()
            zipPath.deleteIfExists()
            request
        }

        val requestFlow = flow {
            listOfRequests.forEach {
                log.info { "Sending: $it" }
                emit(it)
                delay(timeMillis = 100L)
            }
        }

        return client.stub.addMedia(
            requestFlow
        )
    }
}