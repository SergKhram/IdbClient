package io.github.sergkhram.idbClient.requests.app

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
 * Install an application
 * @param bundlePath - Path to the .app/.ipa to install. Note that .app bundles will usually be faster to install than
 * .ipa files.
 * @param makeDebuggable - If set, will persist the application bundle alongside the iOS Target, this is needed for
 * debug server commands to function
 * @param overrideMTime - If set, idb will disregard the mtime of files contained in an .ipa file. Current timestamp
 * will be used as modification time. Use this flag to ensure app updates work properly when your build system
 * normalises the timestamps of contents of archives.
 */
class InstallRequest(
    private val bundlePath: String,
    private val makeDebuggable: Boolean = false,
    private val overrideMTime: Boolean = false
): AsyncIdbRequest<Flow<InstallResponse>>() {
    override suspend fun execute(client: GrpcClient): Flow<InstallResponse> {
        val listOfRequests = mutableListOf(
            InstallRequest.newBuilder()
                .setDestination(InstallRequest.Destination.APP)
                .build()
        )
        if(makeDebuggable) {
            listOfRequests.add(
                InstallRequest.newBuilder().setMakeDebuggable(true).build()
            )
        }
        if(overrideMTime) {
            listOfRequests.add(
                InstallRequest.newBuilder().setOverrideModificationTime(true).build()
            )
        }
        listOfRequests.add(
            InstallRequest.newBuilder()
                .setPayload(
                    if(bundlePath.contains(".ipa"))
                        PayloadRequestBody.IpaPayload(File(bundlePath)).requestBody
                    else
                        PayloadRequestBody.AppPayload(File(bundlePath)).requestBody
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