package com.shanu.quizflow.feature.quiz.presentation.modulelist.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview
import com.shanu.quizflow.feature.quiz.presentation.modulelist.OverallProgress
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.R
import com.shanu.quizflow.core.ui.components.SpotlightSurface
import com.shanu.quizflow.core.ui.theme.Dimens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CircularWavyProgressIndicator

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import kotlinx.coroutines.delay

@Composable
fun OverallProgressCard(
    progress: OverallProgress,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val targetProgress = progress.completionPercentage / 100f
    var animatedTarget by remember(targetProgress, visible) { mutableFloatStateOf(0f) }

    LaunchedEffect(targetProgress, visible) {
        if (visible) {
            delay(280.milliseconds) // Wait for PopIn spring animation to complete
            animatedTarget = targetProgress
        } else {
            animatedTarget = 0f
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = animatedTarget,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "completionProgress",
    )

    SpotlightSurface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(Dimens.CornerRadiusMedium)),
        contentAlignment = Alignment.TopStart,
        spotlightAlignment = Alignment.BottomStart,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceMedium),
        ) {
            // Top row: heading + circular ring
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                // Left: YOUR PROGRESS label + greeting
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimens.SpaceMedium),
                ) {
                    Text(
                        text = stringResource(R.string.overall_progress_heading),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(Dimens.SpaceExtraSmall))
                    Text(
                        text = stringResource(R.string.overall_progress_greeting),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Right: circular ring with % inside
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularWavyProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        amplitude = {1f},
                        waveSpeed = 5.dp
                    )
                    Text(
                        text = "${progress.completionPercentage}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Bottom row: MODULES | TOTAL SCORE | AVG SCORE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                StatBlock(
                    label = stringResource(R.string.progress_modules_label),
                    value = "${progress.completedModulesCount}/${progress.totalModulesCount}",
                    modifier = Modifier.weight(1f),
                )

                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )

                StatBlock(
                    label = stringResource(R.string.progress_total_score_uppercase),
                    value = "${progress.totalScore}/${progress.totalModulesCount * 10}",
                    modifier = Modifier.weight(1f),
                )

                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )

                StatBlock(
                    label = stringResource(R.string.progress_avg_score_uppercase),
                    value = if (progress.averageScore > 0)
                        String.format(LocalLocale.current.platformLocale, "%.0f%%", progress.averageScore * 10)
                    else "—",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@ComponentPreviews
@Composable
private fun OverallProgressCardPreview() {
    QuizFlowPreview {
        OverallProgressCard(
            progress = OverallProgress(
                completedModulesCount = 3,
                totalModulesCount = 10,
                completionPercentage = 30,
                totalScore = 27,
                averageScore = 9.0f,
            ),
        )
    }
}
