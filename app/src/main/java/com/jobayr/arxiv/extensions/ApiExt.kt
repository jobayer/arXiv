package com.jobayr.arxiv.extensions

import android.util.Log
import android.util.Xml
import com.jobayr.arxiv.models.Feed
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.text.ParseException

@Throws(IOException::class, XmlPullParserException::class, ParseException::class)
fun getFeed(inputStream: InputStream): Feed {
    val feed = Feed()
    inputStream.use { iStream ->
        val xmlParser = Xml.newPullParser()
        xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        xmlParser.setInput(iStream, null)
        xmlParser.nextTag()
        parseFeed(feed, xmlParser)
        return feed
    }
}

@Throws(IOException::class, XmlPullParserException::class, ParseException::class)
private fun parseFeed(feed: Feed, xmlParser: XmlPullParser) {
    while (xmlParser.nextTag() != XmlPullParser.END_TAG) {
        when(xmlParser.name) {
            "totalResults" -> {
                setTotalResults(feed, xmlParser)
            }
            else -> {
                skip(xmlParser)
            }
        }
    }
}

@Throws(IOException::class, XmlPullParserException::class, ParseException::class)
private fun setTotalResults(feed: Feed, xmlParser: XmlPullParser) {
    while (xmlParser.next() != XmlPullParser.END_TAG) {
        if (xmlParser.eventType == XmlPullParser.TEXT) {
            val data = xmlParser.text
            if (data.isNotBlank()) {
                Log.e("ApiError", "Data $data" )
                feed.totalResults = data.toInt()
            } else Log.e("ApiError", "Data Null" )
        } else Log.e("ApiError", "No Data Type" )
    }
}

private fun getData(parser: XmlPullParser): String {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    result.replace("\\s+", "")
    return result
}

private fun skip(parser: XmlPullParser) {
//    if (parser.eventType != XmlPullParser.START_TAG) {
//        throw IllegalStateException()
//    }
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}

