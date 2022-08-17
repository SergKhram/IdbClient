package io.github.sergkhram.idbClient.requests.management

import idb.LogRequest
import idb.LogRequest.Source
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import java.time.Duration

class LogRequest(
    private val arguments: List<String> = emptyList(),
    predicate: () -> Boolean,
    timeout: Duration = Duration.ofSeconds(10L)
) : PredicateIdbRequest<List<String>>(predicate, timeout) {
    override suspend fun execute(client: GrpcClient): List<String> {
        val response = client.stub.log(
            LogRequest.newBuilder().setSourceValue(Source.COMPANION_VALUE).addAllArguments(arguments).build()
        )
        val logs = mutableListOf<String>()
        response.takeWhileCondition {
            logs.add(it.output.toStringUtf8())
        }
        return logs
    }
}