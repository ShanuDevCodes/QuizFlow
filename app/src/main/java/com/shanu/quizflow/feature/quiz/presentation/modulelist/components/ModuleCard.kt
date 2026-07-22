package com.shanu.quizflow.feature.quiz.presentation.modulelist.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview
import com.shanu.quizflow.feature.quiz.domain.model.ModuleStatus
import com.shanu.quizflow.feature.quiz.presentation.modulelist.ModuleUi
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.R
import com.shanu.quizflow.core.ui.theme.Dimens

@Composable
fun ModuleCard(
    module: ModuleUi,
    onStartClick: () -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val targetProgress = (module.highScore?.toFloat() ?: 0f) / module.totalQuestions.coerceAtLeast(1)
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

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpaceMedium),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Card(
                    shape = CircleShape,
                    colors = if(targetProgress < 1f){
                        CardDefaults.cardColors()
                    }else{
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    }
                ){
                    Text(
                        text = if(targetProgress == 0f){
                            "Not Attempted"
                        }else if (targetProgress < 1f){
                            "Attempted"
                        } else{
                            "Completed"
                        },
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(Dimens.SpaceSmall)
                    )
                }
            }
            Text(
                text = module.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpaceExtraSmall, bottom = Dimens.SpaceSmall),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            val progressText = if (module.highScore != null) {
                "${module.highScore} / ${module.totalQuestions}"
            } else {
                "0 / ${module.totalQuestions}"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearWavyProgressIndicator(
                    progress = { animatedProgress },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    waveSpeed = 10.dp,
                    amplitude = {1f}
                )
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (module.status) {
                    ModuleStatus.IN_PROGRESS -> {
                        if (module.lastScore != null) {
                            OutlinedButton(onClick = onReviewClick) {
                                Text(
                                    text = stringResource(R.string.module_status_review),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Button(onClick = onStartClick) {
                            Text(
                                text = stringResource(R.string.module_status_resume),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    ModuleStatus.COMPLETED -> {
                        OutlinedButton(onClick = onReviewClick) {
                            Text(
                                text = stringResource(R.string.module_status_review),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Button(onClick = onStartClick) {
                            Text(
                                text = stringResource(R.string.module_status_start),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    ModuleStatus.NOT_STARTED -> {
                        Button(onClick = onStartClick) {
                            Text(
                                text = stringResource(R.string.module_status_start),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun ModuleCardPreview() {
    QuizFlowPreview {
        ModuleCard(
            module = ModuleUi(
                id = "1",
                title = "Jetpack Compose",
                description = "Build modern Android UIs declaratively.",
                status = ModuleStatus.COMPLETED,
                totalQuestions = 10,
                lastScore = 8,
                highScore = 9,
            ),
            onStartClick = {},
            onReviewClick = {},
        )
    }
}

