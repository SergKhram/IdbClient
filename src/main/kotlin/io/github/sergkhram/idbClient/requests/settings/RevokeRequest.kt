package io.github.sergkhram.idbClient.requests.settings

import idb.RevokeResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.Permission
import io.github.sergkhram.idbClient.requests.IdbRequest

class RevokeRequest(
    private val bundleId: String = "", private val permissions: List<Permission> = emptyList(), private val scheme: String = ""
): IdbRequest<RevokeResponse>() {
    override suspend fun execute(client: GrpcClient): RevokeResponse {
        return client.stub.revoke(
            idb.RevokeRequest.newBuilder().setBundleId(bundleId).addAllPermissionsValue(permissions.map { it.value }).setScheme(scheme).build()
        )
    }
}