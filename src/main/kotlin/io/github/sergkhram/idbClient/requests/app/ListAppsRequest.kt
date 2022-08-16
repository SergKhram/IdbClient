package io.github.sergkhram.idbClient.requests.app

import idb.ListAppsResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class ListAppsRequest(private val suppressProcessState: Boolean = false): IdbRequest<ListAppsResponse>() {
    override suspend fun execute(client: GrpcClient): ListAppsResponse {
        return client.stub.listApps(
            idb.ListAppsRequest.newBuilder().setSuppressProcessState(suppressProcessState).build()
        )
    }
}