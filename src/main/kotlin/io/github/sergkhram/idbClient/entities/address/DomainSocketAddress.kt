package io.github.sergkhram.idbClient.entities.address

class DomainSocketAddress(val path: String) : Address {
    override fun toString() = path
}