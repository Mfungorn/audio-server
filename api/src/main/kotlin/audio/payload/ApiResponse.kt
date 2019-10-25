package audio.payload

class ApiResponse(
        private var success: Boolean,
        private var message: String? = ""
) {

    fun isSuccess(): Boolean {
        return success
    }

    fun setSuccess(success: Boolean) {
        this.success = success
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }
}