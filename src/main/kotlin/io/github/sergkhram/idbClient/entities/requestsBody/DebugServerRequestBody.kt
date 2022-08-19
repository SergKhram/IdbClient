package io.github.sergkhram.idbClient.entities.requestsBody

import idb.DebugServerRequest

sealed class DebugServerRequestBody {
    abstract val requestBody: DebugServerRequest
    data class DebugServerStartRequest(val bundleId: String) : DebugServerRequestBody() {
        override val requestBody: DebugServerRequest = DebugServerRequest.newBuilder()
            .setStart(DebugServerRequest.Start.newBuilder().setBundleId(bundleId).build()).build()
    }

    class DebugServerStopRequest : DebugServerRequestBody() {
        override val requestBody: DebugServerRequest = DebugServerRequest.newBuilder()
            .setStop(DebugServerRequest.Stop.getDefaultInstance()).build()
    }

    class DebugServerStatusRequest : DebugServerRequestBody() {
        override val requestBody: DebugServerRequest = DebugServerRequest.newBuilder()
            .setStatus(DebugServerRequest.Status.getDefaultInstance()).build()
    }
}
