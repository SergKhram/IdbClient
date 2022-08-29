package io.github.sergkhram.idbClient.requests.media

import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.commons.io.FileUtils
import java.io.File
import java.time.Duration

/**
 * Record the target's screen to a mp4 video file
 * @param zipFile - zip file to output the video to (you should to unpack it to *.mp4)
 */
class RecordRequest(
    private val predicate: () -> Boolean,
    private val zipFile: File,
    val timeout: Duration = Duration.ofSeconds(10L),
): IdbRequest<File>() {
    override suspend fun execute(client: GrpcClient): File {
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
        FileUtils.writeByteArrayToFile(zipFile, data)
        return zipFile
    }
}