package io.github.sergkhram.idbClient.entities.requestsBody.app

import idb.LaunchRequest

sealed class LaunchRequestBody {
    abstract val requestBody: LaunchRequest

    /**
     * Launch an application. Any environment variables of the form IDB_X will be passed through with the IDB_ prefix removed.
     * @param bundleId Bundle id of the app to launch
     * @param env env map
     * @param appArgs Arguments to start the app with
     * @param foregroundIfRunning If the app is already running foreground that process
     * @param waitFor Wait for the process to exit, tailing all output from the app
     * @param waitForDebugger Suspend application right after the launch to facilitate attaching of a debugger (ex, lldb).
     */
    class StartLaunchRequestBody(
        bundleId: String,
        env: Map<String, String> = emptyMap(),
        appArgs: List<String> = emptyList(),
        foregroundIfRunning: Boolean = false,
        waitFor: Boolean = false,
        waitForDebugger: Boolean = false
    ): LaunchRequestBody(){
        override val requestBody: LaunchRequest = LaunchRequest.newBuilder()
            .setStart(
                LaunchRequest.Start.newBuilder()
                    .setBundleId(bundleId)
                    .putAllEnv(env)
                    .addAllAppArgs(appArgs)
                    .setForegroundIfRunning(foregroundIfRunning)
                    .setWaitFor(waitFor)
                    .setWaitForDebugger(waitForDebugger)
            )
            .build()
    }

    internal class StopLaunchRequestBody: LaunchRequestBody(){
        override val requestBody: LaunchRequest = LaunchRequest.newBuilder()
            .setStop(
                LaunchRequest.Stop.getDefaultInstance()
            )
            .build()
    }
}
