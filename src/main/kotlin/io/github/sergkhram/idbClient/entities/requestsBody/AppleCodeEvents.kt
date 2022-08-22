package io.github.sergkhram.idbClient.entities.requestsBody

import idb.HIDEvent
import idb.HIDEvent.HIDKey

object AppleCodeEvents {
    val mapOfCodes = mapOf<Char, List<HIDEvent>>(
        'a' to prepareKeyEvent(4),
        'b' to prepareKeyEvent(5),
        'c' to prepareKeyEvent(6),
        'd' to prepareKeyEvent(7),
        'e' to prepareKeyEvent(8),
        'f' to prepareKeyEvent(9),
        'g' to prepareKeyEvent(10),
        'h' to prepareKeyEvent(11),
        'i' to prepareKeyEvent(12),
        'j' to prepareKeyEvent(13),
        'k' to prepareKeyEvent(14),
        'l' to prepareKeyEvent(15),
        'm' to prepareKeyEvent(16),
        'n' to prepareKeyEvent(17),
        'o' to prepareKeyEvent(18),
        'p' to prepareKeyEvent(19),
        'q' to prepareKeyEvent(20),
        'r' to prepareKeyEvent(21),
        's' to prepareKeyEvent(22),
        't' to prepareKeyEvent(23),
        'u' to prepareKeyEvent(24),
        'v' to prepareKeyEvent(25),
        'w' to prepareKeyEvent(26),
        'x' to prepareKeyEvent(27),
        'y' to prepareKeyEvent(28),
        'z' to prepareKeyEvent(29),
        '1' to prepareKeyEvent(30),
        '2' to prepareKeyEvent(31),
        '3' to prepareKeyEvent(32),
        '4' to prepareKeyEvent(33),
        '5' to prepareKeyEvent(34),
        '6' to prepareKeyEvent(35),
        '7' to prepareKeyEvent(36),
        '8' to prepareKeyEvent(37),
        '9' to prepareKeyEvent(38),
        '0' to prepareKeyEvent(39),
        '\n' to prepareKeyEvent(40),
        ';' to prepareKeyEvent(51),
        '=' to prepareKeyEvent(46),
        ',' to prepareKeyEvent(54),
        '-' to prepareKeyEvent(45),
        '.' to prepareKeyEvent(55),
        '/' to prepareKeyEvent(56),
        '`' to prepareKeyEvent(53),
        '[' to prepareKeyEvent(47),
        '\\' to prepareKeyEvent(49),
        ']' to prepareKeyEvent(48),
        '\'' to prepareKeyEvent(52),
        ' ' to prepareKeyEvent(44),
        'A' to prepareShiftKeyEvent(4),
        'B' to prepareShiftKeyEvent(5),
        'C' to prepareShiftKeyEvent(6),
        'D' to prepareShiftKeyEvent(7),
        'E' to prepareShiftKeyEvent(8),
        'F' to prepareShiftKeyEvent(9),
        'G' to prepareShiftKeyEvent(10),
        'H' to prepareShiftKeyEvent(11),
        'I' to prepareShiftKeyEvent(12),
        'J' to prepareShiftKeyEvent(13),
        'K' to prepareShiftKeyEvent(14),
        'L' to prepareShiftKeyEvent(15),
        'M' to prepareShiftKeyEvent(16),
        'N' to prepareShiftKeyEvent(17),
        'O' to prepareShiftKeyEvent(18),
        'P' to prepareShiftKeyEvent(19),
        'Q' to prepareShiftKeyEvent(20),
        'R' to prepareShiftKeyEvent(21),
        'S' to prepareShiftKeyEvent(22),
        'T' to prepareShiftKeyEvent(23),
        'U' to prepareShiftKeyEvent(24),
        'V' to prepareShiftKeyEvent(25),
        'W' to prepareShiftKeyEvent(26),
        'X' to prepareShiftKeyEvent(27),
        'Y' to prepareShiftKeyEvent(28),
        'Z' to prepareShiftKeyEvent(29),
        '!' to prepareShiftKeyEvent(30),
        '@' to prepareShiftKeyEvent(31),
        '#' to prepareShiftKeyEvent(32),
        '$' to prepareShiftKeyEvent(33),
        '%' to prepareShiftKeyEvent(34),
        '^' to prepareShiftKeyEvent(35),
        '&' to prepareShiftKeyEvent(36),
        '*' to prepareShiftKeyEvent(37),
        '(' to prepareShiftKeyEvent(38),
        ')' to prepareShiftKeyEvent(39),
        '_' to prepareShiftKeyEvent(45),
        '+' to prepareShiftKeyEvent(46),
        '{' to prepareShiftKeyEvent(47),
        '}' to prepareShiftKeyEvent(48),
        ':' to prepareShiftKeyEvent(51),
        '"' to prepareShiftKeyEvent(52),
        '|' to prepareShiftKeyEvent(49),
        '<' to prepareShiftKeyEvent(54),
        '>' to prepareShiftKeyEvent(55),
        '?' to prepareShiftKeyEvent(56),
        '~' to prepareShiftKeyEvent(53)
    )

    private fun prepareKeyEvent(i: Int): List<HIDEvent> {
        val list = mutableListOf<HIDEvent>()
        val action = HIDEvent.HIDPressAction.newBuilder()
            .setKey(
                HIDKey.newBuilder()
                    .setKeycode(i.toLong())
                    .build()
            ).build()
        list.add(
            HIDEvent.newBuilder().setPress(
                HIDEvent.HIDPress.newBuilder()
                    .setAction(
                        action
                    )
                    .setDirection(HIDEvent.HIDDirection.DOWN)
                    .build()
            ).build()
        )
        list.add(
            HIDEvent.newBuilder().setPress(
                HIDEvent.HIDPress.newBuilder()
                    .setAction(
                        action
                    )
                    .setDirection(HIDEvent.HIDDirection.UP)
                    .build()
            ).build()
        )
        return list
    }
    private fun prepareShiftKeyEvent(i: Int) : List<HIDEvent> {
        val list = mutableListOf<HIDEvent>()
        val action = HIDEvent.HIDPressAction.newBuilder()
            .setKey(
                HIDKey.newBuilder()
                    .setKeycode(225)
                    .build()
            ).build()
        list.add(
            HIDEvent.newBuilder().setPress(
                HIDEvent.HIDPress.newBuilder()
                    .setAction(
                        action
                    )
                    .setDirection(HIDEvent.HIDDirection.DOWN)
                    .build()
            ).build()
        )
        list.addAll(prepareKeyEvent(i))
        list.add(
            HIDEvent.newBuilder().setPress(
                HIDEvent.HIDPress.newBuilder()
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