package subprocess.exception

import java.util.concurrent.TimeUnit

open class SubprocessKilledOnTimeoutException : SubprocessException {
    val timeout: Long
    val timeUnit: TimeUnit

    constructor(timeout: Long, timeUnit: TimeUnit) {
        this.timeout = timeout
        this.timeUnit = timeUnit
    }

    constructor(timeout: Long, timeUnit: TimeUnit, message: String) : super(message) {
        this.timeout = timeout
        this.timeUnit = timeUnit
    }
}