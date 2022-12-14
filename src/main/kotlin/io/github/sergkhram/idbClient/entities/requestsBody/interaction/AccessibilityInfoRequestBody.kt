package io.github.sergkhram.idbClient.entities.requestsBody.interaction

import idb.AccessibilityInfoRequest

sealed class AccessibilityInfoRequestBody {
    abstract val requestBody: AccessibilityInfoRequest

    /**
     * Describes Accessibility Information for the entire screen
     * @param format Will report data in the newer nested format, rather than the flat one
     */
    class AccessibilityInfoAllRequestBody(format: Format = Format.NESTED): AccessibilityInfoRequestBody() {
        override val requestBody: AccessibilityInfoRequest = AccessibilityInfoRequest.newBuilder()
            .setFormatValue(format.value)
            .build()
    }

    /**
     * Describes Accessibility Information at a point on the screen
     * @param format Will report data in the newer nested format, rather than the flat one
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    class AccessibilityInfoPointRequestBody(format: Format = Format.NESTED, x: Double, y: Double): AccessibilityInfoRequestBody() {
        override val requestBody: AccessibilityInfoRequest = AccessibilityInfoRequest.newBuilder()
            .setFormatValue(format.value)
            .setPoint(
                idb.Point.newBuilder().setX(x).setY(y).build()
            )
            .build()
    }
}