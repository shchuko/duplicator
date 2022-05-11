import logging.UncaughtExceptionHandlerImpl
import javax.swing.SwingUtilities

fun main() {
    val handler = UncaughtExceptionHandlerImpl()
    Thread.setDefaultUncaughtExceptionHandler(handler)

    SwingUtilities.invokeLater {
        Presenter(ViewModel("Duplicator"), handler)
    }
}
