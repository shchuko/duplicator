import logging.UncaughtExceptionHandlerImpl
import javax.swing.SwingUtilities

class Main {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val handler = UncaughtExceptionHandlerImpl()
            Thread.setDefaultUncaughtExceptionHandler(handler)

            SwingUtilities.invokeLater {
                Presenter(ViewModel("Duplicator"), handler)
            }
        }
    }
}
