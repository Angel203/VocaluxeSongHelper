package helper

import helper.Utils.Filetypes
import models.ShortSong
import models.Song
import org.apache.commons.io.FileUtils
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class Helper {
    private var txts: MutableList<File> = ArrayList()
    private var txds: MutableList<File> = ArrayList()

    private val prefixes = listOf(
        "TITLE",
        "ARTIST",
        "TITLE_ON_SORTING",
        "ARTIST_ON_SORTING",
        "CREATOR",
        "AUTOR",
        "AUTHOR",
        "VERSION",
        "SOURCE",
        "YOUTUBE",
        "LENGTH",
        "MP3",
        "BPM",
        "EDITION",
        "GENRE",
        "ALBUM",
        "YEAR",
        "LANGUAGE",
        "COMMENT",
        "GAP",
        "COVER",
        "BACKGROUND",
        "VIDEO",
        "VIDEOGAP",
        "VIDEOASPECT",
        "START",
        "END",
        "PREVIEWSTART",
        "PREVIEW",
        "MEDLEYSTARTBEAT",
        "MEDLEYENDBEAT",
        "ENDSHORT",
        "CALCMEDLEY",
        "RELATIVE",
        "RESOLUTION"
    )

    /***
     * The lists helper.txt und helper.txd gets filled.
     * TXT includes TXD files.
     *
     * @param extension is a member of the enum [Utils.Filetypes]
     */
    fun fillList(root: String, extension: String) {
        val dir = File(root)
        fillList(dir, extension)
        if (extension == Filetypes.TXT.toString()) {
            fillList(dir, Filetypes.TXD.toString())
        }
    }

    /**
     * Prints out all directories which do not contain a COVER file.
     */
    fun showMissingCovers() {
        printPathsWithoutCover()
    }

    val songs: List<String>
        get() = songsList

    /**
     *
     *
     * @param dir       - directory, in which the files are searched.
     * @param extension - The extension of the file(s).
     */
    private fun fillList(dir: File, extension: String) {
        try {
            val files = dir.listFiles() ?: return
            for (file in files) {
                if (file.isDirectory) {
                    //  System.out.println("directory:" + file.getCanonicalPath());
                    fillList(file, extension)
                } else {
                    if (file.canonicalPath.endsWith(extension)) {
                        //  System.out.println(" file:" + file.getCanonicalPath());
                        if (extension == Filetypes.TXT.toString()) txts.add(file)
                        if (extension == Filetypes.TXD.toString()) txds.add(file)
                        // System.out.println(file.getAbsolutePath());
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Prints all paths where no cover exists.
     */
    private fun printPathsWithoutCover() {
        val allFiles: MutableList<File> = ArrayList()
        allFiles.addAll(txts)
        allFiles.addAll(txds)
        val detailedAll = getDetailedList(allFiles)
        detailedAll.forEach {
            if (it.cover.isNullOrEmpty() || File("" + it.cover).isFile) println("Missing cover or annotation in ${it.path}")
        }
    }

    private val songsList: List<String>
        get() {
            val songs: MutableList<String> = ArrayList()
            addToList(songs, txts, false)
            addToList(songs, txds, true)
            songs.sort()
            return songs
        }

    private fun addToList(songs: MutableList<String>, files: List<File>, duet: Boolean): List<String> {
        var reader: BufferedReader
        for (f in files) {
            var title = ""
            var artist = ""
            try {
                reader = BufferedReader(InputStreamReader(FileInputStream(f.canonicalPath), "Cp1252"))
                val lines = reader.readLines()
                for (line in lines) {
                    if (line.startsWith("#TITLE")) {
                        title = line.substring(7)
                        if (duet) title += " [DUET]"
                    } else if (line.startsWith("#ARTIST")) {
                        artist = line.substring(8)
                    }
                    if (artist.isNotEmpty() && title.isNotEmpty()) {
                        songs.add("$artist - $title")
                        break
                    }
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return songs
    }

    val songDetails: MutableList<Song>
        get() = getDetailedList(txts)

    /**
     * File can be a list of txt's or txd's, or whatever.
     *
     * @param files
     * @return
     */
    private fun getDetailedList(files: List<File>): MutableList<Song> {
        val songs: MutableList<Song> = ArrayList()
        var reader: BufferedReader
        createFolderIfDoesNotExists(getServerCoverDir())
        for (f in files) {
            var song: Song
            try {
                reader = BufferedReader(InputStreamReader(FileInputStream(f.canonicalPath), "Cp1252"))
                song = checkForProperties(reader)
                if (song.title == "") {
                    println("Ignored: Title: ${song.title} Path: ${song.path}")
                    reader.close()
                    continue
                }
                song.id = (song.artist + song.title).hashCode().toString()
                song.path = f.canonicalPath
                if (song.cover != null) {
                    val coverPath = f.parent + "\\" + song.cover
                    val fileContent = FileUtils.readFileToByteArray(File(coverPath))
                    val base64image = Base64.getEncoder().encodeToString(fileContent)
                    writeToFile(getServerCoverDir() + "/${song.id}.cover", base64image)
                }
                songs.add(song)
                reader.close()
            } catch (e: Exception) {
                when (e) {
                    is FileNotFoundException -> {
                        println("Ignoring txt, reason: ${e.localizedMessage}")
                    }
                    is IOException -> {
                        e.printStackTrace()
                    }
                    else -> throw e
                }
            }
        }
        return songs
    }

    private fun createFolderIfDoesNotExists(folderPath: String) {
        val fileDir = File(folderPath)
        if (fileDir.isDirectory) {
            try {
                if (!fileDir.exists()) {
                    fileDir.mkdirs()
                }

            } catch (e: Exception) {
                when (e) {
                    is SecurityException -> {
                        println("Could not create folder $folderPath")
                    }
                    else -> {
                        throw e
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun checkForProperties(reader: BufferedReader): Song {
        val song = Song()
        val lines: List<String> = reader.readLines()
        lines.forEach { line ->
            if (line.startsWith("#TITLE")) song.title = line.substring(7)
            if (line.startsWith("#ARTIST")) song.artist = line.substring(8)
            if (line.startsWith("#AUTOR ")) song.author = line.substring(7)
            if (line.startsWith("#AUTHOR")) song.author = line.substring(8)
            if (line.startsWith("#CREATOR")) song.author = line.substring(9)
            if (line.startsWith("#VERSION")) song.version = line.substring(9)
            if (line.startsWith("#MP3")) song.mp3 = line.substring(5)
            if (line.startsWith("#BPM")) song.bpm = line.substring(5)
            if (line.startsWith("#EDITION")) song.edition = line.substring(9)
            if (line.startsWith("#GENRE")) song.genre = line.substring(7)
            if (line.startsWith("#ALBUM")) song.album = line.substring(7)
            if (line.startsWith("#YEAR")) song.year = line.substring(6)
            if (line.startsWith("#LANGUAGE")) song.language = line.substring(10)
            if (line.startsWith("#GAP")) song.gap = line.substring(5)
            if (line.startsWith("#COVER")) song.cover = line.substring(7)
            if (line.startsWith("#BACKGROUND")) song.background = line.substring(12)
            if (line.startsWith("#VIDEO")) song.video = line.substring(7)
            if (line.startsWith("#VIDEOGAP")) song.videogap = line.substring(10)
            if (line.startsWith("#START")) song.start = line.substring(7)
            if (line.startsWith("#END")) song.end = line.substring(5)
            if (line.startsWith("#PREVIEWSTART")) song.previewstart = line.substring(5)
            if (line.startsWith("#PREVIEW")) song.preview = line.substring(5)
            if (line.startsWith("#MEDLEYSTARTBEAT")) song.medleystartbeat = line.substring(17)
            // unofficial
            if (line.startsWith("P1")) song.duett = true
        }
        return song
    }

    fun fillSongDetails(songs: MutableList<Song>) {
        songs.addAll(getDetailedList(txts))
        songs.addAll(getDetailedList(txds))
        songs.sort()
    }

    fun writeToFile(filename: String, text: String) {
        val f = File(filename)
        if (f.exists()) {
            f.delete()
        }
        val bufferedWriter: BufferedWriter
        try {
            if (f.createNewFile()) {
                bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(f)))
                bufferedWriter.write(text)
                bufferedWriter.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Writes a value after a prefix (e.g. #TITLE:, #YEAR:)
     *
     * @param afterPrefix
     * @param filePath
     */
    fun writeAfterPrefix(afterPrefix: String, filePath: String) {
        try {
            val reader = BufferedReader(InputStreamReader(FileInputStream(filePath), "Cp1252"))
            val oldLines: MutableList<String> = ArrayList()
            var position = 0
            var writepos = -1
            val lines = reader.readLines()
            lines.forEach { line ->
                oldLines.add(line)
                if (line.startsWith(afterPrefix)) {
                    writepos = position
                }
                position++
            }
            lines.forEach { line -> oldLines.add(writepos, line) }
            overWriteExistingSongFile(filePath, oldLines)
            println(lines.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun overWriteExistingSongFile(filePath: String, lines: List<String>) {
        try {
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(filePath), "Cp1252"))
            for (line in lines) {
                writer.write(line)
                writer.newLine()
            }
            writer.flush()
            writer.close()
            println("$filePath neu geschrieben")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * Generates a short list which only contains the id, title and artist of a song.
     *
     * @param songs The list with the required attributes id, title and artist (more are ignored).
     * @return A list of songs with only id, title and artist.
     */
    fun getShortList(songs: List<Song>): List<ShortSong> {
        val list: MutableList<ShortSong> = ArrayList()
        for (song in songs) {
            val shortSong = ShortSong()
            shortSong.id = song.id
            shortSong.title = song.title
            shortSong.artist = song.artist
            list.add(shortSong)
        }
        return list
    }

    /**
     * Removes the path of all Songs in the list.
     *
     * @param songs The list of the songs.
     */
    fun removePath(songs: List<Song>) {
        songs.forEach { it.path = "" }
    }

    fun getSongFolder(): String {
        val lines = getConfigLines()
        lines.forEach {
            if (it.startsWith("SongFolder:")) return it.substring("SongFolder:".length)
        }
        throw FileNotFoundException("SongFolder not found in config/config.cfg")
    }

    private fun getConfigLines(): List<String> {
        val inputStream = javaClass.classLoader.getResourceAsStream("config/config.cfg") ?: throw FileNotFoundException(
            "config/config.cfg not found"
        )
        val config = BufferedReader(InputStreamReader(inputStream))
        return config.readLines()
    }

    fun getYearFilePath(): String {
        val lines = getConfigLines()
        lines.forEach {
            if (it.startsWith("YearFilePath:")) return it.substring("YearFilePath:".length)
        }
        throw FileNotFoundException("YearFilePath not found in config/config.cfg")
    }

    fun getServerFullJSON(): String {
        val lines = getConfigLines()
        lines.forEach {
            if (it.startsWith("ServerFullJSON:")) return it.substring("ServerFullJSON:".length)
        }
        throw FileNotFoundException("ServerFullJSON not found in config/config.cfg")
    }

    fun getServerShortJSON(): String {
        val lines = getConfigLines()
        lines.forEach {
            if (it.startsWith("ServerShortJSON:")) return it.substring("ServerShortJSON:".length)
        }
        throw FileNotFoundException("ServerShortJSON not found in config/config.cfg")
    }

    fun getServerCoverDir(): String {
        val lines = getConfigLines()
        lines.forEach {
            if (it.startsWith("ServerCoverDir:")) return it.substring("ServerCoverDir:".length)
        }
        throw FileNotFoundException("ServerCoverDir not found in config/config.cfg")
    }
}
