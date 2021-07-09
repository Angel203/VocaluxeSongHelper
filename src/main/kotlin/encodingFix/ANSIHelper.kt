package encodingFix

import java.io.*
import java.util.*

/**
 * Sets the path of the root song directory
 * @param path - the root song directory
 */
class ANSIHelper(private val path: String) {
    private var txts: MutableList<File> = ArrayList()
    private var lines = ArrayList<String>()
    fun findTXTs() {
        val dir = File(path) // Songs directory
        displayDirectoryContents(dir)
    }

    /**
     * Adds add TXT files to the [txts] List.
     * @param dir the directory.
     */
    private fun displayDirectoryContents(dir: File) {
        try {
            val files = dir.listFiles() ?: return
            for (file in files) {
                if (file.isDirectory) {
                    displayDirectoryContents(file)
                } else {
                    if (file.canonicalPath.endsWith(".txt") ||file.canonicalPath.endsWith(".txd")) {
                        txts.add(file)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Fixes the wrong encoding (from UTF-8 to ANSI (CP1252)).
     *
     * @param dictionary - The [Dictionary] hash map to fix wrong encoding.
     */
    fun fixToAnsi(dictionary: Dictionary) {
        for (f in txts) {
            var changed = false
            lines.clear()
            try {
                val reader = BufferedReader(InputStreamReader(FileInputStream(f.canonicalPath), "Cp1252"))
                val lines = reader.readLines()
                val newLines = mutableListOf<String>()
                lines.forEach { line ->
                    var newLine = line
                    val keys = dictionary.keys
                    for (s in keys) {
                        if (newLine.contains(s!!)) {
                            newLine = newLine.replace(s, dictionary.getValue(s)!!)
                            if (!changed) {
                                println("file has been changed: " + f.canonicalPath)
                            }
                            changed = true
                        }
                    }
                    newLines.add(newLine)
                }
                reader.close()
                if (changed) {
                    val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(f), "Cp1252"))
                    for (s in newLines) {
                        writer.write(s)
                        writer.newLine()
                    }
                    writer.flush()
                    writer.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
