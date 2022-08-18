package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtil {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        val module = SimpleModule()
        objectMapper.registerModule(module)
    }

    fun convertStringToJsonNode(jsonString: String?): JsonNode? {
        val mapper = objectMapper
        return mapper.readTree(jsonString)
    }
}