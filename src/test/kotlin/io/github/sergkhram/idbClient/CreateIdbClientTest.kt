package io.github.sergkhram.idbClient

import idb.CompanionServiceGrpcKt
import idb.ConnectRequest
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.net.Socket


class CreateIdbClientTest: BaseTest() {

//    @Test
//    fun simpleCreateIdbClientTest() {
//        runBlocking {
//            val idb = IOSDebugBridgeClient()
//            val address = TcpAddress("localhost", 10882)
//            val udid = idb.connectToCompanion(address)
//            log.info { "$udid - companion connected" }
//            val list = idb.getTargetsList()
//            Assertions.assertEquals(1, list.size)
//            Assertions.assertEquals(address, list.first().address)
//            Assertions.assertEquals(udid, list.first().targetDescription.udid)
//        }
//    }
//
//    @Test
//    fun createIdbClientWCompanionInfoTest() {
//        runBlocking {
//            val address = TcpAddress("localhost", 10882)
//            val idb = IOSDebugBridgeClient(
//                listOfCompanions = listOf(address)
//            )
//            val list = idb.getTargetsList()
//            Assertions.assertEquals(1, list.size)
//            Assertions.assertEquals(address, list.first().address)
//        }
//    }

    @Test
    fun createChannelManuallyTest() {
        runBlocking {
            val channel = ManagedChannelBuilder.forAddress("127.0.0.1", 10882).usePlaintext().build()
            log.info { "ManagedChannel " + channel.isShutdown.toString() }
            log.info { "ManagedChannel " + channel.isTerminated.toString() }
            log.info { "ManagedChannel " + channel.getState(false).toString() }
            val connectionResponse = CompanionServiceGrpcKt.CompanionServiceCoroutineStub(channel).connect(
                ConnectRequest.getDefaultInstance()
            )
            val udid = connectionResponse.companion.udid
            log.info { "ManagedChannel $udid - companion connected" }
        }
    }

    @Test
    fun createAnotherTypeChannelManuallyTest() {
        runBlocking {
            val channel = NettyChannelBuilder.forAddress("127.0.0.1", 10882).usePlaintext().build()
            log.info { "NettyChannel " + channel.isShutdown.toString() }
            log.info { "NettyChannel " + channel.isTerminated.toString() }
            log.info { "NettyChannel " + channel.getState(false).toString() }
            val connectionResponse = CompanionServiceGrpcKt.CompanionServiceCoroutineStub(channel).connect(
                ConnectRequest.getDefaultInstance()
            )
            val udid = connectionResponse.companion.udid
            log.info { "NettyChannel $udid - companion connected" }
        }
    }

    @Test
    fun connectToIdbCompanionTest() {
        Assertions.assertTrue(serverListening("127.0.0.1", 10882));
    }

    private fun serverListening(host: String?, port: Int): Boolean {
        var s: Socket? = null
        return try {
            s = Socket(host, port)
            true
        } catch (e: Exception) {
            false
        } finally {
            if (s != null) try {
                s.close()
            } catch (e: Exception) {
            }
        }
    }
}