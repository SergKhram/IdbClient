package io.github.sergkhram.idbClient.requests.files

import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.files.FileContainer
import io.github.sergkhram.idbClient.entities.requestsBody.files.toFileContainerProto
import io.github.sergkhram.idbClient.requests.IdbRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

class TailRequest(
    val container: FileContainer,
    val path: String,
    private val predicate: () -> Boolean,
    val timeout: Duration = Duration.ofSeconds(10L)
): IdbRequest<String>() {
    override suspend fun execute(client: GrpcClient): String {
        val listOfRequests = listOf(
            idb.TailRequest.newBuilder()
                .setStart(
                    idb.TailRequest.Start.newBuilder()
                        .setContainer(container.toFileContainerProto())
                        .setPath(path)
                        .build()
                )
                .build(),
            idb.TailRequest.newBuilder()
                .setStop(
                    idb.TailRequest.Stop.getDefaultInstance()
                )
                .build()
        )
        val needToWait = AtomicBoolean(true)
        val flowOfRequests = flow {
            listOfRequests.forEach {
                if(it.hasStop()) {
                    withTimeoutOrNull(timeout.toMillis()) {
                        while(!predicate.invoke()) {
                            delay(timeMillis = 100L)
                        }
                    }
                    needToWait.getAndSet(false)
                }
                log.info { "Sending $it" }
                emit(it)
            }
        }
        val response = client.stub.tail(
            flowOfRequests
        )
        val data = StringBuilder()
        response.catch{ log.error { it.message } }.takeWhile { needToWait.get() }.collect{
            data.append(it.data.toStringUtf8())
        }
        return data.toString()
    }
}