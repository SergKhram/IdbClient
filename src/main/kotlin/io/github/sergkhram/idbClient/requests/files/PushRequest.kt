package io.github.sergkhram.idbClient.requests.files

import com.google.protobuf.ByteString
import idb.Payload
import idb.PushResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import kotlin.io.path.deleteIfExists


class PushRequest(
    private val dstPath: String,
    private val srcPath: String,
    val container: FileContainer
): IdbRequest<PushResponse>(){
    override suspend fun execute(client: GrpcClient): PushResponse {
        val zipFile = kotlin.io.path.createTempFile(suffix = ".zip")
        val srcFile = File(srcPath)
        if(srcFile.isDirectory) ZipUtil.pack(srcFile, zipFile.toFile()) else ZipUtil.packEntry(srcFile, zipFile.toFile())
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
                    Payload.newBuilder()
                        .setData(
                            ByteString.copyFrom(
                                FileUtils.readFileToByteArray(zipFile.toFile())
                            )
                        )
                        .build()
                )
                .build()
        )
        zipFile.deleteIfExists()

        val flowOfRequests = flow {
            listOfRequests.forEach {
                log.info { "Sending $it" }
                emit(it)
                delay(timeMillis = 100L)
            }
        }

        return client.stub.push(
            flowOfRequests
        )
    }
}