package io.github.sergkhram.idbClient.managers

import com.fasterxml.jackson.databind.JsonNode
import io.github.sergkhram.idbClient.Const.localTargetsListCmd
import io.github.sergkhram.idbClient.Const.startCompanionCmd
import io.github.sergkhram.idbClient.logs.KLogger
import io.github.sergkhram.idbClient.util.beautifyJsonString
import io.github.sergkhram.idbClient.util.cmdBuilder
import java.io.IOException
import java.net.ServerSocket


internal object ProcessManager {
    private val log = KLogger.logger

    @Synchronized
    internal fun startLocalCompanion(udid: String): Pair<Process, Int> {
        val port = getFreePort()
        val processBuilder = cmdBuilder(
            startCompanionCmd(
                udid,
                port
            )
        )
        val process = processBuilder.start()
        return Pair(process, port)
    }

    internal fun getLocalTargetsJson(): JsonNode? {
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

    internal fun available(port: Int): Boolean {
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

    internal fun Process.destroyImmediately() =
        takeIf { it.isAlive }?.let {
            it.descendants()?.forEach{ pd -> pd.destroyForcibly() }
            it.destroyForcibly()
        }
}