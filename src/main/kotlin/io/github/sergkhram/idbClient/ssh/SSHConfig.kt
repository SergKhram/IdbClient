package io.github.sergkhram.idbClient.ssh

class SSHConfig(
    val host: String,
    val user: String,
    val password: String,
    val port: Int = 22
)