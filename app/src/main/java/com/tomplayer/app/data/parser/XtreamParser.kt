package com.tomplayer.app.data.parser

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.Episode
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.Season
import com.tomplayer.app.data.model.Series

data class XtreamCategory(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int = 0
)

data class XtreamChannel(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String? = null,
    @SerializedName("stream_id") val streamId: String,
    @SerializedName("stream_icon") val streamIcon: String? = null,
    @SerializedName("epg_channel_id") val epgChannelId: String? = null,
    @SerializedName("added") val added: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("tv_archive") val tvArchive: Int = 0,
    @SerializedName("direct_source") val directSource: String? = null,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: Int = 0
)

data class XtreamMovie(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("stream_id") val streamId: String,
    @SerializedName("stream_icon") val streamIcon: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("year") val year: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("director") val director: String? = null,
    @SerializedName("cast") val cast: String? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null,
    @SerializedName("youtube_trailer") val youtubeTrailer: String? = null,
    @SerializedName("rating_imdb") val ratingImdb: String? = null
)

data class XtreamSeries(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("series_id") val seriesId: String,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("rating_imdb") val ratingImdb: String? = null,
    @SerializedName("year") val year: String? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("last_updated") val lastUpdated: String? = null
)

data class XtreamSeriesInfo(
    @SerializedName("seasons") val seasons: List<XtreamSeason>? = null,
    @SerializedName("episodes") val episodes: Map<String, List<XtreamEpisode>>? = null
)

data class XtreamSeason(
    @SerializedName("id") val id: String,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String? = null
)

data class XtreamEpisode(
    @SerializedName("id") val id: String,
    @SerializedName("num") val episodeNum: Int,
    @SerializedName("title") val title: String? = null,
    @SerializedName("info") val info: XtreamEpisodeInfo? = null
)

data class XtreamEpisodeInfo(
    @SerializedName("movie_image") val movieImage: String? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("duration") val duration: String? = null
)

data class XtreamResponse(
    @SerializedName("live_categories") val liveCategories: List<XtreamCategory>? = null,
    @SerializedName("movie_categories") val movieCategories: List<XtreamCategory>? = null,
    @SerializedName("series_categories") val seriesCategories: List<XtreamCategory>? = null,
    @SerializedName("live_streams") val liveStreams: List<XtreamChannel>? = null,
    @SerializedName("movie_streams") val movieStreams: List<XtreamMovie>? = null,
    @SerializedName("series_streams") val seriesStreams: List<XtreamSeries>? = null,
    @SerializedName("user_info") val userInfo: Map<String, Any>? = null,
    @SerializedName("server_info") val serverInfo: Map<String, Any>? = null
)

object XtreamParser {

    private val gson = Gson()

    fun parseLiveCategories(json: String): List<Category> {
        return try {
            val response = gson.fromJson(json, XtreamResponse::class.java)
            response.liveCategories?.map { Category(id = it.categoryId, name = it.categoryName) }
                ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun parseMovieCategories(json: String): List<Category> {
        return try {
            val response = gson.fromJson(json, XtreamResponse::class.java)
            response.movieCategories?.map { Category(id = it.categoryId, name = it.categoryName) }
                ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun parseSeriesCategories(json: String): List<Category> {
        return try {
            val response = gson.fromJson(json, XtreamResponse::class.java)
            response.seriesCategories?.map { Category(id = it.categoryId, name = it.categoryName) }
                ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun parseLiveStreams(json: String, baseUrl: String, username: String, password: String): List<Channel> {
        return try {
            val response = gson.fromJson(json, XtreamResponse::class.java)
            response.liveStreams?.map { stream ->
                Channel(
                    id = stream.streamId,
                    name = stream.name,
                    logoUrl = stream.streamIcon,
                    streamUrl = "$baseUrl/live/$username/$password/${stream.streamId}.m3u8",
                    category = stream.categoryId,
                    epgChannelId = stream.epgChannelId,
                    tvgId = stream.epgChannelId
                )
            } ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun parseMovies(json: String, baseUrl: String, username: String, password: String): List<Movie> {
        return try {
            val response = gson.fromJson(json, XtreamResponse::class.java)
            response.movieStreams?.map { movie ->
                Movie(
                    id = movie.streamId,
                    name = movie.name,
                    streamUrl = "$baseUrl/movie/$username/$password/${movie.streamId}.mp4",
                    coverUrl = movie.streamIcon,
                    backdropUrl = movie.backdropPath?.firstOrNull(),
                    category = movie.categoryId,
                    year = movie.year ?: movie.rating,
                    rating = movie.ratingImdb ?: movie.rating,
                    duration = movie.duration,
                    description = movie.plot,
                    director = movie.director,
                    cast = movie.cast,
                    genre = movie.genre
                )
            } ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun parseSeriesList(json: String): List<Series> {
        return try {
            val response = gson.fromJson(json, XtreamResponse::class.java)
            response.seriesStreams?.map { series ->
                Series(
                    id = series.seriesId,
                    name = series.name,
                    coverUrl = series.cover,
                    backdropUrl = series.backdropPath?.firstOrNull(),
                    category = series.categoryId,
                    year = series.year,
                    rating = series.ratingImdb ?: series.rating,
                    description = series.plot,
                    genre = series.genre
                )
            } ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun parseSeriesInfo(json: String, seriesId: String, baseUrl: String, username: String, password: String): Series? {
        return try {
            val info = gson.fromJson(json, XtreamSeriesInfo::class.java)
            val baseSeries = findSeriesById(seriesId) ?: return null

            val seasons = info.seasons?.map { season ->
                val episodeList = info.episodes?.get(season.id)?.map { ep ->
                    Episode(
                        id = ep.id,
                        name = ep.title ?: "Episode ${ep.episodeNum}",
                        streamUrl = "$baseUrl/series/$username/$password/${ep.id}.mp4",
                        seasonNumber = season.seasonNumber,
                        episodeNumber = ep.episodeNum,
                        coverUrl = ep.info?.movieImage,
                        description = ep.info?.plot,
                        duration = ep.info?.duration
                    )
                } ?: emptyList()

                Season(
                    id = season.id,
                    number = season.seasonNumber,
                    name = season.name ?: "Season ${season.seasonNumber}",
                    episodes = episodeList
                )
            } ?: emptyList()

            baseSeries.copy(seasons = seasons)
        } catch (e: Exception) { null }
    }

    private var lastSeriesList: List<Series> = emptyList()
    private var lastBaseUrl = ""
    private var lastUsername = ""
    private var lastPassword = ""

    fun storeSeriesContext(series: List<Series>, baseUrl: String, username: String, password: String) {
        lastSeriesList = series
        lastBaseUrl = baseUrl
        lastUsername = username
        lastPassword = password
    }

    private fun findSeriesById(seriesId: String): Series? {
        return lastSeriesList.find { it.id == seriesId }
    }
}
