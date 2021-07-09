package listSongs

import helper.Helper
import helper.Utils.Filetypes
import com.google.gson.Gson
import models.Song
import java.util.*

class ListSongs {
    fun createJSON(songDirectory: String, fullFile: String, shortFile: String) {
        val helper = Helper()
        val songs: MutableList<Song> = ArrayList()
        val gson = Gson()

        helper.fillList(songDirectory, Filetypes.TXT.toString())
        helper.fillSongDetails(songs)
        println("Found ${songs.size} songs")

        helper.removePath(songs)
        helper.writeToFile(fullFile, gson.toJson(songs))
        val shortSong = helper.getShortList(songs)
        helper.writeToFile(shortFile, gson.toJson(shortSong))
    }
}
