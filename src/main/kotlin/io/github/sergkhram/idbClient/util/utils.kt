package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import idb.TargetDescription
import io.github.sergkhram.idbClient.logs.KLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.deleteIfExists

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
    if(srcFile.isDirectory)
        ZipUtil.pack(srcFile, zipPath.toFile())
    else
        ZipUtil.packEntry(srcFile, zipPath.toFile())
    return zipPath
}

internal fun unpackGzip(gzipFile: File): ByteArray {
    return GZIPInputStream(
        FileInputStream(gzipFile)
    ).use(GZIPInputStream::readAllBytes)
}

fun unpackBytes(data: ByteArray): ByteArray {
    val gzipFile = kotlin.io.path.createTempFile(suffix = ".gz")
    FileUtils.writeByteArrayToFile(gzipFile.toFile(), data)
    val bytes = unpackGzip(gzipFile.toFile())
    gzipFile.deleteIfExists()
    return bytes
}

/**
 * @param transformFunc - if bytes data from flow is compressed you can use this param to unpack this data before
 * creating file
 */
suspend fun Flow<ByteArray>.exportFile(
    dstPath: String,
    transformFunc: (ByteArray) -> ByteArray = { bytes -> bytes }
): File {
    var bytes: ByteArray = byteArrayOf()
    this.catch{ KLogger.logger.error { it.message } }.collect {
        bytes += it
    }
    val exportFile = File(dstPath)
    FileUtils.writeByteArrayToFile(exportFile, transformFunc.invoke(bytes))
    return exportFile
}