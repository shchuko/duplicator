package subprocess.exception

open class SubprocessException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}