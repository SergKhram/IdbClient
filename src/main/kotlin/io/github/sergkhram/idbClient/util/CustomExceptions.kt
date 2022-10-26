package io.github.sergkhram.idbClient.util

class NoCompanionWithUdidException(udid: String): Exception("There is no companion with $udid")