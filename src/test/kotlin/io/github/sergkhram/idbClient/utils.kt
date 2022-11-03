package io.github.sergkhram.idbClient

import io.github.sergkhram.idbClient.entities.ProcessManager
import java.io.File
import java.lang.reflect.Method

internal fun getResourceFile(clazz: Class<out Any>, fileName: String): File {
    val classLoader: ClassLoader = clazz.classLoader
    return File(classLoader.getResource(fileName)?.file ?: "")
}

internal fun getFreePortMethod(): Method {
    val portMethod = ProcessManager::class.java.getDeclaredMethod("getFreePort")
    portMethod.isAccessible = true
    return portMethod
}

const val expectedFile = "expectedDirectory/expected.txt"
const val expectedDir = "expectedDirectory"
const val expectedGzip = "expected.txt.gz"

val secondSimulatorUdid = System.getProperty("secondSimulator")?.toString() ?: ""