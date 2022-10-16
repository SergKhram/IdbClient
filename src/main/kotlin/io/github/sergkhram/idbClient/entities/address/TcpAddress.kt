package io.github.sergkhram.idbClient.entities.address

class TcpAddress(val host: String, val port: Int) : Address {
    override fun toString() = "$host:$port"
    override fun equals(other: Any?): Boolean {
        if(other==null || other !is TcpAddress) return false
        return this === other || (this.port == other.port && this.host == other.host)
    }
}
