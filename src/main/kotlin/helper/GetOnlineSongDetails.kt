package helper

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class GetOnlineSongDetails {
    var map: MutableMap<String, String> = HashMap()
    @Throws(Exception::class)
    fun getDetails(artist: String?, title: String?): Map<String, String> {
        map = HashMap()
        callMe(
            "https://theaudiodb.com/api/v1/json/1/searchtrack.php?s=" + URLEncoder.encode(
                artist,
                StandardCharsets.UTF_8.toString()
            ) + "&t=" + URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
        )
        return map
    }

    @Throws(Exception::class)
    fun callMe(url: String) {
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        // optional default is GET
        con.requestMethod = "GET"
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0")
        val `in` = BufferedReader(
            InputStreamReader(con.inputStream)
        )
        val response = StringBuffer()
        val inputLine = `in`.readLines()
        inputLine.forEach { line ->
            response.append(line)
        }
        `in`.close()
        val jsonObject = JSONObject(response.toString())
        processResult(jsonObject, url)
    }

    @Throws(Exception::class)
    private fun processResult(jsonObject: JSONObject, url: String) {
        var idTrack = ""
        var idAlbum = ""
        var yearReleased = ""
        var strGenre = ""
        var strAlbum = ""
        if (jsonObject.has("track")) {
            if (jsonObject["track"].toString() == "null") return
            val jsonString = jsonObject.toString() //assign your JSON String here
            val obj = JSONObject(jsonString)
            val arr = obj.getJSONArray("track")
            for (i in 0 until arr.length()) {
                idTrack = arr.getJSONObject(i).getString("idTrack")
                idAlbum = arr.getJSONObject(i).getString("idAlbum")
                strAlbum = arr.getJSONObject(i).getString("strAlbum")
                // weitere Details möglich.
            }
            if (idAlbum != "") {
                map["album"] = strAlbum
                callMe("https://theaudiodb.com/api/v1/json/1/album.php?m=$idAlbum")
            }
        }
        if (jsonObject.has("album")) {
            if (jsonObject["album"].toString() == "null") return
            val jsonString = jsonObject.toString() //assign your JSON String here
            val obj = JSONObject(jsonString)
            val arr = obj.getJSONArray("album")
            for (i in 0 until arr.length()) {
                yearReleased = arr.getJSONObject(i).getString("intYearReleased")
                if (arr.getJSONObject(i).has("strGenre")) {
                    try {
                        if (arr.getJSONObject(i)["strGenre"] is String) strGenre =
                            arr.getJSONObject(i).getString("strGenre")
                    } catch (ex: Exception) {
                        println(url)
                        ex.printStackTrace()
                    }
                }
                // more details are possible.
            }
            if (yearReleased != "" && yearReleased != "0") map["year"] = yearReleased
            if (strGenre != "" && strGenre != "null") map["genre"] = strGenre
        }
        // need to album to be able to find out the year.

        // Link for the year, require the album id https://theaudiodb.com/api/v1/json/1/album.php?m=2115888
    }
}
