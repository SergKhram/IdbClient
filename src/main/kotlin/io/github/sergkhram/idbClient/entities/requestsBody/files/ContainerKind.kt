package io.github.sergkhram.idbClient.entities.requestsBody.files

enum class ContainerKind(val value: Int) {
    NONE(0),
    APPLICATION(1),
    ROOT(2),
    MEDIA(3),
    CRASHES(4),
    PROVISIONING_PROFILES(5),
    MDM_PROFILES(6),
    SPRINGBOARD_ICONS(7),
    WALLPAPER(8),
    DISK_IMAGES(9),
    GROUP_CONTAINER(10),
    APPLICATION_CONTAINER(11),
    AUXILLARY(12),
    XCTEST(13),
    DYLIB(14),
    DSYM(15),
    FRAMEWORK(16),
    SYMBOLS(17)
}