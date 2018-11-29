package net.vrgsoft.library

import java.util.regex.Matcher
import kotlin.text.Regex

class Regex {
    companion object {
        val IMAGE_PATTERN = "(.+?)\\.(jpg|png|gif|bmp)$"
        val IMAGE_TAG_PATTERN = "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
        val ICON_TAG_PATTERN = "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
        val ICON_REV_TAG_PATTERN = "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
        val ITEMPROP_IMAGE_TAG_PATTERN = "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
        val ITEMPROP_IMAGE_REV_TAG_PATTERN = "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
        val TITLE_PATTERN = "<title(.*?)>(.*?)</title>"
        val SCRIPT_PATTERN = "<script(.*?)>(.*?)</script>"
        val METATAG_PATTERN = "<meta(.*?)>"
        val METATAG_CONTENT_PATTERN = "content=\"(.*?)\""
        val URL_PATTERN = "<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>"

        fun match(content: String, pattern: String, index: Int): String {
            val r: Regex = kotlin.text.Regex(pattern)

            return LinkCrawler.extendedTrim(r.find(content)!!.value)
        }

        fun matchAll(content: String, pattern: String): List<String> {
            val r: Regex = kotlin.text.Regex(pattern)
            val matches: List<String> = r.findAll(content).map { matchResult -> matchResult.value }.toList()
            return matches
        }
    }
}

