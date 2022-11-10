package io.github.sergkhram.idbClient

import com.google.protobuf.ByteString
import idb.*
import io.github.sergkhram.idbClient.entities.companion.CompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.entities.requestsBody.management.LogSource
import io.github.sergkhram.idbClient.requests.files.PullRequest
import io.github.sergkhram.idbClient.requests.management.DescribeRequest
import io.github.sergkhram.idbClient.requests.management.LogRequest
import io.github.sergkhram.idbClient.ssh.CmdResult
import io.github.sergkhram.idbClient.ssh.SSHConfig
import io.github.sergkhram.idbClient.ssh.SSHExecutor
import io.github.sergkhram.idbClient.util.GetHostTargetsException
import io.github.sergkhram.idbClient.util.NoCompanionWithUdidException
import io.github.sergkhram.idbClient.util.NoSimulatorsOnHostException
import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.AdditionalAnswers.delegatesTo
import org.mockito.Mockito.mock
import java.lang.reflect.Field
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible
import idb.LogRequest as IdbLogRequest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IOSDebugBridgeClientTest : BaseTest() {

    private val grpcCleanup = GrpcCleanupRule()
    private val idbClient = IOSDebugBridgeClient()
    private val udid = UUID.randomUUID().toString()
    private val defaultDescription = defaultDescriptionAnswer(udid)
    private val serverName = InProcessServerBuilder.generateName()
    private lateinit var channel: ManagedChannel
    private val listOfLogs = listOf(
        "Output 1",
        "Output 2",
        "Output 3"
    )
    val fileBytes = getResourceFile(this::class.java, expectedFile).readBytes()

    private val serviceImpl: CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase = mock(
        CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase::class.java,
        delegatesTo<CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase>(
            object : CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase() {
                override suspend fun describe(request: TargetDescriptionRequest): TargetDescriptionResponse {
                    return defaultDescription
                }

                override suspend fun connect(request: ConnectRequest): ConnectResponse {
                    return ConnectResponse.newBuilder()
                        .setCompanion(defaultDescription.companion)
                        .build()
                }

                override fun log(request: IdbLogRequest): Flow<LogResponse> {
                    return flow {
                        listOfLogs.forEach {
                            if (it.contains("3")) {
                                delay(3000)
                            }
                            emit(
                                LogResponse.newBuilder()
                                    .setOutput(
                                        ByteString.copyFromUtf8(
                                            if (request.source.number == LogSource.TARGET.value)
                                                it
                                            else
                                                "$it companion"
                                        )
                                    )
                                    .build()
                            )
                        }
                    }
                }

                override fun pull(request: idb.PullRequest): Flow<PullResponse> {
                    return flow {
                        emit(
                            PullResponse.newBuilder()
                                .setPayload(
                                    Payload.newBuilder()
                                        .setData(
                                            ByteString.copyFrom(
                                                fileBytes
                                            )
                                        )
                                        .build()
                                )
                                .build()
                        )
                    }
                }
            })
    )

    @BeforeAll
    fun setUp() {
        grpcCleanup.register(
            InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start()
        )
    }

    @BeforeEach
    fun beforeEach() {
        channel = grpcCleanup.register(
            InProcessChannelBuilder.forName(serverName).directExecutor().build()
        )
    }

    @Test
    fun checkGetTargetsListTest(softly: SoftAssertions) {
        runBlocking {
            prepareCompanionForTest()
            val actual = idbClient.getTargetsList()
            softly.assertThat(actual.size)
                .isEqualTo(1)
            softly.assertThat(actual.first().address)
                .isEqualTo(address)
            softly.assertThat(actual.first().targetDescription)
                .isEqualTo(
                    defaultDescription.targetDescription
                )
            softly.assertThat(actual.first().companion)
                .isEqualTo(
                    defaultDescription.companion
                )
        }
    }

    @Test
    fun checkDisconnectCompanionByUdidTest(softly: SoftAssertions) {
        runBlocking {
            prepareCompanionForTest()
            softly.assertThat(idbClient.getTargetsList().size).isEqualTo(1)
            idbClient.disconnectCompanion(udid)
            softly.assertThat(idbClient.getTargetsList().size).isEqualTo(0)
        }
    }

    @Test
    fun checkDisconnectCompanionByAddressTest(softly: SoftAssertions) {
        runBlocking {
            prepareCompanionForTest()
            softly.assertThat(idbClient.getTargetsList().size).isEqualTo(1)
            idbClient.disconnectCompanion(address)
            softly.assertThat(idbClient.getTargetsList().size).isEqualTo(0)
        }
    }

    @Test
    fun checkPMapTest(softly: SoftAssertions) {
        runBlocking {
            val firstUuid = UUID.randomUUID().toString()
            val secondUuid = UUID.randomUUID().toString()
            val listOfValues = CopyOnWriteArrayList<String>()
            val hashMap = ConcurrentHashMap<String, String>()
            val delayValue = 3000L

            hashMap.putAll(
                mapOf(
                    firstUuid to firstUuid,
                    secondUuid to secondUuid
                )
            )
            val pMapFunc = getPMapFunc()
            suspend fun suspendFun(data: Map.Entry<String, String>) {
                if (data.key == firstUuid) {
                    delay(delayValue)
                }
                listOfValues.add(data.value)
            }

            val job = launch {
                pMapFunc.callSuspend(
                    idbClient,
                    hashMap,
                    ::suspendFun
                )
            }
            delay(delayValue / 2)
            softly.assertThat(listOfValues.size).isEqualTo(1)
            softly.assertThat(listOfValues).containsExactly(secondUuid)
            job.join()
            softly.assertThat(listOfValues.size).isEqualTo(2)
            softly.assertThat(listOfValues).containsExactly(secondUuid, firstUuid)
        }
    }

    @Test
    fun checkExecuteIdbRequestTest() {
        runBlocking {
            prepareCompanionForTest()
            val idbRequest = DescribeRequest()
            val response = assertDoesNotThrow {
                idbClient.execute(idbRequest, udid)
            }
            Assertions.assertEquals(
                defaultDescription,
                response
            )
        }
    }

    @Test
    fun checkExecuteIdbRequestWExceptionTest() {
        runBlocking {
            val idbRequest = DescribeRequest()
            val missingUuid = UUID.randomUUID().toString()
            val exception = assertThrows<NoCompanionWithUdidException> {
                idbClient.execute(idbRequest, missingUuid)
            }
            Assertions.assertEquals(
                "There is no companion with $missingUuid",
                exception.message
            )
        }
    }

    @Test
    fun checkExecutePredicateIdbRequestTest() {
        runBlocking {
            prepareCompanionForTest()
            val idbRequest = LogRequest(
                { false }
            )
            val response = assertDoesNotThrow {
                idbClient.execute(idbRequest, udid)
            }
            Assertions.assertEquals(
                listOfLogs,
                response
            )
        }
    }

    @Test
    fun checkExecutePredicateIdbRequestWTimeoutTest() {
        runBlocking {
            prepareCompanionForTest()
            val idbRequest = LogRequest(
                { false },
                Duration.ofSeconds(1)
            )
            val response = assertDoesNotThrow {
                idbClient.execute(idbRequest, udid)
            }
            Assertions.assertEquals(
                listOfLogs.dropLast(1),
                response
            )
        }
    }

    @Test
    fun checkExecutePredicateIdbRequestWPredicateTest() {
        runBlocking {
            prepareCompanionForTest()
            val atomic = AtomicBoolean(false)
            val idbRequest = LogRequest(
                { atomic.get() }
            )
            val response = async {
                assertDoesNotThrow {
                    idbClient.execute(idbRequest, udid)
                }
            }
            delay(1000)
            atomic.set(true)
            Assertions.assertEquals(
                listOfLogs.dropLast(1),
                response.await()
            )
        }
    }

    @Test
    fun checkExecutePredicateIdbRequestWCompanionSourceTest() {
        runBlocking {
            prepareCompanionForTest()
            val idbRequest = LogRequest(
                { false },
                source = LogSource.COMPANION
            )
            val response = assertDoesNotThrow {
                idbClient.execute(idbRequest, udid)
            }
            Assertions.assertEquals(
                listOfLogs.map { "$it companion" },
                response
            )
        }
    }

    @Test
    fun checkExecutePredicateIdbRequestWExceptionTest() {
        runBlocking {
            val idbRequest = LogRequest(
                { false }
            )
            val missingUuid = UUID.randomUUID().toString()
            val exception = assertThrows<NoCompanionWithUdidException> {
                idbClient.execute(idbRequest, missingUuid)
            }
            Assertions.assertEquals(
                "There is no companion with $missingUuid",
                exception.message
            )
        }
    }

    @Test
    fun checkExecuteAsyncIdbRequestWExceptionTest() {
        runBlocking {
            val idbRequest = PullRequest("")
            val missingUuid = UUID.randomUUID().toString()
            val exception = assertThrows<NoCompanionWithUdidException> {
                idbClient.execute(idbRequest, missingUuid)
            }
            Assertions.assertEquals(
                "There is no companion with $missingUuid",
                exception.message
            )
        }
    }

    @Test
    fun checkExecuteAsyncIdbRequestTest() {
        runBlocking {
            prepareCompanionForTest()
            val idbRequest = PullRequest("")
            val response = assertDoesNotThrow {
                idbClient.execute(idbRequest, udid)
            }
            Assertions.assertEquals(
                fileBytes.map { it.toUnsignedValue() },
                response.first().map { it.toUnsignedValue() }
            )
        }
    }

    @Test
    fun checkGetHostTargetsTest() {
        val config = SSHConfig("", "", "")
        mockkObject(SSHExecutor)
        every {
            SSHExecutor.execute(
                config,
                Const.localTargetsListCmd.joinToString(" ")
            )
        } returns CmdResult(
            0,
            dirtyJson
        )
        val result = assertDoesNotThrow {
            idbClient.getHostTargets(
                config
            )
        }
        assertThat(result)
            .containsExactlyInAnyOrderElementsOf(
                listOf(
                    "57F21C95-A14C-4599-927A-2304C81645E5",
                    "F6898331-B68E-41F2-98EA-FD436F420C4A"
                )
            )
    }

    @Test
    fun checkGetHostTargetsCmdExceptionTest() {
        val config = SSHConfig("127.0.0.1", "", "")
        mockkObject(SSHExecutor)
        every {
            SSHExecutor.execute(
                config,
                Const.localTargetsListCmd.joinToString(" ")
            )
        } returns CmdResult(
            1,
            "",
            "error"
        )
        val exception = assertThrows<GetHostTargetsException> {
            idbClient.getHostTargets(
                config
            )
        }
        assertThat(exception.message)
            .isEqualTo("Get host' targets process failed for ${config.host}:${config.port} with reason: error")
    }

    @Test
    fun checkGetHostTargetsEmptyListExceptionTest() {
        val config = SSHConfig("127.0.0.1", "", "")
        mockkObject(SSHExecutor)
        every {
            SSHExecutor.execute(
                config,
                Const.localTargetsListCmd.joinToString(" ")
            )
        } returns CmdResult(
            0
        )
        val exception = assertThrows<NoSimulatorsOnHostException> {
            idbClient.getHostTargets(
                config
            )
        }
        assertThat(exception.message)
            .isEqualTo("There are no simulators on this host: ${config.host}")
    }

    @Test
    fun checkStartRemoteTargetCompanionDefaultPortTest() {
        val config = SSHConfig("127.0.0.1", "", "")
        val remoteCompanionCmd = Const.startCompanionCmd(
            udid,
            10882
        ).plus(">/dev/null 2>&1 &")
        mockkObject(SSHExecutor)
        every {
            SSHExecutor.execute(
                config,
                remoteCompanionCmd.joinToString(" ")
            )
        } returns CmdResult(
            0,
            ""
        )
        val result = assertDoesNotThrow {
            idbClient.startRemoteTargetCompanion(
                config,
                udid
            )
        }
        assertTrue(result)
    }

    @Test
    fun checkStartRemoteTargetCompanionTest() {
        val config = SSHConfig("127.0.0.1", "", "")
        val remoteCompanionCmd = Const.startCompanionCmd(
            udid,
            10883
        ).plus(">/dev/null 2>&1 &")
        mockkObject(SSHExecutor)
        every {
            SSHExecutor.execute(
                config,
                remoteCompanionCmd.joinToString(" ")
            )
        } returns CmdResult(
            0,
            ""
        )
        val result = assertDoesNotThrow {
            idbClient.startRemoteTargetCompanion(
                config,
                udid,
                10883
            )
        }
        assertTrue(result)
    }

    @Test
    fun checkStartRemoteTargetCompanionFailedStartExceptionTest() {
        val config = SSHConfig("127.0.0.1", "", "")
        val remoteCompanionCmd = Const.startCompanionCmd(
            udid,
            10882
        ).plus(">/dev/null 2>&1 &")
        val error = "some error"
        mockkObject(SSHExecutor)
        every {
            SSHExecutor.execute(
                config,
                remoteCompanionCmd.joinToString(" ")
            )
        } returns CmdResult(
            1,
            "",
            error
        )
        val result = assertDoesNotThrow {
            idbClient.startRemoteTargetCompanion(
                config,
                udid
            )
        }
        assertFalse(result)
    }

    @AfterEach
    fun afterEach() {
        getClientsList().clear()
        if (this::channel.isInitialized) channel.shutdownNow()
    }

    @AfterAll
    fun teardown() {
        val teardownMethod = GrpcCleanupRule::class.java.getDeclaredMethod("teardown")
        teardownMethod.isAccessible = true
        teardownMethod.invoke(grpcCleanup)
    }

    private fun getChannel(): Field {
        val channelField = RemoteCompanionData::class.java.getDeclaredField("channel")
        channelField.isAccessible = true
        return channelField
    }

    private fun getClientsList(): ConcurrentHashMap<String, CompanionData> {
        val clientsField = IOSDebugBridgeClient::class.java.getDeclaredField("clients")
        clientsField.isAccessible = true
        return clientsField.get(idbClient) as ConcurrentHashMap<String, CompanionData>
    }

    private fun putMockedCompanionToClients(rcd: RemoteCompanionData) {
        getClientsList()[udid] = rcd
    }

    private fun prepareCompanionForTest() {
        val remoteCompanionData = RemoteCompanionData(address)
        val currentChannel = getChannel().get(remoteCompanionData)
        (currentChannel as ManagedChannel).shutdownNow()
        getChannel().set(remoteCompanionData, channel)
        putMockedCompanionToClients(remoteCompanionData)
    }

    private fun getPMapFunc(): KFunction<*> {
        val funcs = IOSDebugBridgeClient::class.declaredFunctions
        val func = funcs.first { it.name == "pMap" }
        func.isAccessible = true
        return func
    }

    private val dirtyJson = "{\"model\":\"iPad Pro (9.7-inch)\",\"os_version\":\"iOS 15.5\",\"udid\":\"57F21C95-A" +
            "14C-4599-927A-2304C81645E5\",\"architecture\":\"x86_64\",\"type\":\"Simulator\",\"name\":\"iPad Pro " +
            "(9.7-inch)\",\"state\":\"Shutdown\"}\n{\"model\":\"iPad Pro (9.7-inch)\",\"os_version\":\"iOS 15.5\",\"ud" +
            "id\":\"F6898331-B68E-41F2-98EA-FD436F420C4A\",\"architecture\":\"x86_64\",\"type\":\"Simulator\",\"na" +
            "me\":\"iPad Pro (9.7-inch)\",\"state\":\"Shutdown\"}"
}