package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.sergkhram.idbClient.entities.address.Address
import io.github.sergkhram.idbClient.entities.address.DomainSocketAddress
import io.github.sergkhram.idbClient.entities.address.TcpAddress
import io.github.sergkhram.idbClient.interceptors.IdbInterceptor
import io.github.sergkhram.idbClient.logs.KLogger
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.deleteIfExists

internal val cmdBuilder: (List<String>) -> ProcessBuilder = {
    ProcessBuilder(it)
        .directory(
            File(
                System.getProperty("user.home")
            )
        )
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
}

internal fun compress(srcPath: String): Path {
    val zipPath = kotlin.io.path.createTempFile(suffix = ".zip")
    val srcFile = File(srcPath)
    if (srcFile.isDirectory)
        ZipUtil.pack(
            srcFile,
            zipPath.toFile()
        )
    else
        ZipUtil.packEntry(
            srcFile,
            zipPath.toFile()
        )
    return zipPath
}

internal fun unpackGzip(gzipFile: File) =
    GZIPInputStream(
        FileInputStream(gzipFile)
    ).use(
        GZIPInputStream::readAllBytes
    )

fun unpackBytes(data: ByteArray): ByteArray {
    val gzipFile = kotlin.io.path.createTempFile(suffix = ".gz")
    FileUtils.writeByteArrayToFile(gzipFile.toFile(), data)
    val bytes = unpackGzip(gzipFile.toFile())
    gzipFile.deleteIfExists()
    return bytes
}

/**
 * @param transformFunc if bytes data from flow is compressed you can use this param to unpack this data before
 * creating file
 */
suspend fun Flow<ByteArray>.exportFile(
    dstPath: String,
    transformFunc: (ByteArray) -> ByteArray = { bytes -> bytes }
): File {
    var bytes: ByteArray = byteArrayOf()
    this.catch { KLogger.logger.error { it.message } }.collect {
        bytes += it
    }
    val exportFile = File(dstPath)
    FileUtils.writeByteArrayToFile(exportFile, transformFunc.invoke(bytes))
    return exportFile
}

internal fun prepareManagedChannel(
    address: Address,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): ManagedChannel {
    val managedChannelBuilder = when (address) {
        is TcpAddress -> ManagedChannelBuilder.forAddress(address.host, address.port)
        is DomainSocketAddress -> ManagedChannelBuilder.forTarget(address.path)
    }
    return managedChannelBuilder
        .intercept(IdbInterceptor)
        .usePlaintext()
        .executor(
            dispatcher.asExecutor()
        )
        .build()
}

internal fun isStartedOnMac() = System.getProperty("os.name").contains("mac", ignoreCase = true)

internal fun String.beautifyJsonString() = JsonUtil.convertStringToJsonNode(
    "[${
        this.replace(
            "}\n" +
                    "{", "},{"
        )
    }]"
)

internal fun JsonNode.getUdids(): List<String> =
    (this as ArrayNode)
        .mapNotNull {
            it.get("udid")?.asText()
        }

@RequiresOptIn(message = "This API is experimental. Don't use it without check the result", RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class IdbExperimental