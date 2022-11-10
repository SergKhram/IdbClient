package io.github.sergkhram.idbClient.util

class NoCompanionWithUdidException(udid: String): Exception("There is no companion with $udid")
class NoSimulatorsOnHostException(host: String): Exception("There are no simulators on this host: $host")
class GetHostTargetsException(host: String, port: Int, error: String):
    Exception("Get host' targets process failed for $host:$port with reason: $error")