package io.github.sergkhram.idbClient.ssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.Session
import com.jcraft.jsch.JSch
import io.github.sergkhram.idbClient.logs.KLogger
import java.util.concurrent.Executors

internal object SSHExecutor {
    private val log = KLogger.logger
    private const val connectionTimeout = 5000

    private val executorService = Executors.newCachedThreadPool {
        Executors.defaultThreadFactory().newThread(it).apply {
            isDaemon = true
        }
    }

    @Synchronized
    fun execute(config: SSHConfig, cmd: String): CmdResult {
        log.debug("Executing ssh task ${config.host}")

        return config.withSession {
            sshExec(this, cmd)
        }
    }

    private fun sshExec(session: Session, cmd: String): CmdResult {
        log.debug("Executing '$cmd'")

        val exec = session.openChannel("exec") as ChannelExec

        with (exec) {
            setCommand(cmd)
            connect()
        }

        return CmdResult().apply {
            try {
                val stdin = executorService.submit {
                    this.output = String(exec.inputStream.readBytes())
                }
                val stderr = executorService.submit {
                    this.error = String(exec.errStream.readBytes())
                }

                stdin.get()
                stderr.get()
            } finally {
                this.exitCode = exec.exitStatus
                exec.disconnect()
            }
        }
    }

    private fun SSHConfig.withSession(block: Session.() -> CmdResult): CmdResult {
        JSch().getSession(user, host, port).apply {
            this.setConfig("StrictHostKeyChecking", "no")
            this.setPassword(password)
            log.debug("Connecting to SSH host $host:$port")
            connect(connectionTimeout)
            val result: CmdResult
            try {
                result = block(this)
            } finally {
                log.debug("Disconnecting SSH session")
                disconnect()
            }
            return result
        }
    }

}