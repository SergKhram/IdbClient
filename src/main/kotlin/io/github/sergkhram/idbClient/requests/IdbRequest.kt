package io.github.sergkhram.idbClient.requests

 import io.github.sergkhram.idbClient.entities.GrpcClient
 import io.github.sergkhram.idbClient.logs.KLogger

abstract class IdbRequest<T : Any?> {
    companion object {
        internal val log = KLogger.logger
    }

    internal abstract suspend fun execute(client: GrpcClient): T
}
