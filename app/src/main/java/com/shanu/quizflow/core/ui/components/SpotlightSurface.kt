package com.shanu.quizflow.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.shanu.quizflow.core.ui.theme.Dimens

private const val SpotlightAlpha = 0.35f

// Shared surfaceContainer card with a soft radial-gradient color accent bleeding in from the top-right corner.
@Composable
fun SpotlightSurface(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = contentAlignment,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Dimens.SpotlightOffset, y = -Dimens.SpotlightOffset)
                .size(Dimens.SpotlightSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = SpotlightAlpha), Color.Transparent),
                    ),
                ),
        )
        content()
    }
}
