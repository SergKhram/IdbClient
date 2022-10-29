package io.github.sergkhram.idbClient

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class CreateIdbClientTest: BaseTest() {

    @Test
    fun simpleCreateIdbClientTest() {
        runBlocking {
            val idb = IOSDebugBridgeClient()
            val udid = idb.connectToCompanion(address)
            log.info { "$udid - companion connected" }
            val list = idb.getTargetsList()
            Assertions.assertEquals(1, list.size)
            Assertions.assertEquals(address, list.first().address)
            Assertions.assertEquals(udid, list.first().targetDescription.udid)
        }
    }

    @Test
    fun createIdbClientWCompanionInfoTest() {
        runBlocking {
            val idb = IOSDebugBridgeClient(
                listOfCompanions = listOf(address)
            )
            val list = idb.getTargetsList()
            Assertions.assertEquals(1, list.size)
            Assertions.assertEquals(address, list.first().address)
        }
    }
}