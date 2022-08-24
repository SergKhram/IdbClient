package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtil {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun convertStringToJsonNode(jsonString: String): JsonNode = objectMapper.readTree(jsonString)

    fun <T> convertModelToString(model: T): String = objectMapper.writeValueAsString(model)
}