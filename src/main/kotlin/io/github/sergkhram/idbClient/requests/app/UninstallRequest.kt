package io.github.sergkhram.idbClient.requests.app

import idb.UninstallResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class UninstallRequest(private val bundleId: String): IdbRequest<UninstallResponse>() {
    override suspend fun execute(client: GrpcClient): UninstallResponse {
        return client.stub.uninstall(
            idb.UninstallRequest.newBuilder().setBundleId(bundleId).build()
        )
    }
}