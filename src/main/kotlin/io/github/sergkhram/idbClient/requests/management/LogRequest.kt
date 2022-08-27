package io.github.sergkhram.idbClient.requests.management

import idb.LogRequest
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.management.LogSource
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.cancellable
import java.time.Duration

/**
 * Obtain logs from the target or the companion
 * @param source - TARGET or COMPANION
 * @param arguments - Possible arguments:
[system | process (pid|process) | parent (pid|process) ]
[ level default|info|debug][ predicate <predicate> ]
[ source ][ style (syslog|json) ]
[ timeout <num>[m|h|d] ][ type activity|log|trace ]
 */
class LogRequest(
    predicate: () -> Boolean,
    timeout: Duration = Duration.ofSeconds(10L),
    private val arguments: List<String> = emptyList(),
    private val source: LogSource = LogSource.TARGET
) : PredicateIdbRequest<List<String>>(predicate, timeout) {
    override suspend fun execute(client: GrpcClient): List<String> {
        val response = client.stub.log(
            LogRequest.newBuilder().setSourceValue(source.value).addAllArguments(arguments).build()
        )
        val logs = mutableListOf<String>()
        coroutineScope {
            val job = async {
                response.cancellable().takeWhileCondition {
                    it.output.toStringUtf8().takeIf { str -> !str.contains("Send frame of log") }?.let(logs::add)
                }
            }
            job.await()
            job.cancel()
        }
        return logs
    }
}