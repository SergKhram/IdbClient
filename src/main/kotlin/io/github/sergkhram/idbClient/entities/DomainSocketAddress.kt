package io.github.sergkhram.idbClient.entities

class DomainSocketAddress(val path: String) : Address {
    override fun equals(other: Any?): Boolean {
        return other != null
                && other is DomainSocketAddress
                && other.path == this.path
    }
}
