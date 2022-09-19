package io.github.sergkhram.idbClient.entities.response

import org.apache.commons.io.FileUtils
import java.io.File

class ScreenshotResponse(val bytes: ByteArray, extension : String = ""): FileResponse {
    override val extension = extension.ifEmpty { "jpg" }

    override fun exportFile(file: File) {
        FileUtils.writeByteArrayToFile(
            file,
            bytes
        )
    }
}