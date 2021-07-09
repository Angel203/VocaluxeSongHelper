package yearChecker

import helper.GetOnlineSongDetails
import helper.Helper
import helper.Utils.Filetypes
import models.Song
import java.util.*

class CheckForProperties {
    /**
     * Get the property from a online service.
     *
     * @param outputFile    - where the missing years are stored.
     * @param songDirectory - the directory which has to be searched for.
     */
    fun printMissingProperties(outputFile: String, songDirectory: String) {
        val helper = Helper()
        val songs = getSongList(songDirectory)
        var missingYearPath = ""
        var counter = 0
        for (song in songs) {
            if (song!!.year == null || song.album == null || song.genre == null) {
                counter++
                missingYearPath += """
                    ${song.path}
                    """.trimIndent()
                // get the song details from an online service
                val gosd = GetOnlineSongDetails()
                try {
                    val map = gosd.getDetails(song.artist, song.title)
                    if (map.isNotEmpty()) {
                        val lines: MutableList<String> = ArrayList()
                        if (song.year == null) {
                            if (map.containsKey("year")) {
                                lines.add("#YEAR:" + map["year"])
                                println("#YEAR:" + map["year"])
                            }
                        }
                        if (song.album == null) {
                            if (map.containsKey("album")) {
                                println("#ALBUM:" + map["album"])
                                lines.add("#ALBUM:" + map["album"])
                            }
                        }
                        if (song.genre == null) {
                            if (map.containsKey("genre")) {
                                println("#GENRE:" + map["genre"])
                                lines.add("#GENRE:" + map["genre"])
                            }
                        }
                        if (lines.size > 0) {
                            // I do not fully trust the results of this website.
                            helper.writeAfterPrefix("#MP3", song.path)
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        println(counter.toString() + "songs incomplete")
        helper.writeToFile(outputFile, missingYearPath)
    }

    private fun getSongList(songDirectory: String): List<Song?> {
        val helper = Helper()
        helper.fillList(songDirectory, Filetypes.TXT.toString())
        val songs = helper.songDetails
        songs.sort()
        return songs
    }
}
