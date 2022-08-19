package io.github.sergkhram.idbClient.entities

import io.grpc.ManagedChannel

class CompanionData(val channelBuilder: () -> Pair<ManagedChannel, Process?>, val isLocal: Boolean = false)