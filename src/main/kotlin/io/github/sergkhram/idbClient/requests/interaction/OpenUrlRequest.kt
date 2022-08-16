package io.github.sergkhram.idbClient.requests.interaction

import idb.OpenUrlRequest as OpenUrlRequestProto
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class OpenUrlRequest(private val url: String): IdbRequest<OpenUrlRequestProto>() {
    override suspend fun execute(client: GrpcClient): OpenUrlRequestProto {
        return client.stub.openUrl(
            OpenUrlRequestProto.newBuilder().setUrl(url).build()
        )
    }
}