package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import idb.TargetDescription
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.nio.file.Path

internal fun JsonNode.convertJsonNodeToTargetDescription() = TargetDescription.newBuilder()
    .setUdid(this.get("udid").asText())
    .setName(this.get("name").asText())
    .setState(this.get("state").asText())
    .setTargetType(this.get("type").asText())
    .setOsVersion(this.get("os_version").asText())
    .setArchitecture(this.get("architecture").asText())
    .build()

internal val cmdBuilder: (List<String>) -> ProcessBuilder = {
    ProcessBuilder(it)
        .directory(File(System.getProperty("user.home")))
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
}

internal fun compress(srcPath: String): Path {
    val zipPath = kotlin.io.path.createTempFile(suffix = ".zip")
    val srcFile = File(srcPath)
    if(srcFile.isDirectory) ZipUtil.pack(srcFile, zipPath.toFile()) else ZipUtil.packEntry(srcFile, zipPath.toFile())
    return zipPath
}
