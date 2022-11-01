package io.github.sergkhram.idbClient.handlers

import io.github.sergkhram.idbClient.BaseTest
import io.github.sergkhram.idbClient.handlers.GrpcErrorHandler.handle
import io.github.sergkhram.idbClient.util.NoCompanionWithUdidException
import io.grpc.Status.fromThrowable
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.nio.channels.ClosedChannelException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class GrpcErrorHandlerTest: BaseTest() {

    @Test
    fun checkHandleWithoutExceptionAfterCatchingTest() {
        runBlocking {
            val bool = AtomicBoolean(false)
            val additionalBool = AtomicBoolean(false)
            val action = {
                if(!bool.get()) {
                    bool.set(true)
                    throw StatusException(
                        fromThrowable(ClosedChannelException())
                    )
                }
                bool.getAndSet(false)
            }
            val actionAfterCatch = {
                additionalBool.set(true)
            }
            var actual = false

            assertDoesNotThrow {
                actual = handle(
                    actionAfterCatch,
                    { true },
                    ClosedChannelException::class,
                    action
                )
            }
            Assertions.assertEquals(
                true,
                actual,
                "Returned value is incorrect"
            )
            Assertions.assertEquals(
                true,
                additionalBool.get(),
                "After catch' action hasn't been executed"
            )
            Assertions.assertEquals(
                false,
                bool.get(),
                "Action hasn't been executed the second time"
            )
        }
    }

    @Test
    fun checkHandleWithDoubleExceptionCatchingTest() {
        runBlocking {
            val int = AtomicInteger(0)
            val additionalBool = AtomicBoolean(false)
            val action = {
                int.getAndIncrement()
                throw StatusException(
                    fromThrowable(ClosedChannelException())
                )
                int.get()
            }
            val actionAfterCatch = {
                additionalBool.set(true)
            }
            var actual = 0

            assertThrows<StatusException> {
                actual = handle(
                    actionAfterCatch,
                    { true },
                    ClosedChannelException::class,
                    action
                )
            }
            Assertions.assertEquals(
                0,
                actual,
                "Returned value is incorrect"
            )
            Assertions.assertEquals(
                true,
                additionalBool.get(),
                "After catch' action hasn't been executed"
            )
            Assertions.assertEquals(
                2,
                int.get(),
                "Action hasn't been executed the second time"
            )
        }
    }

    @Test
    fun checkHandleWithAnotherExceptionTypeTest() {
        runBlocking {
            val bool = AtomicBoolean(false)
            val additionalBool = AtomicBoolean(false)
            val action = {
                if(!bool.get()) {
                    bool.set(true)
                    throw StatusException(
                        fromThrowable(ClosedChannelException())
                    )
                }
                bool.getAndSet(false)
            }
            val actionAfterCatch = {
                additionalBool.set(true)
            }
            var actual = false

            assertThrows<StatusException> {
                actual = handle(
                    actionAfterCatch,
                    { true },
                    NoCompanionWithUdidException::class,
                    action
                )
            }
            Assertions.assertEquals(
                false,
                actual,
                "Returned value is incorrect"
            )
            Assertions.assertEquals(
                false,
                additionalBool.get(),
                "After catch' action shouldn't be executed"
            )
            Assertions.assertEquals(
                true,
                bool.get(),
                "Action hasn't been executed one time only"
            )
        }
    }

    @Test
    fun checkHandleWithoutAnyExceptionTest() {
        runBlocking {
            val int = AtomicInteger(0)
            val additionalBool = AtomicBoolean(false)
            val action = {
                int.incrementAndGet()
            }
            val actionAfterCatch = {
                additionalBool.set(true)
            }
            var actual = 0

            assertDoesNotThrow {
                actual = handle(
                    actionAfterCatch,
                    { true },
                    StatusException::class,
                    action
                )
            }
            Assertions.assertEquals(
                1,
                actual,
                "Returned value is incorrect"
            )
            Assertions.assertEquals(
                false,
                additionalBool.get(),
                "After catch' action shouldn't be executed"
            )
            Assertions.assertEquals(
                1,
                int.get(),
                "Action hasn't been executed one time only"
            )
        }
    }

    @Test
    fun checkHandleWithExceptionWFalsePredicateTest() {
        runBlocking {
            val bool = AtomicBoolean(false)
            val additionalBool = AtomicBoolean(false)
            val action = {
                if(!bool.get()) {
                    bool.set(true)
                    throw StatusException(
                        fromThrowable(ClosedChannelException())
                    )
                }
                bool.getAndSet(false)
            }
            val actionAfterCatch = {
                additionalBool.set(true)
            }
            var actual = false

            assertThrows<StatusException> {
                actual = handle(
                    actionAfterCatch,
                    { false },
                    ClosedChannelException::class,
                    action
                )
            }
            Assertions.assertEquals(
                false,
                actual,
                "Returned value is incorrect"
            )
            Assertions.assertEquals(
                false,
                additionalBool.get(),
                "After catch' action shouldn't be executed"
            )
            Assertions.assertEquals(
                true,
                bool.get(),
                "Action hasn't been executed one time only"
            )
        }
    }

    @Test
    fun checkHandleWithExceptionAfterCatchingTest() {
        runBlocking {
            val int = AtomicInteger(0)
            val additionalBool = AtomicBoolean(false)
            val action = {
                int.getAndIncrement()
                throw StatusException(
                    fromThrowable(ClosedChannelException())
                )
                int.get()
            }
            val actionAfterCatch = {
                additionalBool.set(true)
                throw NoCompanionWithUdidException("1")
            }
            var actual = 0

            assertThrows<NoCompanionWithUdidException> {
                actual = handle(
                    actionAfterCatch,
                    { true },
                    ClosedChannelException::class,
                    action
                )
            }
            Assertions.assertEquals(
                0,
                actual,
                "Returned value is incorrect"
            )
            Assertions.assertEquals(
                true,
                additionalBool.get(),
                "After catch' action hasn't been executed"
            )
            Assertions.assertEquals(
                1,
                int.get(),
                "Action hasn't been executed one time only"
            )
        }
    }
}