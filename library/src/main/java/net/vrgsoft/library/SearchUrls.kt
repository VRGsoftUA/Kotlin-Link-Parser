package net.vrgsoft.library

import java.net.URL
import java.util.ArrayList

object SearchUrls {

  const val ALL = 0
  const val FIRST = 1

  /** It finds urls inside the text and return the matched ones  */
  fun matches(text: String): ArrayList<String> {
    return matches(text, ALL)
  }

  /** It finds urls inside the text and return the matched ones  */
  fun matches(
    text: String,
    results: Int
  ): ArrayList<String> {

    val urls = ArrayList<String>()
    val splitString = text.split(' ')
        .dropLastWhile { it.isEmpty() }
        .toTypedArray()

    for (string in splitString) {
      try {
        val item = URL(string)
        urls.add(item.toString())
      } catch (_: Exception) {
      }

      if (results == FIRST && urls.size > 0) break
    }

    return urls
  }
}