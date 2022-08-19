package io.github.sergkhram.idbClient

object Const {
    const val localIdbCompanionPath = "/usr/local/bin/idb_companion"
    val localTargetsListCmd = listOf(localIdbCompanionPath, "--list", "1", "--json")
    val startLocalCompanionCmd: (Pair<String, Int>) -> List<String> = { pair ->
        listOf(localIdbCompanionPath, "--udid", pair.first, "--grpc-port", pair.second.toString(), "> /dev/null 2>&1")
    }
    val noCompanionWithUdid: (String) -> NoSuchElementException = {
        NoSuchElementException("There is no companion with udid $it")
    }
}