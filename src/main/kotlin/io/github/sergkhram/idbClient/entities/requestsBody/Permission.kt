package io.github.sergkhram.idbClient.entities.requestsBody

enum class Permission(val value: Int) {
    PHOTOS(0),
    CAMERA(1),
    CONTACTS(2),
    URL(3),
    LOCATION(4),
    NOTIFICATION(5),
    MICROPHONE(6)
}