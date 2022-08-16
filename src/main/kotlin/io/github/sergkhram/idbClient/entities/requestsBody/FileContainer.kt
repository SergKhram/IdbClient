package io.github.sergkhram.idbClient.entities.requestsBody

class FileContainer(val bundleId: String = "", val kind: ContainerKind = ContainerKind.ROOT)

fun FileContainer.toFileContainerProto(): idb.FileContainer = idb.FileContainer
    .newBuilder()
    .setKindValue(this.kind.value)
    .setBundleId(this.bundleId)
    .build()