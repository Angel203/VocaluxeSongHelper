package coverChecker

import helper.Utils.Filetypes
import kotlin.jvm.JvmStatic
import helper.Helper

object CoverChecker {
    @JvmStatic
    fun main(args: Array<String>) {
        val helper = Helper()
        helper.fillList(helper.getSongFolder(), Filetypes.TXT.toString())
        helper.showMissingCovers()
    }
}
