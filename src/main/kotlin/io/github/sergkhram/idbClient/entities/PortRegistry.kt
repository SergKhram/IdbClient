package io.github.sergkhram.idbClient.entities

import java.io.IOException
import java.net.ServerSocket


object PortRegistry {
    @Synchronized
    fun getFreePort(): Int {
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