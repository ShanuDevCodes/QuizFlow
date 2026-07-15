package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.shanu.quizflow.core.ui.theme.Dimens

@Composable
fun OptionCard(
    option: OptionUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = option.state == OptionState.DEFAULT
    val (containerColor, contentColor) = optionColors(option.state)
    val animatedContainerColor by animateColorAsState(containerColor, label = "optionContainerColor")
    val borderColor = optionBorderColor(option.state)

    Surface(
        color = animatedContainerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
        border = borderColor?.let { BorderStroke(Dimens.OptionCardBorderWidth, it) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dimens.OptionCardMinHeight)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics {
                role = Role.Button
                option.accessibilityDescription()?.let { contentDescription = it }
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpaceMedium, vertical = Dimens.SpaceSmall),
        ) {
            Text(text = option.text, modifier = Modifier.weight(1f))
            optionIcon(option.state)?.let { icon ->
                Icon(imageVector = icon, contentDescription = null)
            }
        }
    }
}

@Composable
private fun optionColors(state: OptionState): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> =
    when (state) {
        OptionState.DEFAULT -> MaterialTheme.colorScheme.surfaceContainer to MaterialTheme.colorScheme.onSurface
        OptionState.CORRECT -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        OptionState.WRONG -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        OptionState.DIMMED -> MaterialTheme.colorScheme.surfaceContainerLow to MaterialTheme.colorScheme.onSurfaceVariant
    }

@Composable
private fun optionBorderColor(state: OptionState): androidx.compose.ui.graphics.Color? = when (state) {
    OptionState.CORRECT -> MaterialTheme.colorScheme.secondary
    OptionState.WRONG -> MaterialTheme.colorScheme.error
    else -> null
}

private fun optionIcon(state: OptionState) = when (state) {
    OptionState.CORRECT -> Icons.Filled.CheckCircle
    OptionState.WRONG -> Icons.Filled.Cancel
    else -> null
}

private fun OptionUi.accessibilityDescription(): String? = when (state) {
    OptionState.CORRECT -> "$text, correct answer"
    OptionState.WRONG -> "$text, your answer, incorrect"
    OptionState.DIMMED -> "$text"
    OptionState.DEFAULT -> null
}
