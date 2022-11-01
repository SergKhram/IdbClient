package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.sergkhram.idbClient.*
import io.grpc.ConnectivityState
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import kotlin.io.path.deleteIfExists
import kotlin.io.path.name


class UtilsTest: BaseTest() {

    @Test
    fun checkCmdBuilderTest() {
        val cmd = listOf("echo \"q\"")
        val builder = cmdBuilder(cmd)

        Assertions.assertEquals(
            cmd,
            builder.command()
        )
    }

    @Test
    fun checkCompressFileTest() {
        val compressedFile = compress(
            getResourceFile(this::class.java, expectedFile).path
        )

        assertThat(compressedFile.name).contains(".zip")
        compressedFile.deleteIfExists()
    }

    @Test
    fun checkCompressDirTest() {
        val compressedFile = compress(
            getResourceFile(this::class.java, expectedDir).path
        )

        assertThat(compressedFile.name).contains(".zip")
        compressedFile.deleteIfExists()
    }

    @Test
    fun checkUnpackGzipTest() {
        val byteArray = unpackGzip(
            getResourceFile(this::class.java, expectedGzip)
        )

        Assertions.assertEquals(
            "expected",
            byteArray.decodeToString()
        )
    }

    @Test
    fun checkUnpackBytesTest() {
        val byteArray = unpackBytes(
            getResourceFile(this::class.java, expectedGzip).readBytes()
        )

        Assertions.assertEquals(
            "expected",
            byteArray.decodeToString()
        )
    }

    @Test
    fun checkBeautifyJsonString() {
        val beautifiedJsonString = dirtyJson.beautifyJsonString()

        Assertions.assertEquals(
            jacksonObjectMapper().readTree(correctJson),
            beautifiedJsonString
        )
    }

    @Test
    fun checkPrepareManagedChannelTest(softly: SoftAssertions) {
        val channel = prepareManagedChannel(address)
        softly.assertThat(channel.getState(false).name).isEqualTo(ConnectivityState.IDLE.name)
        softly.assertThat(channel.isShutdown).isFalse
        softly.assertThat(channel.isTerminated).isFalse
        softly.assertThat(channel.authority()).isEqualTo(address.host + ":" + address.port)
        softly.assertAll()
        channel.shutdownNow()
    }

    @Test
    @EnabledOnOs(OS.MAC)
    fun checkIsStartedOnMacTest() {
        Assertions.assertTrue(isStartedOnMac())
    }

    @Test
    @DisabledOnOs(OS.MAC)
    fun checkIsStartedNotOnMacTest() {
        Assertions.assertFalse(isStartedOnMac())
    }

    private val dirtyJson = "{\"model\":\"iPad Pro (9.7-inch)\",\"os_version\":\"iOS 15.5\",\"udid\":\"57F21C95-A" +
            "14C-4599-927A-2304C81645E5\",\"architecture\":\"x86_64\",\"type\":\"Simulator\",\"name\":\"iPad Pro " +
            "(9.7-inch)\",\"state\":\"Shutdown\"}\n{\"model\":\"iPad Pro (9.7-inch)\",\"os_version\":\"iOS 15.5\",\"ud" +
            "id\":\"F6898331-B68E-41F2-98EA-FD436F420C4A\",\"architecture\":\"x86_64\",\"type\":\"Simulator\",\"na" +
            "me\":\"iPad Pro (9.7-inch)\",\"state\":\"Shutdown\"}"

    private val correctJson = "[{\"model\":\"iPad Pro (9.7-inch)\",\"os_version\":\"iOS 15.5\",\"ud" +
            "id\":\"57F21C95-A14C-4599-927A-2304C81645E5\",\"architecture\":\"x86_64\",\"type\":\"Simulator\",\"na" +
            "me\":\"iPad Pro (9.7-inch)\",\"state\":\"Shutdown\"},{\"model\":\"iPad Pro (9.7-inch)\",\"os_versio" +
            "n\":\"iOS 15.5\",\"udid\":\"F6898331-B68E-41F2-98EA-FD436F420C4A\",\"architecture\":\"x86_64\",\"ty" +
            "pe\":\"Simulator\",\"name\":\"iPad Pro (9.7-inch)\",\"state\":\"Shutdown\"}]"
}