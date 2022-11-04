package io.github.sergkhram.idbClient.entities

import io.github.sergkhram.idbClient.BaseTest
import io.github.sergkhram.idbClient.entities.ProcessManager.available
import io.github.sergkhram.idbClient.entities.ProcessManager.getLocalTargetsJson
import io.github.sergkhram.idbClient.entities.ProcessManager.startLocalCompanion
import io.github.sergkhram.idbClient.getFreePortMethod
import io.github.sergkhram.idbClient.secondSimulatorUdid
import io.github.sergkhram.idbClient.wSimulatorsProperty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.net.ServerSocket

class ProcessManagerTest: BaseTest() {
    private var process: Process? = null
    private var ss: ServerSocket? = null

    @AfterEach
    fun afterEach() {
        process?.destroyForcibly()
        ss?.close()
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @EnabledIfSystemProperty(named = wSimulatorsProperty, matches = "true")
    fun checkStartLocalCompanionTest(softly: SoftAssertions) {
        val startedSimulatorData = startLocalCompanion(secondSimulatorUdid)
        process = startedSimulatorData.first
        softly.assertThat(startedSimulatorData.second).isNotNull
        softly.assertThat(process?.isAlive).isTrue
        runBlocking {
            delay(3000)
        }
        softly.assertThat(available(startedSimulatorData.second)).isFalse
        softly.assertAll()
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @EnabledIfSystemProperty(named = wSimulatorsProperty, matches = "true")
    fun checkGetLocalTargetsJsonTest(softly: SoftAssertions) {
        val targets = getLocalTargetsJson()
        val udids = targets!!.mapNotNull {
            it.get("udid")?.asText()
        }
        softly.assertThat(udids).contains(secondSimulatorUdid)
        softly.assertThat(udids).isNotEmpty
        softly.assertAll()
    }

    @Test
    fun checkGetFreePortTest() {
        val port = getFreePortMethod().invoke(ProcessManager) as Int
        assertDoesNotThrow {
            ServerSocket(port).close()
        }
    }

    @Test
    fun checkIsPortAvailableTest(softly: SoftAssertions) {
        val port = getFreePortMethod().invoke(ProcessManager) as Int
        softly.assertThat(available(port)).isTrue
        ss = ServerSocket(port)
        softly.assertThat(available(port)).isFalse
        softly.assertAll()
    }
}