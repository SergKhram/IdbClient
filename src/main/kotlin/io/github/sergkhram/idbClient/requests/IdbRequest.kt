package io.github.sergkhram.idbClient.requests

 import io.github.sergkhram.idbClient.entities.GrpcClient

abstract class IdbRequest<T : Any?> {
    abstract suspend fun execute(client: GrpcClient): T
}
