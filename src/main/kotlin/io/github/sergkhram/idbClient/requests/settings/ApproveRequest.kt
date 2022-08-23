package io.github.sergkhram.idbClient.requests.settings

import idb.ApproveRequest
import idb.ApproveResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.settings.Permission
import io.github.sergkhram.idbClient.requests.IdbRequest

class ApproveRequest(
    private val bundleId: String = "", private val permissions: List<Permission> = emptyList(), private val scheme: String = ""
): IdbRequest<ApproveResponse>() {
    override suspend fun execute(client: GrpcClient): ApproveResponse {
        return client.stub.approve(
            ApproveRequest.newBuilder().setBundleId(bundleId).addAllPermissionsValue(permissions.map { it.value }).setScheme(scheme).build()
        )
    }
}