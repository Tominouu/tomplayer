package com.tomplayer.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.ProgressInfo
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.ui.components.CategoryRow
import com.tomplayer.app.ui.components.TvChannelCard
import com.tomplayer.app.ui.components.TvTopBar
import com.tomplayer.app.ui.theme.NetflixBlack
import com.tomplayer.app.ui.theme.NetflixDarkGrey
import com.tomplayer.app.ui.theme.NetflixGrey
import com.tomplayer.app.ui.theme.NetflixLightGrey
import com.tomplayer.app.ui.theme.NetflixRed
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite60
import com.tomplayer.app.ui.theme.NetflixWhite80

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenPlayer: (String, String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEpg: () -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val channels by viewModel.channels.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val series by viewModel.series.collectAsState()
    val liveCategories by viewModel.liveCategories.collectAsState()
    val movieCategories by viewModel.movieCategories.collectAsState()
    val seriesCategories by viewModel.seriesCategories.collectAsState()
    val favoriteChannels by viewModel.favoriteChannels.collectAsState()
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val favoriteSeries by viewModel.favoriteSeries.collectAsState()
    val featuredMovies by viewModel.featuredMovies.collectAsState()
    val resumeContent by viewModel.resumeContent.collectAsState()
    val hasContent by viewModel.hasContent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedLiveCategory by viewModel.selectedLiveCategory.collectAsState()
    val selectedMovieCategory by viewModel.selectedMovieCategory.collectAsState()
    val selectedSeriesCategory by viewModel.selectedSeriesCategory.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NetflixBlack)
    ) {
        if (!hasContent && !isLoading) {
            EmptyState(onNavigateToSettings = onNavigateToSettings)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {
                item {
                    TvTopBar(
                        title = "TomPlayer",
                        onSearchClick = onNavigateToSearch,
                        onSettingsClick = onNavigateToSettings
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    TabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (selectedTab) {
                    ContentTab.LIVE -> {
                        item {
                            Text(
                                text = "Catégories",
                                modifier = Modifier.padding(horizontal = 48.dp),
                                color = NetflixWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CategoryRow(
                                categories = liveCategories,
                                selectedCategoryId = selectedLiveCategory,
                                onCategorySelected = { viewModel.selectLiveCategory(it) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (favoriteChannels.isNotEmpty()) {
                            item {
                                SectionHeader("Mes chaînes")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 48.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(favoriteChannels) { ch ->
                                        TvChannelCard(
                                            name = ch.name, logoUrl = ch.logoUrl,
                                            onClick = { onOpenPlayer("live", ch.id) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        item {
                            SectionHeader("Toutes les chaînes")
                            Spacer(modifier = Modifier.height(8.dp))
                            ChannelGridCompact(
                                channels = channels,
                                onChannelClick = { onOpenPlayer("live", it.id) }
                            )
                        }
                    }

                    ContentTab.MOVIES -> {
                        item {
                            if (featuredMovies.isNotEmpty()) {
                                HeroMovieCard(
                                    movie = featuredMovies.first(),
                                    onPlay = { onOpenPlayer("movie", it.id) },
                                    onFavorite = { viewModel.toggleMovieFavorite(it.id) }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        item {
                            Text(
                                text = "Catégories",
                                modifier = Modifier.padding(horizontal = 48.dp),
                                color = NetflixWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CategoryRow(
                                categories = movieCategories,
                                selectedCategoryId = selectedMovieCategory,
                                onCategorySelected = { viewModel.selectMovieCategory(it) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (resumeContent.isNotEmpty()) {
                            item {
                                SectionHeader("Reprendre")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 48.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(resumeContent.filter { it.contentType.name == "MOVIE" }) { prog ->
                                        val movie = movies.find { it.id == prog.contentId }
                                        if (movie != null) {
                                            MovieCard(movie = movie, onPlay = { onOpenPlayer("movie", movie.id) })
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        if (favoriteMovies.isNotEmpty()) {
                            item {
                                SectionHeader("Mes films")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 48.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(favoriteMovies) { m ->
                                        MovieCard(movie = m, onPlay = { onOpenPlayer("movie", m.id) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        item {
                            SectionHeader("Tous les films")
                            Spacer(modifier = Modifier.height(8.dp))
                            MovieGrid(
                                movies = movies,
                                onMovieClick = { onOpenPlayer("movie", it.id) },
                                onFavorite = { viewModel.toggleMovieFavorite(it.id) }
                            )
                        }
                    }

                    ContentTab.SERIES -> {
                        item {
                            Text(
                                text = "Catégories",
                                modifier = Modifier.padding(horizontal = 48.dp),
                                color = NetflixWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CategoryRow(
                                categories = seriesCategories,
                                selectedCategoryId = selectedSeriesCategory,
                                onCategorySelected = { viewModel.selectSeriesCategory(it) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (favoriteSeries.isNotEmpty()) {
                            item {
                                SectionHeader("Mes séries")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 48.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(favoriteSeries) { s ->
                                        PosterCard(
                                            title = s.name,
                                            imageUrl = s.coverUrl,
                                            onClick = { onOpenPlayer("series", s.id) },
                                            rating = s.rating
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        item {
                            SectionHeader("Toutes les séries")
                            Spacer(modifier = Modifier.height(8.dp))
                            PosterGrid(
                                items = series,
                                title = { it.name },
                                imageUrl = { it.coverUrl },
                                subtitle = { it.year },
                                onClick = { s -> onOpenPlayer("series", s.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabRow(
    selectedTab: ContentTab,
    onTabSelected: (ContentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabButton(label = "En Direct", icon = "\uD83D\uDCFA", isSelected = selectedTab == ContentTab.LIVE,
            onClick = { onTabSelected(ContentTab.LIVE) })
        TabButton(label = "Films", icon = "\uD83C\uDFAC", isSelected = selectedTab == ContentTab.MOVIES,
            onClick = { onTabSelected(ContentTab.MOVIES) })
        TabButton(label = "Séries", icon = "\uD83D\uDCFD\uFE0F", isSelected = selectedTab == ContentTab.SERIES,
            onClick = { onTabSelected(ContentTab.SERIES) })
    }
}

@Composable
private fun TabButton(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> NetflixRed
            isFocused -> NetflixGrey
            else -> NetflixDarkGrey
        },
        modifier = Modifier.onFocusChanged { isFocused = it.isFocused }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = icon, fontSize = 20.sp)
            Text(
                text = label,
                color = NetflixWhite,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

// ─── Movie Hero ───────────────────────────────────────────────

@Composable
private fun HeroMovieCard(
    movie: Movie,
    onPlay: (Movie) -> Unit,
    onFavorite: (Movie) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .padding(horizontal = 48.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (movie.backdropUrl != null) {
            AsyncImage(
                model = movie.backdropUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (movie.coverUrl != null) {
            AsyncImage(
                model = movie.coverUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(NetflixDarkGrey),
                contentAlignment = Alignment.Center
            ) {
                Text("\uD83C\uDFAC", fontSize = 64.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color.Transparent, NetflixBlack),
                        startY = 200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
        ) {
            Text(
                text = movie.name,
                color = NetflixWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                movie.year?.let { Text(it, color = NetflixWhite80, fontSize = 14.sp) }
                movie.rating?.let { Text("\u2605 $it", color = NetflixRed, fontSize = 14.sp) }
                movie.duration?.let { Text(it, color = NetflixWhite80, fontSize = 14.sp) }
            }
            if (movie.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.description,
                    color = NetflixWhite60,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PlayButton(label = "Lecture", onClick = { onPlay(movie) })
                var fFocused by remember { mutableStateOf(false) }
                Surface(
                    onClick = { onFavorite(movie) },
                    shape = CircleShape,
                    color = if (fFocused) NetflixGrey else NetflixDarkGrey,
                    modifier = Modifier.size(48.dp).onFocusChanged { fFocused = it.isFocused }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (movie.isFavorite) "\u2764" else "\u2661", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = NetflixRed
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("\u25B6", fontSize = 18.sp, color = NetflixWhite)
            Text(label, color = NetflixWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Movie Grid ────────────────────────────────────────────────

@Composable
private fun MovieGrid(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onFavorite: (Movie) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        contentPadding = PaddingValues(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        itemsIndexed(movies) { _, movie ->
            MovieCard(movie = movie, onPlay = onMovieClick)
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onPlay: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = { onPlay(movie) },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) NetflixGrey else NetflixDarkGrey
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .background(NetflixDarkGrey),
                contentAlignment = Alignment.Center
            ) {
                if (movie.coverUrl != null) {
                    AsyncImage(
                        model = movie.coverUrl,
                        contentDescription = movie.name,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("\uD83C\uDFAC", fontSize = 32.sp)
                }

                if (movie.progressPercent > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.BottomCenter)
                            .background(NetflixDarkGrey)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(movie.progressPercent)
                                .height(3.dp)
                                .background(NetflixRed)
                        )
                    }
                }
            }

            Text(
                text = movie.name,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                color = NetflixWhite,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                movie.year?.let {
                    Text(it, color = NetflixWhite60, fontSize = 11.sp)
                }
                movie.rating?.let {
                    Text("\u2605 $it", color = NetflixRed, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─── Poster Card (for Series) ────────────────────────────────

@Composable
fun PosterCard(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit,
    subtitle: String? = null,
    rating: String? = null,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier
            .width(200.dp)
            .onFocusChanged { isFocused = it.isFocused },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) NetflixGrey else NetflixDarkGrey
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .background(NetflixDarkGrey),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("\uD83D\uDCFD\uFE0F", fontSize = 32.sp)
                }
            }

            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = NetflixWhite,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            if (rating != null || subtitle != null) {
                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rating?.let { Text("\u2605 $it", color = NetflixRed, fontSize = 11.sp) }
                    subtitle?.let { Text(it, color = NetflixWhite60, fontSize = 11.sp) }
                }
            }
        }
    }
}

@Composable
fun <T> PosterGrid(
    items: List<T>,
    title: (T) -> String,
    imageUrl: (T) -> String?,
    onClick: (T) -> Unit,
    subtitle: ((T) -> String?)? = null,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        contentPadding = PaddingValues(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        itemsIndexed(items) { _, item ->
            PosterCard(
                title = title(item),
                imageUrl = imageUrl(item),
                onClick = { onClick(item) },
                subtitle = subtitle?.invoke(item)
            )
        }
    }
}

// ─── Channel Grid Compact ─────────────────────────────────────

@Composable
private fun ChannelGridCompact(
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(channels) { _, channel ->
            TvChannelCard(
                name = channel.name,
                logoUrl = channel.logoUrl,
                onClick = { onChannelClick(channel) }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 48.dp),
        color = NetflixWhite,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmptyState(onNavigateToSettings: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TomPlayer",
                color = NetflixRed,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Ajoutez une playlist pour commencer",
                color = NetflixWhite80,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Surface(
                onClick = onNavigateToSettings,
                shape = RoundedCornerShape(12.dp),
                color = NetflixRed
            ) {
                Text(
                    "Ajouter une playlist",
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp),
                    color = NetflixWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
