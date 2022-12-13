package io.github.sergkhram.idbClient.ssh

internal data class CmdResult(
    var exitCode: Int? = null,
    var output: String? = null,
    var error: String? = null
)