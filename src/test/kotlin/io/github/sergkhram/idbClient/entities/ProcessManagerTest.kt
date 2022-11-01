package io.github.sergkhram.idbClient.entities

import io.github.sergkhram.idbClient.BaseTest
import io.github.sergkhram.idbClient.entities.ProcessManager.available
import io.github.sergkhram.idbClient.entities.ProcessManager.getLocalTargetsJson
import io.github.sergkhram.idbClient.entities.ProcessManager.startLocalCompanion
import io.github.sergkhram.idbClient.secondSimulatorUdid
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.lang.reflect.Method
import java.net.ServerSocket

class ProcessManagerTest: BaseTest() {
    var process: Process? = null
    var ss: ServerSocket? = null

    @AfterEach
    fun afterEach() {
        process?.destroy()
        ss?.close()
    }

    @Test
    @EnabledOnOs(OS.MAC)
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
    fun checkAvailableTest(softly: SoftAssertions) {
        val port = getFreePortMethod().invoke(ProcessManager) as Int
        softly.assertThat(available(port)).isTrue
        ss = ServerSocket(port)
        softly.assertThat(available(port)).isFalse
        softly.assertAll()
    }

    private fun getFreePortMethod(): Method {
        val portMethod = ProcessManager::class.java.getDeclaredMethod("getFreePort")
        portMethod.isAccessible = true
        return portMethod
    }
}