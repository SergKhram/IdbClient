package io.github.sergkhram.idbClient.requests.xctest

import idb.InstallRequest
import idb.InstallResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.PayloadRequestBody
import io.github.sergkhram.idbClient.requests.AsyncIdbRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * Install a xctest
 * @param testBundlePath - Bundle path of the test bundle
 */
class XctestInstallRequest(
    private val testBundlePath: String
): AsyncIdbRequest<Flow<InstallResponse>>() {
    override suspend fun execute(client: GrpcClient): Flow<InstallResponse> {
        val listOfRequests = mutableListOf(
            InstallRequest.newBuilder()
                .setDestination(
                    InstallRequest.Destination.XCTEST
                )
                .build(),
            InstallRequest.newBuilder()
                .setPayload(
                    PayloadRequestBody.DataPayload(
                        File(testBundlePath)
                    ).requestBody
                )
                .build()
        )
        val requestFlow = flow {
            listOfRequests.forEach {
                log.info { "Sending: $it" }
                emit(it)
                delay(timeMillis = 100L)
            }
        }
        return client.stub.install(
            requestFlow
        ).catch{ log.error { it.message } }
    }
}