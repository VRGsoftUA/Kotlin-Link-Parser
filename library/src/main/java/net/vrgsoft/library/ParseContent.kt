package net.vrgsoft.library

data class ParseContent(
  var success: Boolean = false,
  var htmlCode: String = "",
  var raw: String = "",
  var title: String = "",
  var description: String = "",
  var url: String = "",
  var finalUrl: String = "",
  var canonicalUrl: String = "",
  var metaTags: Map<String, String> = mapOf(),
  var images: MutableList<String> = mutableListOf(),
  var urlData: List<Int> = listOf()
)