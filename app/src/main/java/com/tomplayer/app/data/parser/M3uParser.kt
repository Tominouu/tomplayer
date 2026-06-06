package com.tomplayer.app.data.parser

import com.tomplayer.app.data.model.Channel

object M3uParser {

    fun parse(content: String, sourceUrl: String? = null): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var i = 0

        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val metadata = parseExtInf(line)
                i++
                if (i < lines.size) {
                    val streamUrl = lines[i].trim()
                    if (streamUrl.isNotBlank() && !streamUrl.startsWith("#")) {
                        val channel = Channel(
                            id = metadata.id ?: streamUrl.hashCode().toString(),
                            name = metadata.name ?: "Unknown",
                            logoUrl = metadata.logoUrl,
                            streamUrl = streamUrl,
                            category = metadata.groupTitle,
                            epgChannelId = metadata.tvgId,
                            tvgName = metadata.tvgName,
                            tvgId = metadata.tvgId,
                            userAgent = metadata.userAgent,
                            referer = metadata.referer
                        )
                        channels.add(channel)
                    }
                }
            } else {
                i++
            }
        }
        return channels
    }

    private data class ExtInfMetadata(
        val id: String?,
        val name: String?,
        val logoUrl: String?,
        val groupTitle: String?,
        val tvgId: String?,
        val tvgName: String?,
        val userAgent: String?,
        val referer: String?
    )

    private fun parseExtInf(line: String): ExtInfMetadata {
        val tvgId = extractAttribute(line, "tvg-id")
        val tvgName = extractAttribute(line, "tvg-name")
        val tvgLogo = extractAttribute(line, "tvg-logo")
        val groupTitle = extractAttribute(line, "group-title")
        val userAgent = extractAttribute(line, "user-agent")
        val referer = extractAttribute(line, "referer")

        val name = line.substringAfterLast(",", "").trim()
        val id = tvgId ?: name.hashCode().toString()

        return ExtInfMetadata(
            id = id,
            name = name.ifBlank { null },
            logoUrl = tvgLogo,
            groupTitle = groupTitle,
            tvgId = tvgId,
            tvgName = tvgName,
            userAgent = userAgent,
            referer = referer
        )
    }

    private fun extractAttribute(line: String, attribute: String): String? {
        val regex = Regex("""$attribute="([^"]*)"""")
        return regex.find(line)?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
    }
}
