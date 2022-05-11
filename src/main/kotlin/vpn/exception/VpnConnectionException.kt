package vpn.exception

open class VpnConnectionException : VpnException {
    val connectionName: String

    constructor(connectionName: String) : super() {
        this.connectionName = connectionName
    }

    constructor(message: String, connectionName: String) : super(message) {
        this.connectionName = connectionName
    }

    constructor(cause: Throwable, connectionName: String) : super(cause = cause) {
        this.connectionName = connectionName
    }

    constructor(message: String, cause: Throwable, connectionName: String) : super(message, cause) {
        this.connectionName = connectionName
    }
}