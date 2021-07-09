package yearChecker

import helper.Helper


object MainOnlinePropertyChecker {
    @JvmStatic
    fun main(args: Array<String>) {
        printSongsWithoutYear();
    }

    private fun printSongsWithoutYear() {
        val helper = Helper()
        val checkForProperties = CheckForProperties()
        // https://theaudiodb.com/api_guide.php it costs 3$ per month now, have to find an other service.
        // checkForProperties.printMissingProperties(helper.getYearFilePath(), helper.getSongFolder())
        // println("Songs without a year are in years.txt")
    }
}
