package io.github.sergkhram.idbClient

import idb.CompanionInfo
import idb.ScreenDimensions
import idb.TargetDescription
import idb.TargetDescriptionResponse
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

internal fun defaultDescriptionAnswer(udid: String) =
    TargetDescriptionResponse.newBuilder()
        .setCompanion(
            CompanionInfo.newBuilder()
                .setUdid(udid)
                .setIsLocal(false)
                .build()
        )
        .setTargetDescription(
            TargetDescription.newBuilder()
                .setUdid(udid)
                .setName("iPhone 14")
                .setScreenDimensions(
                    ScreenDimensions.newBuilder()
                        .setDensity(3.0)
                        .setHeight(2532)
                        .setWidth(1170)
                        .setWidthPoints(390)
                        .setHeightPoints(844)
                        .build()
                )
                .setState("Booted")
                .setTargetType("simulator")
                .setOsVersion("iOS 15.5")
                .setArchitecture("x86_64")
                .build()
        ).build()

const val wSimulatorsProperty = "wSimulators"

internal fun Byte.toUnsignedValue(): Int = this.toInt() and 0xFF