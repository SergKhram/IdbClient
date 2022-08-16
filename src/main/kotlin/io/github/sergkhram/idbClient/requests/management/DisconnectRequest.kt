package io.github.sergkhram.idbClient.requests.management

import io.github.sergkhram.idbClient.IOSDebugBridgeClient
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class DisconnectRequest(private val udid: String) : IdbRequest<Unit>() {
    override suspend fun execute(client: GrpcClient) {
        IOSDebugBridgeClient.clients[udid]?.grpcClient?.close()
        IOSDebugBridgeClient.clients.remove(udid)
    }
}