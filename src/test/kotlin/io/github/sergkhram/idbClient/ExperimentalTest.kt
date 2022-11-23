package io.github.sergkhram.idbClient

import io.github.sergkhram.idbClient.ssh.SSHConfig
import org.junit.jupiter.api.Test

class ExperimentalTest: BaseTest() {

    @Test
    fun test() {
        val idb = IOSDebugBridgeClient()
        val devices = idb.getHostTargets(
            SSHConfig(
                "",//host
                "",//user
                ""//password
            )
        )
        log.info { devices.joinToString { "," } }
    }
}