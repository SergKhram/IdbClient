package io.github.sergkhram.idbClient.entities.requestsBody.management

import idb.DebugServerRequest

sealed class DebugServerRequestBody {
    abstract val requestBody: DebugServerRequest

    /**
     * Start the Debug Server
     * @param bundleId - The bundle id to debug
     */
    data class DebugServerStartRequest(val bundleId: String) : DebugServerRequestBody() {
        override val requestBody: DebugServerRequest = DebugServerRequest.newBuilder()
            .setStart(DebugServerRequest.Start.newBuilder().setBundleId(bundleId).build()).build()
    }

    /**
     * Stop the debug server
     */
    class DebugServerStopRequest : DebugServerRequestBody() {
        override val requestBody: DebugServerRequest = DebugServerRequest.newBuilder()
            .setStop(DebugServerRequest.Stop.getDefaultInstance()).build()
    }

    /**
     * Get the status of the debug server
     */
    class DebugServerStatusRequest : DebugServerRequestBody() {
        override val requestBody: DebugServerRequest = DebugServerRequest.newBuilder()
            .setStatus(DebugServerRequest.Status.getDefaultInstance()).build()
    }
}
