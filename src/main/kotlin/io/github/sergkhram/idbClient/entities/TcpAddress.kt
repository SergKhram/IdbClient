package io.github.sergkhram.idbClient.entities

class TcpAddress(val host: String, val port: Int = 0) : Address {
    override fun equals(other: Any?): Boolean {
        return other != null
                && other is TcpAddress
                && other.host == this.host
                && other.port == this.port
    }
}
