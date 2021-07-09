package encodingFix

import helper.Helper
import kotlin.jvm.JvmStatic

object FixEncoding {
    @JvmStatic
    fun main(args: Array<String>) {
        val helper = Helper()
        val ansiHelper = ANSIHelper(helper.getSongFolder())
        ansiHelper.findTXTs()
        ansiHelper.fixToAnsi(Dictionary())
    }
}
