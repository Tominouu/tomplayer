package com.tomplayer.app.data.parser

import com.tomplayer.app.data.model.EpgProgram
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object EpgParser {

    private val dateFormats = listOf(
        "yyyyMMddHHmmss Z",
        "yyyyMMddHHmmss ZZZZZ",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    )

    fun parse(xmlContent: String): List<EpgProgram> {
        val programs = mutableListOf<EpgProgram>()
        val now = System.currentTimeMillis()

        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xmlContent))

            var eventType = parser.eventType
            var currentChannelId: String? = null
            var currentTitle: String? = null
            var currentDescription: String? = null
            var currentStart: Long? = null
            var currentEnd: Long? = null
            var currentCategory: String? = null
            var currentIcon: String? = null
            var insideTitle = false
            var insideDesc = false
            var insideCategory = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "programme" -> {
                                currentChannelId = parser.getAttributeValue(null, "channel")
                                val startStr = parser.getAttributeValue(null, "start")
                                val stopStr = parser.getAttributeValue(null, "stop")
                                currentStart = parseDate(startStr)
                                currentEnd = parseDate(stopStr)
                                currentTitle = null
                                currentDescription = null
                                currentCategory = null
                                currentIcon = null
                            }
                            "title" -> insideTitle = true
                            "desc" -> insideDesc = true
                            "category" -> insideCategory = true
                            "icon" -> currentIcon = parser.getAttributeValue(null, "src")
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (insideTitle) currentTitle = parser.text
                        if (insideDesc) currentDescription = parser.text
                        if (insideCategory) currentCategory = parser.text
                    }
                    XmlPullParser.END_TAG -> {
                        when (parser.name) {
                            "title" -> insideTitle = false
                            "desc" -> insideDesc = false
                            "category" -> insideCategory = false
                            "programme" -> {
                                if (currentChannelId != null && currentTitle != null &&
                                    currentStart != null && currentEnd != null
                                ) {
                                    val program = EpgProgram(
                                        channelId = currentChannelId,
                                        title = currentTitle,
                                        description = currentDescription,
                                        startTime = currentStart,
                                        endTime = currentEnd,
                                        isNow = currentStart <= now && currentEnd > now,
                                        category = currentCategory,
                                        iconUrl = currentIcon
                                    )
                                    programs.add(program)
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return programs
    }

    private fun parseDate(dateStr: String?): Long? {
        if (dateStr == null) return null
        for (format in dateFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.parse(dateStr)?.time
            } catch (_: Exception) {}
        }
        return null
    }
}
