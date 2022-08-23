package io.github.sergkhram.idbClient.entities.requestsBody.crash

class CrashLogQueryRequestBody(val since: Long = 0, val before: Long = 0, val bundleId: String = "", val name: String = "")