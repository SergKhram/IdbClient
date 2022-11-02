package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.sergkhram.idbClient.BaseTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JsonUtilTest: BaseTest() {
    
    @Test
    fun checkConvertStringToJsonNode() {
        val jsonString = "{\"model\":\"iPad Pro (9.7-inch)\",\"os_version\":\"iOS 15.5\",\"udid\":\"57F21C95-A\"}"
        val actual = JsonUtil.convertStringToJsonNode(jsonString)
        Assertions.assertEquals(
            jacksonObjectMapper().readTree(jsonString), actual
        )
    }

    @Test
    fun checkConvertModelToString() {
        class TestClass(val value: Int, val description: String)
        val `object` = TestClass(127, "new class")
        val actual = JsonUtil.convertModelToString(`object`)
        Assertions.assertEquals(
            "{\"value\":127,\"description\":\"new class\"}",
            actual
        )
    }
}