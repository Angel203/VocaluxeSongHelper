package listSongs

import helper.Helper
import kotlin.jvm.JvmStatic

object MainListSongs {
    @JvmStatic
    fun main(args: Array<String>) {
        val helper = Helper()
        val listSongs = ListSongs()
        listSongs.createJSON(helper.getSongFolder(), helper.getServerFullJSON(), helper.getServerShortJSON())
    }
}
