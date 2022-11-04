package io.github.sergkhram.idbClient

import idb.*
import io.github.sergkhram.idbClient.entities.companion.CompanionData
import io.github.sergkhram.idbClient.entities.companion.RemoteCompanionData
import io.github.sergkhram.idbClient.requests.management.DescribeRequest
import io.github.sergkhram.idbClient.util.NoCompanionWithUdidException
import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.mockito.AdditionalAnswers.delegatesTo
import org.mockito.Mockito.mock
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IOSDebugBridgeClientTest: BaseTest() {

    private val grpcCleanup = GrpcCleanupRule()
    private val idbClient = IOSDebugBridgeClient()
    private val udid = UUID.randomUUID().toString()
    private val defaultDescription = defaultDescriptionAnswer(udid)
    private val serverName = InProcessServerBuilder.generateName()
    private lateinit var channel: ManagedChannel

    private val serviceImpl: CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase = mock(
        CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase::class.java, delegatesTo<CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase>(
            object : CompanionServiceGrpcKt.CompanionServiceCoroutineImplBase() {
                override suspend fun describe(request: TargetDescriptionRequest): TargetDescriptionResponse {
                    return defaultDescription
                }

                override suspend fun connect(request: ConnectRequest): ConnectResponse {
                    return ConnectResponse.newBuilder()
                        .setCompanion(defaultDescription.companion)
                        .build()
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
                if(data.key == firstUuid) {
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
            delay(delayValue/2)
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

    @AfterEach
    fun afterEach() {
        getClientsList().clear()
        if(this::channel.isInitialized) channel.shutdownNow()
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
}