package io.github.sergkhram.idbClient.entities.requestsBody.interaction

import idb.AccessibilityInfoRequest

sealed class AccessibilityInfoRequestBody {
    abstract val requestBody: AccessibilityInfoRequest

    //Describes Accessibility Information for the entire screen
    data class AccessibilityInfoAllRequestBody(val format: Format): AccessibilityInfoRequestBody() {
        override val requestBody: AccessibilityInfoRequest = AccessibilityInfoRequest.newBuilder()
            .setFormatValue(format.value)
            .build()
    }

    //Describes Accessibility Information at a point on the screen
    data class AccessibilityInfoPointRequestBody(val format: Format, val x: Double, val y: Double): AccessibilityInfoRequestBody() {
        override val requestBody: AccessibilityInfoRequest = AccessibilityInfoRequest.newBuilder()
            .setFormatValue(format.value)
            .setPoint(
                idb.Point.newBuilder().setX(x).setY(y).build()
            )
            .build()
    }
}