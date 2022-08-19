package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import idb.TargetDescription
import java.io.File

internal fun JsonNode.convertJsonNodeToTargetDescription() = TargetDescription.newBuilder()
    .setUdid(this.get("udid").asText())
    .setName(this.get("name").asText())
    .setState(this.get("state").asText())
    .setTargetType(this.get("type").asText())
    .setOsVersion(this.get("os_version").asText())
    .setArchitecture(this.get("architecture").asText())
    .build()

internal val cmdBuilder: (List<String>) -> Process = {
    ProcessBuilder(it)
        .directory(File(System.getProperty("user.home")))
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
}
