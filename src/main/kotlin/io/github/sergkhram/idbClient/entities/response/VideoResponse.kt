package io.github.sergkhram.idbClient.entities.response

import org.apache.commons.io.FileUtils
import java.io.File

class VideoResponse(val bytes: ByteArray) : FileResponse {
    override val extension = "mp4"

    override fun exportFile(file: File) {
        FileUtils.writeByteArrayToFile(
            file,
            bytes
        )
    }
}