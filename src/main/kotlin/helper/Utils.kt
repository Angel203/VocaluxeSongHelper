package helper

class Utils {
    enum class Filetypes(private val filetype: String) {
        TXT(".txt"), TXD(".txd"), JPG(".jpg");

        override fun toString(): String {
            return filetype
        }
    }
}
