package io.github.sergkhram.idbClient

import io.github.sergkhram.idbClient.entities.address.TcpAddress
import io.github.sergkhram.idbClient.logs.KLogger
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CreateIdbClientTest {
    private val log = KLogger.logger

    @Test
    fun simpleCreateIdbClientTest() {
        runBlocking {
            val idb = IOSDebugBridgeClient()
            val address = TcpAddress("127.0.0.1", 10882)
            val udid = idb.connectToCompanion(address)
            udid?.let {
                log.info { it }
                val list = idb.getTargetsList()
                Assertions.assertEquals(1, list.size)
                Assertions.assertEquals(address, list.first().address)
                Assertions.assertEquals(it, list.first().targetDescription.udid)
            }
        }
    }

    @Test
    fun createIdbClientWCompanionInfoTest() {
        runBlocking {
            val address = TcpAddress("127.0.0.1", 10882)
            val idb = IOSDebugBridgeClient(
                listOfCompanions = listOf(address)
            )
            val list = idb.getTargetsList()
            list.takeIf { it.isNotEmpty() }?.let {
                Assertions.assertEquals(1, list.size)
                Assertions.assertEquals(address, it.first().address)
            }
        }
    }
}