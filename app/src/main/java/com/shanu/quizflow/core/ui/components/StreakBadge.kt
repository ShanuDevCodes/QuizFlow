package com.shanu.quizflow.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.shanu.quizflow.core.ui.theme.Dimens

@Composable
fun StreakBadge(
    currentStreak: Int,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (active) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = if (active) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = containerColor,
        modifier = modifier
            .height(Dimens.StreakBadgeContainerHeight)
            .width(Dimens.StreakBadgeContainerWidth)
            .semantics {
                contentDescription = if (active) {
                    "Streak badge lit: $currentStreak correct answers in a row"
                } else {
                    "Streak: $currentStreak correct answers in a row"
                }
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.SpaceExtraSmall, vertical = Dimens.SpaceExtraSmall),
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(Dimens.StreakBadgeSize),
            )
            Text(
                text = currentStreak.toString(),
                color = color,
            )
        }
    }
}
