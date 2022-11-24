package io.github.sergkhram.idbClient

import io.github.sergkhram.idbClient.entities.address.TcpAddress
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory

@ExtendWith(SoftAssertionsExtension::class)
abstract class BaseTest {
    protected val log: Logger = LoggerFactory.getLogger(this::class.java)
    protected val address = TcpAddress("127.0.0.1", 10882)
}