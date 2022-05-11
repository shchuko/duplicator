import collections.SimpleObservableList
import swingUtils.centring
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger
import javax.swing.DefaultListModel
import kotlin.properties.Delegates.observable


class ViewModel(title: String) : JView(title) {
    enum class Status { RUNNING, PAUSED, OK, CANCELLED, ERRORED }

    companion object {
        const val NONE_STRING = "<none>"
        private val logger = Logger.getLogger(ViewModel::class.java.name)
    }

    val vpnConnections = SimpleObservableList(
        onAdd = jVpnConnections::addItem,
        onRemove = jVpnConnections::removeItemAt,
        NONE_STRING
    )

    val vpnConnection: String?
        get() = jVpnConnections.selectedItem?.toString()

    var targetDir by observable("") { _, _, it -> jTargetDir.text = it }

    private val jFilesListModel = DefaultListModel<String>()
    var filesList = SimpleObservableList(
        onAdd = jFilesListModel::addElement,
        onRemove = jFilesListModel::removeElementAt
    )

    val selectedFilesIndexes: List<Int>
        get() = jFilesList.selectedIndices.asList()

    val overwriteFilesCheckBoxSelected: Boolean
        get() = jOverwriteFilesCheckBox.isSelected

    var filesCopied by observable(Pair(0, 0)) { _, _, it -> jFilesField.text = "${it.first} / ${it.second}" }

    var sizeCopied by observable(Pair(0.0, 0.0)) { _, _, it -> jSizeField.text = "${it.first} / ${it.second}" }

    var speed by observable(0.0) { _, _, it -> jSpeed.text = "$it MiB/s" }

    var speedAvg by observable(0.0) { _, _, it -> jSpeedAvg.text = "$it MiB/s" }

    var logStr by observable("") { _, _, it -> jLog.text = it }

    var status by observable(Status.OK) { _, _, newVal ->
        jStatusLabel.text = newVal.name

        val notRunning = newVal == Status.OK || newVal == Status.CANCELLED || newVal == Status.ERRORED

        jVpnConnections.isEnabled = notRunning
        jRefreshVpnConnButton.isEnabled = notRunning
        jTargetDirChangeButton.isEnabled = notRunning
        jTargetDir.isEnabled = notRunning
        jFilesListChangeButton.isEnabled = notRunning
        jFilesList.isEnabled = notRunning
        jRemoveFileButton.isEnabled = notRunning
        jRemoveAllFilesButton.isEnabled = notRunning
        jOverwriteFilesCheckBox.isEnabled = notRunning

        jStopButton.isEnabled = !notRunning

        when (newVal) {
            Status.RUNNING -> jRunPauseButton.text = "Pause"
            Status.PAUSED -> jRunPauseButton.text = "Continue"
            Status.ERRORED -> jRunPauseButton.text = "Retry"
            Status.CANCELLED,
            Status.OK -> jRunPauseButton.text = "Run"
        }
    }

    var progress by observable(0) { _, _, it ->
        require(it >= 0)
        require(it <= 100)
        jProgressBar.value = it
    }

    init {
        /* link list model to list */
        jFilesList.model = jFilesListModel

        /* init fields */
        targetDir = NONE_STRING
        filesCopied = Pair(0, 0)
        sizeCopied = Pair(0.0, 0.0)
        speed = 0.0
        speedAvg = 0.0
        logStr = ""
        status = Status.OK
        progress = 0

        /* setup window */
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        contentPane = jMainPanel
        isVisible = true
        minimumSize = Dimension(600, 400)

        pack()
        centring()

        logger.addHandler(object : Handler() {
            override fun publish(record: LogRecord?) {
                logStr += record
            }

            override fun flush() {}
            override fun close() {}

        })
    }

    fun addListener(l: Listener) {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                l.onWindowClose()
            }
        })

        jRefreshVpnConnButton.addActionListener { l.onVpnConnectionsRefreshClick() }
        jVpnConnections.addActionListener {
            l.onVpnConnectionSelect(
                if (jVpnConnections.selectedItem == NONE_STRING) null
                else jVpnConnections.selectedItem as String?
            )

        }
        jTargetDirChangeButton.addActionListener { l.onTargetDirChangeButtonClick() }
        jFilesListChangeButton.addActionListener { l.onFilesListChangeButtonClick() }
        jRemoveFileButton.addActionListener { l.onRemoveFileButtonClick() }
        jRemoveAllFilesButton.addActionListener { l.onRemoveAllFilesButtonClick() }
        jRunPauseButton.addActionListener { l.onRunPauseButtonClick() }
        jStopButton.addActionListener { l.onStopButtonClick() }
    }

    interface Listener {
        fun onWindowClose()
        fun onVpnConnectionsRefreshClick()
        fun onVpnConnectionSelect(s: String?)
        fun onTargetDirChangeButtonClick()
        fun onFilesListChangeButtonClick()
        fun onRemoveFileButtonClick()
        fun onRemoveAllFilesButtonClick()
        fun onRunPauseButtonClick()
        fun onStopButtonClick()
    }
}
