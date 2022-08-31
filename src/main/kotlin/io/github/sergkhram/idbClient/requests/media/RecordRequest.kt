package io.github.sergkhram.idbClient.requests.media

import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest
import io.github.sergkhram.idbClient.util.unpackGzip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.commons.io.FileUtils
import java.time.Duration
import kotlin.io.path.deleteIfExists


/**
 * Record the target's screen to a mp4 video file and return byte array
 */
class RecordRequest(
    private val predicate: () -> Boolean,
    val timeout: Duration = Duration.ofSeconds(10L),
): IdbRequest<ByteArray>() {
    override suspend fun execute(client: GrpcClient): ByteArray {
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

        val gzipFile = kotlin.io.path.createTempFile(suffix = ".gz")
        FileUtils.writeByteArrayToFile(gzipFile.toFile(), data)
        val bytes = unpackGzip(gzipFile.toFile())
        gzipFile.deleteIfExists()

        return bytes
    }
}