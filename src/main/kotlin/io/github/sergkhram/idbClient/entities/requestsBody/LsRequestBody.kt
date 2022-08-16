package io.github.sergkhram.idbClient.entities.requestsBody

import idb.LsRequest

sealed class LsRequestBody {
    abstract val requestBody: idb.LsRequest
    data class MultipleLsRequestBody(val container: FileContainer, val paths: List<String>) : LsRequestBody(){
        override val requestBody = LsRequest.newBuilder().setContainer(
            container.toFileContainerProto()
        ).addAllPaths(paths).build()
    }

    data class SingleLsRequestBody(val container: FileContainer, val path: String) : LsRequestBody(){
        override val requestBody = LsRequest.newBuilder().setContainer(
            container.toFileContainerProto()
        ).setPath(path).build()
    }
}