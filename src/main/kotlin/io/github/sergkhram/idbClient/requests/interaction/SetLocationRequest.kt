package io.github.sergkhram.idbClient.requests.interaction

import idb.Location
import idb.SetLocationResponse
import io.github.sergkhram.idbClient.entities.GrpcClient
import io.github.sergkhram.idbClient.requests.IdbRequest

class SetLocationRequest(private val latitude: Double, private val longitude: Double): IdbRequest<SetLocationResponse>() {
    override suspend fun execute(client: GrpcClient): SetLocationResponse {
        return client.stub.setLocation(
            idb.SetLocationRequest.newBuilder().setLocation(
                Location.newBuilder()
                    .setLatitude(latitude)
                    .setLongitude(longitude)
                    .build()
            ).build()
        )
    }
}