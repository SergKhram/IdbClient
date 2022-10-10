package io.github.sergkhram.idbClient.entities.address

class TcpAddress(val host: String, val port: Int) : Address {
    override fun asString() = "$host:$port"
}
