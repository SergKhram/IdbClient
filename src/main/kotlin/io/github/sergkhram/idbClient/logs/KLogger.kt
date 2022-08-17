package io.github.sergkhram.idbClient.logs

import io.github.sergkhram.idbClient.IOSDebugBridgeClient
import mu.KotlinLogging

object KLogger {
    val logger = KotlinLogging.logger(IOSDebugBridgeClient::class.java.canonicalName)
}