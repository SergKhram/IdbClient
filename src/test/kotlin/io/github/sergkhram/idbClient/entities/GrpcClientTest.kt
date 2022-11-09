package io.github.sergkhram.idbClient.entities

import io.github.sergkhram.idbClient.BaseTest
import io.github.sergkhram.idbClient.Const.localGrpcStartTimeout
import io.github.sergkhram.idbClient.entities.companion.LocalCompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.getFreePortMethod
import io.github.sergkhram.idbClient.secondSimulatorUdid
import io.github.sergkhram.idbClient.wSimulatorsProperty
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.*
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.lang.reflect.Field
import java.net.ServerSocket
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class GrpcClientTest: BaseTest() {
    private var process: Process? = null
    private var channel: ManagedChannel? = null
    private var ss: ServerSocket? = null

    @OptIn(DelicateCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        runBlocking {
            val jobs: MutableList<Deferred<*>> = mutableListOf()
            jobs.addAll(
                listOf(
                    GlobalScope.async {
                        channel?.shutdownNow()
                        process?.descendants()?.forEach {
                            it.destroyForcibly()
                        }
                        process?.destroyForcibly()
                    },
                    GlobalScope.async {
                        ss?.close()
                    }
                )
            )
            jobs.awaitAll()
        }
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @EnabledIfSystemProperty(named = wSimulatorsProperty, matches = "true")
    fun checkCloseLocalGrpcClientTest(softly: SoftAssertions) {
        val localCompanion = LocalCompanionData(secondSimulatorUdid)
        val client = GrpcClient(localCompanion)
        val stub = client.stub
        channel = stub.channel as ManagedChannel
        process = getProcessField().get(client) as Process?
        val port = channel!!.authority().split(":").last()
        runBlocking {
            delay(1000)
        }
        softly.assertThat(process?.isAlive)
            .`as`("Process must be active after start")
            .isTrue
        softly.assertThat(channel!!.isTerminated || channel!!.isShutdown)
            .`as`("Channel must be active after start")
            .isFalse
        softly.assertThat(ProcessManager.available(port.toInt()))
            .`as`("Port must be busy after start")
            .isFalse
        client.close()
        runBlocking {
            delay(1000)
        }
        softly.assertThat(channel!!.isTerminated && channel!!.isShutdown)
            .`as`("Channel must be inactive after closing the client")
            .isTrue
        softly.assertThat(ProcessManager.available(port.toInt()))
            .`as`("Port must not be busy after closing the client")
            .isTrue
    }

    @Test
    fun checkWaitUntilLocalCompanionStartedTest() {
        val port = getFreePortMethod().invoke(ProcessManager) as Int
        ss = ServerSocket(port)
        runBlocking {
            val client = GrpcClient(LocalCompanionData("udid"))
            assertDoesNotThrow {
                getWaitUntilLocalCompanionStartedMethod().callSuspend(
                    client,
                    port,
                    "udid"
                )
            }
        }
    }

    @Test
    fun checkExceptionWaitUntilLocalCompanionStartedTest(softly: SoftAssertions) {
        val port = getFreePortMethod().invoke(ProcessManager) as Int
        runBlocking {
            val client = GrpcClient(LocalCompanionData("udid"))
            val exception = assertThrows<StatusException> {
                getWaitUntilLocalCompanionStartedMethod().callSuspend(
                    client,
                    port,
                    "udid"
                )
            }
            softly.assertThat(exception.status.code)
                .isEqualTo(Status.ABORTED.code)
            softly.assertThat(exception.status.description)
                .isEqualTo("Start local companion(udid) process failed")
        }
    }

    @Test
    fun checkWaitUntilLocalCompanionStartedWithDeferredStartTest() {
        val port = getFreePortMethod().invoke(ProcessManager) as Int
        runBlocking {
            val client = GrpcClient(LocalCompanionData("udid"))
            assertDoesNotThrow {
                val job = async(start = CoroutineStart.LAZY) {
                    getWaitUntilLocalCompanionStartedMethod().callSuspend(
                        client,
                        port,
                        "udid"
                    )
                }
                job.start()
                delay(localGrpcStartTimeout/2)
                ss = ServerSocket(port)
                job.await()
            }
        }
    }

    @Test
    @EnabledOnOs(OS.MAC)
    fun checkStubCreationTest(softly: SoftAssertions) {
        val remoteCompanionData = RemoteCompanionData(address)
        val client = GrpcClient(remoteCompanionData)
        val stubDelegate = getStubProperty(client)
        softly.assertThat(stubDelegate.isInitialized())
            .`as`("Stub must not be initialized before first use")
            .isFalse
        client.use {
            softly.assertThat(client.stub.channel.authority())
                .isEqualTo(address.toString())
            softly.assertThat(stubDelegate.isInitialized())
                .`as`("Stub must be initialized after first use")
                .isTrue
        }
    }

    private fun getProcessField(): Field {
        val processField = GrpcClient::class.java.getDeclaredField("process")
        processField.isAccessible = true
        return processField
    }

    private fun getWaitUntilLocalCompanionStartedMethod(): KFunction<*> {
        val waitMethod = GrpcClient::class.declaredFunctions.first {
            it.name == "waitUntilLocalCompanionStarted"
        }
        waitMethod.isAccessible = true
        return waitMethod
    }

    private fun getStubProperty(client: GrpcClient): Lazy<*> {
        val stubProperty = GrpcClient::class.memberProperties.first {
            it.name == "stub"
        }
        stubProperty.isAccessible = true
        return stubProperty.getDelegate(client) as Lazy<*>
    }
}