package io.github.sergkhram.idbClient.requests.app

import idb.LaunchResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.entities.requestsBody.app.LaunchRequestBody
import io.github.sergkhram.idbClient.requests.PredicateIdbRequest
import io.github.sergkhram.idbClient.util.JsonUtil.convertModelToString
import kotlinx.coroutines.flow.asFlow
import java.time.Duration

class LaunchRequest(
    private val launch: LaunchRequestBody,
    predicate: () -> Boolean,
    timeout: Duration = Duration.ofSeconds(10L)
): PredicateIdbRequest<List<LaunchResponse>>(predicate, timeout) {
    override suspend fun execute(client: GrpcClient): List<LaunchResponse> {
        val response = client.stub.launch(
            listOf(launch.requestBody).asFlow()
        )
        val listOfLaunchResponse = mutableListOf<LaunchResponse>()
        response.takeWhileCondition {
            val output = it.output
            val data = output.data
            if(data.any()) {
                if(output.`interface`.number==0) log.info(data.toStringUtf8())
                else log.error(data.toStringUtf8())
            }
            log.debug {
                convertModelToString(it.debugger)
            }
            listOfLaunchResponse.add(it)
        }
        client.stub.launch(
            listOf(LaunchRequestBody.StopLaunchRequestBody().requestBody).asFlow()
        )
        return listOfLaunchResponse
    }

}