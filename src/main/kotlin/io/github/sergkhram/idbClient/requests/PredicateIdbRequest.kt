package io.github.sergkhram.idbClient.requests

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Duration

abstract class PredicateIdbRequest<T : Any?>(private val predicate: () -> Boolean, private val timeout: Duration) : IdbRequest<T>() {
    suspend fun <I> Flow<I>.takeWhileCondition(action: (I) -> Unit) {
        val flow = this
        withTimeoutOrNull(timeout.toMillis()) {
            flow.takeWhile { !predicate.invoke() }
                .collect {
                    action.invoke(it)
                }
        }
    }
}
