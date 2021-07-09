package encodingFix

import java.util.HashMap

class Dictionary {
    private var dictionary = HashMap<String?, String>()
    val keys: Set<String?>
        get() = dictionary.keys

    fun getValue(key: String?): String? {
        return dictionary[key]
    }

    /**
     * Creates teh HashMap `dictionary`.
     * KEY is ANSI string and VALUE is UTF-8 string.
     */
    init {
        dictionary["â€™"] = "’"
        dictionary["Ã¤"] = "ä"
        dictionary["Ã¶"] = "ö"
        dictionary["Ã¼"] = "ü"
        dictionary["ÃŸ"] = "ß"
        dictionary["Â´"] = "´"
        dictionary["Ã©"] = "é"
        dictionary["Ã¨"] = "è"
        dictionary["Ã‡"] = "ç"
        dictionary["â€˜"] = "‘"
        dictionary["â€“"] = "–"
        dictionary["Ã³"] = "ó"
        dictionary["Ã²"] = "ò"
        dictionary["Ã\u00AD"] = "í"
        dictionary["Ã¬"] = "ì"
        dictionary["Ãº"] = "ú"
        dictionary["Ã¹"] = "ù"
        dictionary["Ã±"] = "ñ"
        dictionary["Ã„"] = "Ä"
        dictionary["Ã–"] = "Ö"
        dictionary["Ãœ"] = "Ü"
        dictionary["Ã§"] = "Ç"
        dictionary["Ãˆ"] = "É"
        dictionary["Ã‰"] = "É"
        dictionary["Ã’"] = "Ò"
        dictionary["Ã“"] = "Ó"
        dictionary["Ã\u008D"] = "Í"
        dictionary["ÃŒ"] = "Ì"
        dictionary["Ãš"] = "Ú"
        dictionary["Ã™"] = "Ù"
    }
}
