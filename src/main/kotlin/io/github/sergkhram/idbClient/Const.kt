package io.github.sergkhram.idbClient

object Const {
    const val localIdbCompanionPath = "/usr/local/bin/idb_companion"
    val localTargetsListCmd = listOf(localIdbCompanionPath, "--list", "1", "--json")
    val startLocalCompanionCmd: (String, Int) -> List<String> = { udid, port ->
        listOf(localIdbCompanionPath, "--udid", udid, "--grpc-port", port.toString())
    }
    val noCompanionWithUdid: (String) -> NoSuchElementException = {
        NoSuchElementException("There is no companion with udid $it")
    }
    const val localHost = "127.0.0.1"
    const val localGrpcStartTimeout = 7000L
}