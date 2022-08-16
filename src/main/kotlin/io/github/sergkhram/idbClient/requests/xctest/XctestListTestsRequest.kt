package io.github.sergkhram.idbClient.requests.xctest

import idb.XctestListTestsResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class XctestListTestsRequest(private val appPath: String, private val bundleName: String): IdbRequest<XctestListTestsResponse>() {
    override suspend fun execute(client: GrpcClient): XctestListTestsResponse {
        return client.stub.xctestListTests(
            idb.XctestListTestsRequest.newBuilder()
                .setAppPath(appPath)
                .setBundleName(bundleName)
                .build()
        )
    }
}