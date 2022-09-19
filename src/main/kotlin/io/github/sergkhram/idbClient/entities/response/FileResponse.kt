package io.github.sergkhram.idbClient.entities.response

import java.io.File

interface FileResponse {
    val extension: String
    fun exportFile(file: File)

    fun exportFile(fileName: String) {
        exportFile(
            File("$fileName.${extension}")
        )
    }
}