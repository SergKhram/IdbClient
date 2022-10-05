package io.github.sergkhram.idbClient.entities

import io.grpc.ManagedChannel

class CompanionData(val channelBuilder: () -> Pair<ManagedChannel, ProcessBuilder?>, val isLocal: Boolean = false, val address: Address? = null)