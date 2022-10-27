package io.github.sergkhram.idbClient.handlers

import io.github.sergkhram.idbClient.logs.KLogger
import io.grpc.StatusException
import kotlin.reflect.KClass

object GrpcErrorHandler {
    private val log = KLogger.logger

    internal suspend fun <T, E: Throwable> handle(
        actionAfterCatch: () -> Unit,
        predicate: () -> Boolean,
        exception: KClass<E>,
        block: suspend () -> T
    ): T {
        return try {
            block.invoke()
        } catch (e: StatusException) {
            if(exception.isInstance(e.status.cause)
                && predicate.invoke()
            ) {
                log.warn("${exception.simpleName} is caught - retrying execute request")
                actionAfterCatch.invoke()
                block.invoke()
            } else {
                throw e
            }
        }
    }
}