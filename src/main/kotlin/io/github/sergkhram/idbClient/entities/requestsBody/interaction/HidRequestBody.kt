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
    data class TextCmdHidRequestBody(val text: String) : HidRequestBody() {
        override val requestBody = text.flatMap {
            mapOfCodes[it] ?: emptyList()
        }.asFlow()
    }
    data class TapCmdRequestBody(val x: Double, val y: Double, val duration: Double = 0.0) : HidRequestBody() {
        override val requestBody = prepareListOfTapEvents(x, y, duration).asFlow()

        private fun prepareListOfTapEvents(x: Double, y: Double, duration: Double): List<HIDEvent> {
            val list = mutableListOf<HIDEvent>()
            val action = HIDPressAction.newBuilder()
                .setTouch(
                    HIDEvent.HIDTouch.newBuilder().setPoint(Point.newBuilder().setY(y).setX(x)).build()
                )
                .build()
            list.add(
                HIDEvent.newBuilder()
                .setPress(
                    HIDPress.newBuilder().setAction(action).setDirection(HIDEvent.HIDDirection.DOWN).build()
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
                    HIDPress.newBuilder().setAction(action).setDirection(HIDEvent.HIDDirection.UP).build()
                )
                .build()
            )
            return list
        }
    }
    data class ButtonPressCmdRequestBody(val button: AppleButton, val duration: Double = 0.0) : HidRequestBody() {
        override val requestBody = prepareListOfButtonClickEvents(button, duration).asFlow()

        private fun prepareListOfButtonClickEvents(button: AppleButton, duration: Double): List<HIDEvent> {
            val list = mutableListOf<HIDEvent>()
            val action = HIDPressAction.newBuilder()
                .setButton(
                    HIDEvent.HIDButton.newBuilder().setButtonValue(button.value).build()
                )
                .build()
            list.add(
                HIDEvent.newBuilder()
                    .setPress(
                        HIDPress.newBuilder().setAction(action).setDirection(HIDEvent.HIDDirection.DOWN).build()
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
                        HIDPress.newBuilder().setAction(action).setDirection(HIDEvent.HIDDirection.UP).build()
                    )
                    .build()
            )
            return list
        }
    }
    data class KeyPressCmdRequestBody(val code: Int, val duration: Double = 0.0) : HidRequestBody() {
        override val requestBody = prepareKeyCodeEvents(code, duration).asFlow()
    }
    data class KeysPressCmdRequestBody(val keys: List<Int>) : HidRequestBody() {
        override val requestBody = keys.flatMap { prepareKeyCodeEvents(it) }.asFlow()
    }
    data class SwipeCmdRequestBody(
        val startX: Double,
        val startY: Double,
        val endX: Double,
        val endY: Double,
        val deltaValue: Double = 0.0,
        val durationValue: Double = 0.0
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
