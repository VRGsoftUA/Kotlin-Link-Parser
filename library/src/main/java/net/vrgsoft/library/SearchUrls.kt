package net.vrgsoft.library

import java.net.URL
import java.util.ArrayList

class SearchUrls {
    companion object {


        val ALL = 0
        val FIRST = 1

        /** It finds urls inside the text and return the matched ones  */
        fun matches(text: String): ArrayList<String> {
            return matches(text, ALL)
        }

        /** It finds urls inside the text and return the matched ones  */
        fun matches(text: String, results: Int): ArrayList<String> {

            val urls = ArrayList<String>()

            val splitString = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (string in splitString) {

                try {
                    val item = URL(string)
                    urls.add(item.toString())
                } catch (e: Exception) {
                }

                if (results == FIRST && urls.size > 0)
                    break
            }

            return urls
        }
    }
}