package io.github.sergkhram.idbClient.requests.media

import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.media.VideoFormat
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Duration

/**
 * Stream raw H264 from the target
 * @param fps - The frame rate of the stream. Default is a dynamic fps
 * @param videoFormat - The format of the stream
 * @param compressionQuality - The compression quality (between 0 and 1.0) for the stream
 * @param scaleFactor - The scale factor for the source video (between 0 and 1.0) for the stream
 */
class VideoStreamRequest(
    private val fps: Long,
    private val videoFormat: VideoFormat = VideoFormat.H264,
    private val compressionQuality: Double = 0.2,
    private val scaleFactor: Double = 1.0,
    private val predicate: () -> Boolean,
    val timeout: Duration = Duration.ofSeconds(10L)
): AsyncIdbRequest<Flow<ByteArray>>() {
    override suspend fun execute(client: GrpcClient): Flow<ByteArray> {
        val listOfRequests = listOf(
            idb.VideoStreamRequest.newBuilder()
                .setStart(
                    idb.VideoStreamRequest.Start.newBuilder()
                        .setFps(fps)
                        .setFormatValue(videoFormat.value)
                        .setCompressionQuality(compressionQuality)
                        .setScaleFactor(scaleFactor)
                        .build()
                )
                .build(),
            idb.VideoStreamRequest.newBuilder()
                .setStop(
                    idb.VideoStreamRequest.Stop.getDefaultInstance()
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

        return client.stub.videoStream(
            requestFlow
        ).map {
            it.payload.data.toByteArray()
        }.catch{ log.error { it.message } }
    }
}