package io.github.sergkhram.idbClient

import io.github.sergkhram.idbClient.entities.address.TcpAddress
import org.junit.platform.commons.logging.LoggerFactory

abstract class BaseTest {
    protected val log = LoggerFactory.getLogger(this::class.java)
    protected val address = TcpAddress("127.0.0.1", 10882)
}