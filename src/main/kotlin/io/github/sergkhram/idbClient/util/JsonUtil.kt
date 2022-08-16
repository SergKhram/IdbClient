package io.github.sergkhram.idbClient.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtil {
    var objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        val module = SimpleModule()
        objectMapper.registerModule(module)
    }

    fun <T> convertModelToJsonNode(model: T): JsonNode? {
        val mapper = objectMapper
        return mapper.readValue(mapper.writeValueAsBytes(model), JsonNode::class.java)
    }

    fun <T> convertModelToString(model: T): String? {
        val mapper = objectMapper
        return mapper.writeValueAsString(model)
    }

    fun convertStringToJsonNode(jsonString: String?): JsonNode? {
        val mapper = objectMapper
        return mapper.readTree(jsonString)
    }

    fun <T> convertStringToModel(jsonString: String?, clazz: Class<T>): T? {
        val mapper = objectMapper
        return mapper.readValue(jsonString, clazz)
    }
}