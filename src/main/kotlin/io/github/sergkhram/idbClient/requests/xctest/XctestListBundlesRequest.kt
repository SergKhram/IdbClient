package io.github.sergkhram.idbClient.requests.xctest

import idb.XctestListBundlesResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

/**
 * List the installed test bundles
 */
class XctestListBundlesRequest: IdbRequest<XctestListBundlesResponse>() {
    override suspend fun execute(client: GrpcClient): XctestListBundlesResponse {
        return client.stub.xctestListBundles(
            idb.XctestListBundlesRequest.getDefaultInstance()
        )
    }
}