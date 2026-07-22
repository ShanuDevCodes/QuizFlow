package com.shanu.quizflow.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview

private const val SpotlightAlpha = 0.35f

@Composable
fun SpotlightSurface(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    contentAlignment: Alignment = Alignment.Center,
    spotlightAlignment: Alignment = Alignment.TopEnd,
    spotlightSize: Dp = Dimens.SpotlightSize,
    border: BorderStroke? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(Dimens.CornerRadiusMedium)
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(if (border != null) Modifier.border(border, shape) else Modifier),
        contentAlignment = contentAlignment,
    ) {
        val xOffset = when (spotlightAlignment) {
            Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> Dimens.SpotlightOffset
            else -> -Dimens.SpotlightOffset
        }
        val yOffset = when (spotlightAlignment) {
            Alignment.TopStart, Alignment.TopEnd, Alignment.TopCenter -> -Dimens.SpotlightOffset
            else -> Dimens.SpotlightOffset
        }
        Box(
            modifier = Modifier
                .align(spotlightAlignment)
                .offset(x = xOffset, y = yOffset)
                .size(spotlightSize)
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

@ComponentPreviews
@Composable
private fun SpotlightSurfacePreview() {
    QuizFlowPreview {
        SpotlightSurface(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Spotlight Surface Card",
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}