package io.github.sergkhram.idbClient.entities.requestsBody

import idb.XctestRunRequest

sealed class XctestRunRequestBody {
    abstract val requestBody: XctestRunRequest

    data class LogicXctestRequestBody(
        val testBundleId: String,
        val reportActivities: Boolean,
        val reportAttachments: Boolean,
        val collectCoverage: Boolean,
        val collectLogs: Boolean,
        val waitForDebugger: Boolean,
        val collectResultBundle: Boolean,
        val coverageFormat: CodeCoverageFormat?, //required for collectCoverage = true
        val testsToRun: List<String> = emptyList(),
        val testsToSkip: List<String> = emptyList(),
        val env: Map<String, String> = emptyMap(),
        val args: List<String> = emptyList(),
        val timeout: Long = 0L,
    ) : XctestRunRequestBody() {
        override val requestBody = XctestRunRequest.newBuilder()
            .setMode(XctestRunRequest.Mode.newBuilder().setLogic(XctestRunRequest.Logic.getDefaultInstance()))
            .setTestBundleId(testBundleId)
            .setReportActivities(reportActivities)
            .setReportAttachments(reportAttachments)
            .setCollectCoverage(collectCoverage)
            .setCollectLogs(collectLogs)
            .setWaitForDebugger(waitForDebugger)
            .setCollectResultBundle(collectResultBundle)
            .putAllEnvironment(env)
            .addAllArguments(args)
            .setTimeout(timeout)
            .addAllTestsToRun(testsToRun)
            .addAllTestsToSkip(testsToSkip)
            .build().apply {
                if(collectCoverage) {
                    this.toBuilder().setCodeCoverage(
                        XctestRunRequest.CodeCoverage.newBuilder().setFormatValue(coverageFormat?.value ?: 0).build()
                    ).build()
                }
            }
    }

    data class UIXctestRequestBody(
        val testBundleId: String,
        val appBundleId: String,
        val reportActivities: Boolean,
        val reportAttachments: Boolean,
        val collectCoverage: Boolean,
        val collectLogs: Boolean,
        val waitForDebugger: Boolean,
        val collectResultBundle: Boolean,
        val coverageFormat: CodeCoverageFormat?, //required for collectCoverage = true
        val testHostAppBundleId: String = "",
        val testsToRun: List<String> = emptyList(),
        val testsToSkip: List<String> = emptyList(),
        val env: Map<String, String> = emptyMap(),
        val args: List<String> = emptyList(),
        val timeout: Long = 0L,
    ) : XctestRunRequestBody() {
        override val requestBody = XctestRunRequest.newBuilder()
            .setMode(
                XctestRunRequest.Mode.newBuilder()
                    .setUi(
                        XctestRunRequest.UI.newBuilder()
                            .setAppBundleId(appBundleId)
                            .setTestHostAppBundleId(testHostAppBundleId)
                            .build()
                    )
            )
            .setTestBundleId(testBundleId)
            .setReportActivities(reportActivities)
            .setReportAttachments(reportAttachments)
            .setCollectCoverage(collectCoverage)
            .setCollectLogs(collectLogs)
            .setWaitForDebugger(waitForDebugger)
            .setCollectResultBundle(collectResultBundle)
            .putAllEnvironment(env)
            .addAllArguments(args)
            .setTimeout(timeout)
            .addAllTestsToRun(testsToRun)
            .addAllTestsToSkip(testsToSkip)
            .build().apply {
                if(collectCoverage) {
                    this.toBuilder().setCodeCoverage(
                        XctestRunRequest.CodeCoverage.newBuilder().setFormatValue(coverageFormat?.value ?: 0).build()
                    ).build()
                }
            }
    }

    data class ApplicationXctestRequestBody(
        val testBundleId: String,
        val appBundleId: String,
        val reportActivities: Boolean,
        val reportAttachments: Boolean,
        val collectCoverage: Boolean,
        val collectLogs: Boolean,
        val waitForDebugger: Boolean,
        val collectResultBundle: Boolean,
        val coverageFormat: CodeCoverageFormat?, //required for collectCoverage = true
        val testHostAppBundleId: String = "",
        val testsToRun: List<String> = emptyList(),
        val testsToSkip: List<String> = emptyList(),
        val env: Map<String, String> = emptyMap(),
        val args: List<String> = emptyList(),
        val timeout: Long = 0L,
    ) : XctestRunRequestBody() {
        override val requestBody = XctestRunRequest.newBuilder()
            .setMode(
                XctestRunRequest.Mode.newBuilder()
                    .setApplication(
                        XctestRunRequest.Application.newBuilder()
                            .setAppBundleId(appBundleId)
                            .build()
                    )
            )
            .setTestBundleId(testBundleId)
            .setReportActivities(reportActivities)
            .setReportAttachments(reportAttachments)
            .setCollectCoverage(collectCoverage)
            .setCollectLogs(collectLogs)
            .setWaitForDebugger(waitForDebugger)
            .setCollectResultBundle(collectResultBundle)
            .putAllEnvironment(env)
            .addAllArguments(args)
            .setTimeout(timeout)
            .addAllTestsToRun(testsToRun)
            .addAllTestsToSkip(testsToSkip)
            .build().apply {
                if(collectCoverage) {
                    this.toBuilder().setCodeCoverage(
                        XctestRunRequest.CodeCoverage.newBuilder().setFormatValue(coverageFormat?.value ?: 0).build()
                    ).build()
                }
            }
    }
}