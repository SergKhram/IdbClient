package io.github.sergkhram.idbClient.requests.xctest

import idb.XctestListTestsResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

/**
 * List the tests inside an installed test bundle
 * @param appPath Path of the app of the test (needed for app tests)
 * @param bundleName Bundle id of the test bundle to list
 */
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