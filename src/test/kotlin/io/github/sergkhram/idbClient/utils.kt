package io.github.sergkhram.idbClient

import java.io.File

fun getResourceFile(clazz: Class<out Any>, fileName: String): File {
    val classLoader: ClassLoader = clazz.classLoader
    return File(classLoader.getResource(fileName)?.file ?: "")
}

const val expectedFile = "expectedDirectory/expected.txt"
const val expectedDir = "expectedDirectory"
const val expectedGzip = "expected.txt.gz"

val secondSimulatorUdid = System.getProperty("secondSimulator")?.toString() ?: ""