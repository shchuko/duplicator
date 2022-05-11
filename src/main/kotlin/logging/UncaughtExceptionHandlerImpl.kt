package logging

import java.lang.Thread.UncaughtExceptionHandler
import java.util.logging.Level
import java.util.logging.Logger

internal class UncaughtExceptionHandlerImpl : UncaughtExceptionHandler {
    companion object {
        private val LOGGER = Logger.getLogger(UncaughtExceptionHandlerImpl::class.java.name)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            LOGGER.log(Level.SEVERE, e, "Uncaught exception on thread $t:${t.id}")
        } catch (ignored: Throwable) {
            /* We've lost the last chance to log the throwable :crying_cat: */
        }
    }
}
