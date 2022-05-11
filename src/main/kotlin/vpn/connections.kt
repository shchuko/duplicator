package vpn

import subprocess.runSubprocess
import vpn.exception.VpnException
import java.util.concurrent.TimeUnit

fun listVpnConnections(): List<String> {
    return try {
        listOf(
            "powershell.exe",
            "Get-VpnConnection | Select-Object -ExpandProperty Name"
        ).runSubprocess(timeout = 60, timeUnit = TimeUnit.SECONDS).lines().filter { it.isNotEmpty() }
    } catch (e: Exception) {
        throw VpnException(e)
    }
}