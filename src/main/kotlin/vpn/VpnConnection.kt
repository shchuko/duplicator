package vpn

import subprocess.Command
import subprocess.*
import vpn.exception.VpnConnectionException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

class VpnConnection(private val connectionName: String) {
    private val logger = Logger.getLogger("${this::class.java.name}::$connectionName")

    private fun Command.runSubprocessDefaults() = runSubprocess(logger, 60, TimeUnit.SECONDS)

    fun connect() {
        logger.log(Level.INFO, "Connect requested")

        if (isConnected()) {
            logger.log(Level.INFO, "Already connected")
            return
        }

        try {
            listOf(
                "rasphone",
                "-d",
                connectionName,
                "-f",
                "${System.getProperty("user.home")}\\AppData\\Roaming\\Microsoft\\Network\\Connections\\Pbk\\rasphone.pbk"
            ).runSubprocessDefaults()
            logger.log(Level.INFO, "Connected successfully")
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Connection errored")
            throw VpnConnectionException(connectionName = connectionName, cause = e)
        }
    }

    fun disconnect() {
        logger.log(Level.INFO, "Disconnect requested")
        if (!isConnected()) {
            logger.log(Level.INFO, "Already disconnected")
            return
        }

        try {
            listOf(
                "rasphone",
                "-h",
                connectionName
            ).runSubprocessDefaults()
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Disconnect errored")
            throw VpnConnectionException(connectionName = connectionName, cause = e)
        }
    }

    fun isConnected(): Boolean {
        logger.log(Level.INFO, "Connection status check requested")
        val report = try {
            listOf(
                "powershell.exe",
                "Get-VpnConnection -name '$connectionName' | Select-Object -ExpandProperty ConnectionStatus"
            ).runSubprocessDefaults().lines().filter { it.isNotEmpty() }
        } catch (e: Exception) {
            throw VpnConnectionException(connectionName = connectionName, cause = e)
        }

        if (report.size != 1) {
            logger.log(Level.WARNING, "Unexpected connection status report size")
            throw VpnConnectionException("Unexpected connection status report size, got ${report.size} lines: '${report.joinToString()}'")
        }

        return when (val connectionStatatusString = report.first().trim()) {
            "Disconnected" -> {
                logger.log(Level.INFO, "Connection status: Connected")
                false
            }
            "Connected" -> {
                logger.log(Level.INFO, "Connection Status: Disconnected")
                true
            }
            else -> {
                logger.log(Level.WARNING, "Unknown connection status: $connectionStatatusString")
                throw VpnConnectionException(
                    connectionName = connectionName,
                    message = "Unknown connection status: $connectionStatatusString"
                )
            }
        }
    }


    fun reconnect() {
        logger.log(Level.INFO, "Reconnect requested")
        if (isConnected()) {
            disconnect()
        }
        connect()
    }
}
