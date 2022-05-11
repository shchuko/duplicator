package subprocess

import subprocess.exception.SubprocessExecutionFailedException
import subprocess.exception.SubprocessKilledOnTimeoutException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

private val commonLogger = Logger.getLogger("subprocess.common")

typealias Command = List<String>

fun Command.runSubprocess(
    logger: Logger = commonLogger,
    timeout: Long,
    timeUnit: TimeUnit
): String {
    require(timeout > 0)
    logger.log(Level.INFO, "Executing subprocess '${this.joinToString(" ")}'")
    val proc = ProcessBuilder(*this.toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    logger.log(Level.INFO, "Waiting for subprocess")
    proc.waitFor(timeout, timeUnit)
    if (proc.isAlive) {
        logger.log(Level.WARNING, "Subprocess still alive, destroying on timeout $timeout${timeUnit.name}")
        proc.destroy()
        throw SubprocessKilledOnTimeoutException(timeout, timeUnit)
    }

    if (proc.exitValue() != 0) {
        logger.log(Level.WARNING, "Subprocess execution finished with non-zero exit code ${proc.exitValue()}")
        throw SubprocessExecutionFailedException(proc.exitValue(), proc.errorStream.bufferedReader().readText())
    }
    logger.log(Level.INFO, "Subprocess execution finished with exit code 0")
    return proc.inputStream.bufferedReader().readText()
}