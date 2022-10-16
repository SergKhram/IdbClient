package io.github.sergkhram.idbClient.entities.address

class DomainSocketAddress(val path: String) : Address {
    override fun toString() = path
    override fun equals(other: Any?): Boolean {
        if(other==null || other !is DomainSocketAddress) return false
        return this === other || this.path == other.path
    }
}