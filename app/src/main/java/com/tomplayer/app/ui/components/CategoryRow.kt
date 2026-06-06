package com.tomplayer.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomplayer.app.data.model.Category
import com.tomplayer.app.ui.theme.NetflixDarkGrey
import com.tomplayer.app.ui.theme.NetflixRed
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite80

@Composable
fun CategoryRow(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.height(56.dp),
        contentPadding = PaddingValues(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CategoryChip(
                label = "Toutes",
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )
        }

        itemsIndexed(categories) { _, category ->
            CategoryChip(
                label = "${category.name} (${category.channelCount})",
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    androidx.compose.material3.Surface(
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused },
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = when {
            isSelected -> NetflixRed
            isFocused -> NetflixRed.copy(alpha = 0.7f)
            else -> NetflixDarkGrey
        }
    ) {
        androidx.compose.material3.Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = if (isSelected || isFocused) NetflixWhite else NetflixWhite80,
            fontSize = 14.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Medium else FontWeight.Normal
        )
    }
}
