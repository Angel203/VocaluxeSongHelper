package models

class Song : Comparable<Song> {
    var id: String = ""

    /**
     * The path of the txt or txd File.
     */
    var path: String = ""

    // the actual properties
    var title: String = ""
    var artist: String = ""
    var title_on_sorting: String? = null
    var artist_on_sorting: String? = null
    var creator: String? = null
    var author: String? = null
    var version: String? = null
    var source: String? = null
    var youtube: String? = null
    var length: String? = null
    var mp3: String = ""
    var bpm: String? = null
    var edition: String? = null
    var genre: String? = null
    var album: String? = null
    var year: String? = null
    var language: String? = null
    var comment: String? = null
    var gap: String? = null
    var cover: String? = null
    var background: String? = null
    var video: String? = null
    var videogap: String? = null
    var videoaspect: String? = null
    var start: String? = null
    var end: String? = null
    var previewstart: String? = null
    var preview: String? = null
    var medleystartbeat: String? = null
    var medleyendbeat: String? = null
    var endshort: String? = null
    var calcmedley: String? = null
    var relative: String? = null
    var resolution: String? = null

    // unofficial
    var duett: Boolean = false
    override fun toString(): String {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}'
    }

    override fun compareTo(other: Song): Int {
        if (artist.toLowerCase().compareTo(other.artist.toLowerCase()) == 0) {
            if (title.toLowerCase() > other.title.toLowerCase()) {
                return 1
            }
        }
        return if (artist.toLowerCase() > other.artist.toLowerCase()) {
            1
        } else {
            -1
        }
    }
}
