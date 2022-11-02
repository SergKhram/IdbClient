package io.github.sergkhram.idbClient.entities

import com.fasterxml.jackson.databind.JsonNode
import io.github.sergkhram.idbClient.Const.localTargetsListCmd
import io.github.sergkhram.idbClient.Const.startLocalCompanionCmd
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.util.beautifyJsonString
import io.github.sergkhram.idbClient.util.cmdBuilder
import io.github.sergkhram.idbClient.util.destroyImmediately
import java.io.IOException
import java.net.ServerSocket


internal object ProcessManager {
    private val log = KLogger.logger

    @Synchronized
    fun startLocalCompanion(udid: String): Pair<Process, Int> {
        val port = getFreePort()
        val processBuilder = cmdBuilder(
            startLocalCompanionCmd(
                udid,
                port
            )
        )
        val process = processBuilder.start()
        return Pair(process, port)
    }

    fun getLocalTargetsJson(): JsonNode? {
        var process: Process? = null
        var output: String? = null
        try {
            process = cmdBuilder(localTargetsListCmd).start()
            process.waitFor()
            output = process.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            log.info(e.localizedMessage, e)
        } finally {
            process?.destroyImmediately()
        }
        return output?.beautifyJsonString()
    }

    private fun getFreePort(): Int {
        return ServerSocket(0).use { socket -> socket.localPort }
    }

    fun available(port: Int): Boolean {
        var ss: ServerSocket? = null
        try {
            ss = ServerSocket(port)
            ss.reuseAddress = true
            return true
        } catch (_: IOException) {
        } finally {
            try {
                ss?.close()
            } catch (_: IOException) {}
        }
        return false
    }
}