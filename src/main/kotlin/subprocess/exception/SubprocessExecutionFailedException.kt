package subprocess.exception

open class SubprocessExecutionFailedException : SubprocessException {
    val exitCode: Int
    val stderr: String

    constructor(exitCode: Int, stderr: String) {
        this.exitCode = exitCode
        this.stderr = stderr
    }

    constructor(exitCode: Int, stderr: String, message: String) : super(message) {
        this.exitCode = exitCode
        this.stderr = stderr
    }
}