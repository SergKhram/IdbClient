package io.github.sergkhram.idbClient.requests.interaction

import idb.SimulateMemoryWarningRequest
import idb.SimulateMemoryWarningResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class SimulateMemoryWarningRequest: IdbRequest<SimulateMemoryWarningResponse>() {
    override suspend fun execute(client: GrpcClient): SimulateMemoryWarningResponse {
        return client.stub.simulateMemoryWarning(
            SimulateMemoryWarningRequest.getDefaultInstance()
        )
    }
}