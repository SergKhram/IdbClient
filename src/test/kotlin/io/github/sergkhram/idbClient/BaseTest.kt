package io.github.sergkhram.idbClient

import org.junit.platform.commons.logging.LoggerFactory

abstract class BaseTest {
    protected val log = LoggerFactory.getLogger(this::class.java)
}