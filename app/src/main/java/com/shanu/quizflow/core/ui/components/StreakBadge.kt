package com.shanu.quizflow.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview
import com.shanu.quizflow.R
import com.shanu.quizflow.core.ui.theme.Dimens

@Composable
fun StreakBadge(
    currentStreak: Int,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (active) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = if (active) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val descriptionRes = if (active) R.plurals.streak_badge_active_description else R.plurals.streak_badge_inactive_description
    val description = pluralStringResource(descriptionRes, currentStreak, currentStreak)

    Surface(
        shape = CircleShape,
        color = containerColor,
        modifier = modifier
            .defaultMinSize(
                minWidth = Dimens.StreakBadgeContainerWidth,
                minHeight = Dimens.StreakBadgeContainerHeight,
            )
            .semantics { contentDescription = description },
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Dimens.SpaceSmall, vertical = Dimens.SpaceExtraSmall),
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(Dimens.StreakBadgeSize)
                    .padding(end = 2.dp),
            )
            Text(
                text = currentStreak.toString(),
                color = color,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun StreakBadgePreview() {
    QuizFlowPreview {
        StreakBadge(currentStreak = 3, active = true)
    }
}