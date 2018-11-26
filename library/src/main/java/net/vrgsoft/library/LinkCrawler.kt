package net.vrgsoft.library

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor


class LinkCrawler {
    companion object {
        const val ALL: Int = -1
        const val NONE: Int = -2

        fun extendedTrim(content: String): String = content
                .replace("\\s+", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim { it <= ' ' }

        private const val HTTP_PROTOCOL = "http://"
        private const val HTTPS_PROTOCOL = "https://"
    }

    var mCache: MutableMap<String, ParseContent> = mutableMapOf()
    var mPreloadCallback: LinkPreviewCallback? = null

    private val processor: PublishProcessor<Result> = PublishProcessor.create()

    fun parseUrl(url: String): Flowable<Result> {
        initUrl(url)
        return processor
    }

    private fun initUrl(url: String) {
        mPreloadCallback?.onPre()
        if (mCache.containsKey(url)) {
            processor.onNext(Result(mCache[url], isNull(mCache[url]!!), url))
        } else {
            getCode(url)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                      mCache[url] = it
                      processor.onNext(Result(mCache[url], isNull(it), url))
                    },
                    { t -> t.printStackTrace() }
                )
        }
    }

    private fun getCode(url: String): Single<ParseContent> {
        val content = ParseContent()
        return Single.fromCallable {
            val urls: List<String> = SearchUrls.matches(url)
            when {
                urls.isNotEmpty() -> content.finalUrl = unshortUrl(extendedTrim(urls[0]))
                else -> content.finalUrl = ""
            }
            if (content.finalUrl != "") {
                when {
                    isImage(content.finalUrl) && !content.finalUrl.contains("dropbox") -> {
                        content.success = true
                        content.images.add(content.finalUrl)
                        content.title = ""
                        content.description = ""
                    }
                    else -> try {
                        val doc: Document = Jsoup.connect(content.finalUrl).userAgent("Mozzila").get()
                        content.htmlCode = extendedTrim(doc.toString())
                        val metaTags: Map<String, String> = getMetaTags(content.htmlCode)
                        content.metaTags = metaTags
                        content.title = metaTags["title"]!!
                        content.description = metaTags["description"]!!

                        when {
                            content.title == "" -> {
                                val matchTitle = Regex.match(content.htmlCode, Regex.TITLE_PATTERN, 2)
                                if (matchTitle != "") {
                                    content.title = htmlDecode(matchTitle)
                                }
                            }
                        }
                        if (content.description == "") {
                            content.description = crawlCode(content.htmlCode)
                        }
                        content.description = content.description.replace(Regex.SCRIPT_PATTERN, "")
                        when {
                            metaTags["image"] != "" -> content.images.add(
                                    metaTags["image"]!!)
                            else -> content.images = getImages(doc).toMutableList()
                        }
                        content.success = true
                    } catch (e: Exception) {
                        content.success = false
                    }
                }
            }
            val linksSet = content.finalUrl.split("&")
            content.url = linksSet[0]
            content.canonicalUrl = canonicalPage(content.finalUrl)
            content.description = trimTags(content.description)
            //return content
            content
        }
    }

    private fun getMetaTags(content: String): MutableMap<String, String> {

        val metaTags = mutableMapOf<String, String>().apply {
            this["url"] = ""
            this["title"] = ""
            this["description"] = ""
            this["image"] = ""
        }

        val matches = Regex.matchAll(content,
                Regex.METATAG_PATTERN)

        for (match in matches) {
            val lowerCase = match.toLowerCase()
            if (lowerCase.contains("property=\"og:url\"")
                    || lowerCase.contains("property='og:url'")
                    || lowerCase.contains("name=\"url\"")
                    || lowerCase.contains("name='url'"))
                updateMetaTag(metaTags, "url", separateMetaTagsContent(match))
            else if (lowerCase.contains("property=\"og:title\"")
                    || lowerCase.contains("property='og:title'")
                    || lowerCase.contains("name=\"title\"")
                    || lowerCase.contains("name='title'"))
                updateMetaTag(metaTags, "title", separateMetaTagsContent(match))
            else if (lowerCase
                    .contains("property=\"og:description\"")
                    || lowerCase
                    .contains("property='og:description'")
                    || lowerCase.contains("name=\"description\"")
                    || lowerCase.contains("name='description'"))
                updateMetaTag(metaTags, "description", separateMetaTagsContent(match))
            else if (lowerCase.contains("property=\"og:image\"")
                    || lowerCase.contains("property='og:image'")
                    || lowerCase.contains("name=\"image\"")
                    || lowerCase.contains("name='image'"))
                updateMetaTag(metaTags, "image", separateMetaTagsContent(match))
        }

        return metaTags
    }

    private fun updateMetaTag(metaTags: MutableMap<String, String>, url: String, value: String?) {
        if (value?.isNotEmpty() == true) {
            metaTags[url] = value
        }
    }

    /**
     * Gets content from metatag
     */
    private fun separateMetaTagsContent(content: String): String {
        return Jsoup.parse(content).getElementsByAttribute("content").attr("content")
    }

    private fun crawlCode(content: String): String {
        val resultSpan = getTagContent("span", content)
        val resultParagraph = getTagContent("p", content)
        val resultDiv = getTagContent("div", content)

        val result = when {
            resultParagraph.length > resultSpan.length && resultParagraph.length >= resultDiv.length -> resultParagraph
            resultParagraph.length > resultSpan.length && resultParagraph.length < resultDiv.length -> resultDiv
            else -> resultParagraph
        }

        return htmlDecode(result)
    }

    private fun canonicalPage(oldUrl: String): String {
        var newUrl = oldUrl

        var cannonical = ""
        if (newUrl.startsWith(HTTP_PROTOCOL)) {
            newUrl = newUrl.substring(HTTP_PROTOCOL.length)
        } else if (newUrl.startsWith(HTTPS_PROTOCOL)) {
            newUrl = newUrl.substring(HTTPS_PROTOCOL.length)
        }

        val urlLength = newUrl.length
        (0 until urlLength)
                .takeWhile { newUrl[it] != '/' }
                .forEach { cannonical += newUrl[it] }

        return cannonical

    }

    private fun isNull(sourceContent: ParseContent): Boolean = !sourceContent.success &&
            extendedTrim(sourceContent.htmlCode) == "" &&
            !isImage(sourceContent.finalUrl)

    private fun getTagContent(tag: String, content: String): String {
        val pattern = "<$tag(.*?)>(.*?)</$tag>"
        var result = ""
        var currentMatch: String

        val matches: MutableList<String> = Regex.matchAll(content, pattern).toMutableList()
        val matchesSize = matches.size

        for (i in 0..matchesSize) {
            currentMatch = trimTags(matches[i])
            if (currentMatch.length >= 120) {
                result = extendedTrim(currentMatch)
                break
            }
        }
        if (result == "") {
            val final: String = Regex.match(content, pattern, 2)
            result = extendedTrim(final)
        }

        result = result.replace("&nbsp;", "")
        return htmlDecode(result)
    }

    private fun getImages(document: Document): List<String> {
        val matches: MutableList<String> = mutableListOf()
        val media: Elements = document.select("[src]")

        media.forEach {
            element: Element? ->
            run {
                if (element!!.tagName() == "img") {
                    matches.add(element.attr("abs:src"))
                }
            }
        }
        return matches
    }

    private fun unshortUrl(url: String): String {
        if (!url.startsWith(HTTP_PROTOCOL) && !url.startsWith(HTTPS_PROTOCOL)) {
            return ""
        }

        var urlConn = connectURL(url)
        urlConn.headerFields

        var finalResult = urlConn.url.toString()

        urlConn = connectURL(finalResult)
        urlConn.headerFields


        while (urlConn.url.toString() != finalResult) {
            finalResult = unshortUrl(finalResult)
        }

        return finalResult

    }

    private fun connectURL(strURL: String): URLConnection {
        var conn: URLConnection? = null
        try {
            val inputURL = URL(strURL)
            conn = inputURL.openConnection()
        } catch (e: MalformedURLException) {
            println("Please input a valid URL")
        } catch (ioe: IOException) {
            println("Can not connect to the URL")
        }

        return conn!!
    }

    private fun htmlDecode(content: String): String = Jsoup.parse(content).text()

    private fun trimTags(content: String): String = Jsoup.parse(content).text()

    private fun isImage(url: String): Boolean = url.matches(Regex.IMAGE_PATTERN.toRegex())
}