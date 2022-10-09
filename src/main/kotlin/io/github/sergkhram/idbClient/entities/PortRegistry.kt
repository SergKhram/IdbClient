package io.github.sergkhram.idbClient.entities

import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket


object PortRegistry {
    @Synchronized
    fun getFreePort(): Int {
        return ServerSocket(0).use { socket -> socket.localPort }
    }

    fun available(port: Int): Boolean {
        var ss: ServerSocket? = null
        var ds: DatagramSocket? = null
        try {
            ss = ServerSocket(port)
            ss.reuseAddress = true
            ds = DatagramSocket(port)
            ds.reuseAddress = true
            return true
        } catch (_: IOException) {
        } finally {
            ds?.close()
            if (ss != null) {
                try {
                    ss.close()
                } catch (e: IOException) {
                    /* should not be thrown */
                }
            }
        }
        return false
    }
}