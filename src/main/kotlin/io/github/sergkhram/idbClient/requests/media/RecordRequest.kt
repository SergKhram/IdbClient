package io.github.sergkhram.idbClient.requests.media

import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.response.VideoResponse
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.util.unpackBytes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Duration


/**
 * Record the target's screen to a mp4 video file and return byte array
 */
class RecordRequest(
    private val predicate: () -> Boolean,
    val timeout: Duration = Duration.ofSeconds(10L)
): IdbRequest<VideoResponse>() {
    override suspend fun execute(client: GrpcClient): VideoResponse {
        val listOfRequests = listOf(
            idb.RecordRequest.newBuilder()
                .setStart(
                    idb.RecordRequest.Start.getDefaultInstance()
                )
                .build(),
            idb.RecordRequest.newBuilder()
                .setStop(
                    idb.RecordRequest.Stop.getDefaultInstance()
                )
                .build()
        )
        val requestFlow = flow {
            listOfRequests.forEach {
                if(it.hasStop()) {
                    withTimeoutOrNull(timeout.toMillis()) {
                        while(!predicate.invoke()) {
                            delay(timeMillis = 100L)
                        }
                    }
                }
                log.info { "Sending: $it" }
                emit(it)
            }
        }
        val response = client.stub.record(
            requestFlow
        )
        var data: ByteArray = byteArrayOf()
        response.catch{ log.error { it.message } }.collect{
            data += it.payload.data.toByteArray()
        }

        return VideoResponse(
            unpackBytes(data)
        )
    }
}