package io.github.sergkhram.idbClient.entities.response

import org.apache.commons.io.FileUtils
import java.io.File

sealed class FileResponse(private val bytes: ByteArray, private val extension: String) {
    class ScreenshotResponse(bytes: ByteArray, extension: String = "jpg") :
        FileResponse(bytes, extension.ifEmpty { "jpg" })

    class VideoResponse(bytes: ByteArray) : FileResponse(bytes, "mp4")

    fun exportFile(file: File) {
        FileUtils.writeByteArrayToFile(
            file,
            bytes
        )
    }

    fun exportFile(fileName: String) {
        exportFile(
            File("$fileName.${extension}")
        )
    }
}