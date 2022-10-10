package io.github.sergkhram.idbClient.entities.address

sealed interface Address {
    fun asString(): String
}
