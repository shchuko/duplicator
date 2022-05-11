package logging

import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Level
import java.util.logging.Logger

fun Logger.log(level: Level, e: Throwable, prefix: String = "") {
    val stringWriter = StringWriter()
    e.printStackTrace(PrintWriter(stringWriter))
    log(level, "${if (prefix.isNotBlank()) "$prefix: " else ""}$stringWriter")
}
