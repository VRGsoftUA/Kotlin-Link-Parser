package net.vrgsoft.library

object Regex {

  const val IMAGE_PATTERN = "(.+?)\\.(jpg|png|gif|bmp)$"
  const val IMAGE_TAG_PATTERN =
    "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
  const val ICON_TAG_PATTERN =
    "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
  const val ICON_REV_TAG_PATTERN =
    "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
  const val ITEMPROP_IMAGE_TAG_PATTERN =
    "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
  const val ITEMPROP_IMAGE_REV_TAG_PATTERN =
    "<img(.*?)src=(\"|')(.+?)(gif|jpg|png|bmp)(\"|')(.*?)(/)?>(</img>)?"
  const val TITLE_PATTERN = "<title(.*?)>(.*?)</title>"
  const val SCRIPT_PATTERN = "<script(.*?)>(.*?)</script>"
  const val METATAG_PATTERN = "<meta(.*?)>"
  const val METATAG_CONTENT_PATTERN = "content=\"(.*?)\""
  const val URL_PATTERN =
    "<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>"

  fun match(
    content: String,
    patter: String
  ): String {
    val r = kotlin.text.Regex(patter)
    return LinkCrawler.extendedTrim(r.find(content)?.value ?: "")
  }

  fun matchAll(
    content: String,
    patter: String
  ): List<String> {
    val r = kotlin.text.Regex(patter)
    return r.findAll(content)
        .map { matchResult -> matchResult.value }
        .toList()
  }
}