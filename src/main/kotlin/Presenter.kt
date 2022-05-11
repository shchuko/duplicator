import logging.log
import swingUtils.openFileChooser
import vpn.VpnConnection
import vpn.exception.VpnException
import vpn.listVpnConnections
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import javax.swing.JOptionPane

class Presenter(private val viewModel: ViewModel, uncaughtExceptionHandler: UncaughtExceptionHandler) :
    ViewModel.Listener {
    private val logger: Logger = Logger.getLogger(this::class.java.name)

    init {
        viewModel.addListener(this)

        viewModel.filesList.add("/test/file/1")
        viewModel.filesList.add("/test/file/2")
        viewModel.filesList.add("/test/file/3")

        handleRootLoggerRecords()
    }

    override fun onWindowClose() {
        val confirmed = JOptionPane.showConfirmDialog(
            viewModel,
            "Are you sure want to exit?",
            "Exit",
            JOptionPane.YES_NO_OPTION
        )
        if (confirmed == JOptionPane.YES_OPTION) {
            cancelCopy()
            viewModel.dispose()
        }
    }

    override fun onVpnConnectionsRefreshClick() {
        viewModel.vpnConnections.clear()

        try {
            listVpnConnections()
        } catch (e: VpnException) {
            logger.log(Level.SEVERE, e, "Unable to refresh VPN connections")
            return
        }.forEach { connectionName ->
            viewModel.vpnConnections.add(connectionName)
        }
    }

    override fun onVpnConnectionSelect(s: String?) {
        logger.log(Level.INFO, "onVpnConnectionSelect: $s")
    }

    var conn: VpnConnection? = null
    override fun onTargetDirChangeButtonClick() {
        logger.log(Level.INFO, "onTargetDirChangeButtonClick")

        conn?.disconnect()
        val newConnectionName = viewModel.vpnConnection
        if (newConnectionName != null) {
            conn = VpnConnection(newConnectionName).apply { reconnect() }
        }
    }

    override fun onFilesListChangeButtonClick() {
        logger.log(Level.INFO, "onFilesListChangeButtonClick")
        logger.log(Level.INFO, openFileChooser(viewModel, "Choose source files").joinToString())
    }

    override fun onRemoveFileButtonClick() {
        logger.log(Level.INFO, buildString {
            appendLine("onRemoveFileButtonClick: ")
            viewModel.selectedFilesIndexes.map { viewModel.filesList.toList()[it] }.forEach { name ->
                appendLine("\t$name")
            }
            append("-------------------------------------------------")
        })
    }

    override fun onRemoveAllFilesButtonClick() {
        logger.log(Level.INFO, "onRemoveAllFilesButtonClick")
    }

    override fun onRunPauseButtonClick() {
        logger.log(Level.INFO, "onRunPauseButtonClick: ${viewModel.overwriteFilesCheckBoxSelected}")
        when (viewModel.status) {
            ViewModel.Status.RUNNING -> viewModel.status = ViewModel.Status.PAUSED
            else -> viewModel.status = ViewModel.Status.RUNNING
        }
    }

    override fun onStopButtonClick() {
        logger.log(Level.INFO, "onStopButtonClick")

        when (viewModel.status) {
            ViewModel.Status.PAUSED,
            ViewModel.Status.RUNNING -> {
                val confirmed = JOptionPane.showConfirmDialog(
                    viewModel,
                    "Are you sure want to cancel?",
                    "Cancel copy",
                    JOptionPane.YES_NO_OPTION
                )
                if (confirmed == JOptionPane.YES_OPTION) {
                    cancelCopy()
                    viewModel.status = ViewModel.Status.CANCELLED
                }
            }
            else -> throw IllegalStateException()
        }
    }

    private fun cancelCopy() {
        if (viewModel.status == ViewModel.Status.OK || viewModel.status == ViewModel.Status.CANCELLED) {
            /* safely return */
            return
        }
        viewModel.logStr += "cleaning up..."
    }

    private fun handleRootLoggerRecords() {
        Logger.getLogger("").addHandler(object : Handler() {
            val formatter = SimpleDateFormat("HH:mm:ss")

            override fun publish(record: LogRecord?) {
                if (record == null) {
                    return
                }

                viewModel.logStr += buildString {
                    append(formatter.format(Date(record.millis)))
                    append(" [")
                    append(record.level.name)
                    append("] ")
                    append(record.loggerName)
                    append(": ")
                    appendLine(record.message)
                }
            }

            override fun flush() = Unit
            override fun close() = Unit
        })
    }
}