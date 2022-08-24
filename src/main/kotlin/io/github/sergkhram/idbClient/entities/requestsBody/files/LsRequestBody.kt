package io.github.sergkhram.idbClient.entities.requestsBody.files

import idb.LsRequest

sealed class LsRequestBody {
    abstract val requestBody: LsRequest

    /**
     * List a path inside an application's container with multiple paths
     * @param paths - List of source paths
     * @param container - File container
     */
    data class MultipleLsRequestBody(val container: FileContainer, val paths: List<String>) : LsRequestBody(){
        override val requestBody: LsRequest = LsRequest.newBuilder().setContainer(
            container.toFileContainerProto()
        ).addAllPaths(paths).build()
    }

    /**
     * List a path inside an application's container with single path
     * @param path - Source path
     * @param container - File container
     */
    data class SingleLsRequestBody(val container: FileContainer, val path: String) : LsRequestBody(){
        override val requestBody: LsRequest = LsRequest.newBuilder().setContainer(
            container.toFileContainerProto()
        ).setPath(path).build()
    }
}