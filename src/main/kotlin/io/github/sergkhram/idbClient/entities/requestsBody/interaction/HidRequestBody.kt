package io.github.sergkhram.idbClient.entities.requestsBody.interaction

import idb.HIDEvent
import idb.HIDEvent.HIDPress
import idb.HIDEvent.HIDPressAction
import idb.Point
import io.github.sergkhram.idbClient.entities.requestsBody.interaction.AppleCodeEvents.mapOfCodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

sealed class HidRequestBody {
    abstract val requestBody: Flow<HIDEvent>

    /**
     * Input text
     * @param text - Text to input
     */
    class TextCmdHidRequestBody(text: String) : HidRequestBody() {
        override val requestBody = text.flatMap {
            mapOfCodes[it] ?: emptyList()
        }.asFlow()
    }

    /**
     * Tap On the Screen
     * @param x - The x-coordinate
     * @param y - The y-coordinate
     * @param duration - Press duration
     */
    class TapCmdRequestBody(x: Double, y: Double, duration: Double = 0.0) : HidRequestBody() {
        override val requestBody = prepareListOfTapEvents(x, y, duration).asFlow()

        private fun prepareListOfTapEvents(x: Double, y: Double, duration: Double): List<HIDEvent> {
            val list = mutableListOf<HIDEvent>()
            val action = HIDPressAction.newBuilder()
                .setTouch(
                    HIDEvent.HIDTouch.newBuilder()
                        .setPoint(
                            Point.newBuilder().setY(y).setX(x)
                        ).build()
                )
                .build()
            list.add(
                HIDEvent.newBuilder()
                .setPress(
                    HIDPress.newBuilder()
                        .setAction(action)
                        .setDirection(HIDEvent.HIDDirection.DOWN)
                        .build()
                )
                .build()
            )
            if(!duration.equals(0.0)) {
                list.add(
                    durationEvent(duration)
                )
            }
            list.add(
                HIDEvent.newBuilder()
                .setPress(
                    HIDPress.newBuilder()
                        .setAction(action)
                        .setDirection(HIDEvent.HIDDirection.UP)
                        .build()
                )
                .build()
            )
            return list
        }
    }

    /**
     * A single press of a button
     * @param button - The button name
     * @param duration - Press duration
     */
    class ButtonPressCmdRequestBody(button: AppleButton, duration: Double = 0.0) : HidRequestBody() {
        override val requestBody = prepareListOfButtonClickEvents(button, duration).asFlow()

        private fun prepareListOfButtonClickEvents(button: AppleButton, duration: Double): List<HIDEvent> {
            val list = mutableListOf<HIDEvent>()
            val action = HIDPressAction.newBuilder()
                .setButton(
                    HIDEvent.HIDButton.newBuilder()
                        .setButtonValue(button.value)
                        .build()
                )
                .build()
            list.add(
                HIDEvent.newBuilder()
                    .setPress(
                        HIDPress.newBuilder()
                            .setAction(action)
                            .setDirection(HIDEvent.HIDDirection.DOWN)
                            .build()
                    )
                    .build()
            )
            if(!duration.equals(0.0)) {
                list.add(
                    durationEvent(duration)
                )
            }
            list.add(
                HIDEvent.newBuilder()
                    .setPress(
                        HIDPress.newBuilder()
                            .setAction(action)
                            .setDirection(HIDEvent.HIDDirection.UP)
                            .build()
                    )
                    .build()
            )
            return list
        }
    }

    /**
     * A short press of a keycode
     * @param code - The key code
     * @param duration - Press duration
     */
    class KeyPressCmdRequestBody(code: Int, duration: Double = 0.0) : HidRequestBody() {
        override val requestBody = prepareKeyCodeEvents(code, duration).asFlow()
    }

    /**
     * A sequence of short presses of a keycode
     * @param keys - list of key codes
     */
    class KeysPressCmdRequestBody(keys: List<Int>) : HidRequestBody() {
        override val requestBody = keys.flatMap { prepareKeyCodeEvents(it) }.asFlow()
    }

    /**
     * Swipe from one point to another point
     * @param startX - The x-coordinate of the swipe start point
     * @param startY - The y-coordinate of the swipe start point
     * @param endX - The x-coordinate of the swipe end point
     * @param endY - The y-coordinate of the swipe end point
     * @param deltaValue - delta in pixels between every touch point on the line between start and end points
     * @param durationValue - Swipe duration
     */
    class SwipeCmdRequestBody(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        deltaValue: Double = 0.0,
        durationValue: Double = 0.0
    ) : HidRequestBody() {
        override val requestBody: Flow<HIDEvent> = listOf(
            HIDEvent.newBuilder()
                .setSwipe(
                    HIDEvent.HIDSwipe.newBuilder().apply {
                        this.start = Point.newBuilder().setX(startX).setY(startY).build()
                        this.end = Point.newBuilder().setX(endX).setY(endY).build()
                        if(!deltaValue.equals(0.0)) {
                            this.delta = deltaValue
                        }
                        if(!durationValue.equals(0.0)) {
                            this.duration = durationValue
                        }
                    }.build()
                )
                .build()
        ).asFlow()
    }

    protected val durationEvent: (Double) -> HIDEvent = {
        HIDEvent.newBuilder()
            .setDelay(
                HIDEvent.HIDDelay.newBuilder()
                    .setDuration(it)
                    .build()
            )
            .build()
    }
    protected fun prepareKeyCodeEvents(keyCode: Int, duration: Double = 0.0): List<HIDEvent> {
        val list = mutableListOf<HIDEvent>()
        val action = HIDPressAction.newBuilder()
            .setKey(
                HIDEvent.HIDKey.newBuilder()
                    .setKeycode(keyCode.toLong())
                    .build()
            ).build()
        list.add(
            HIDEvent.newBuilder().setPress(
                HIDPress.newBuilder()
                    .setAction(
                        action
                    )
                    .setDirection(HIDEvent.HIDDirection.DOWN)
                    .build()
            ).build()
        )
        if(!duration.equals(0.0)) {
            list.add(
                durationEvent(duration)
            )
        }
        list.add(
            HIDEvent.newBuilder().setPress(
                HIDPress.newBuilder()
                    .setAction(
                        action
                    )
                    .setDirection(HIDEvent.HIDDirection.UP)
                    .build()
            ).build()
        )
        return list
    }
}
