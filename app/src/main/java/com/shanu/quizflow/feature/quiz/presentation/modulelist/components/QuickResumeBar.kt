package com.shanu.quizflow.feature.quiz.presentation.modulelist.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview
import com.shanu.quizflow.feature.quiz.domain.model.ModuleStatus
import com.shanu.quizflow.feature.quiz.presentation.modulelist.ModuleUi
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.R
import com.shanu.quizflow.core.ui.theme.Dimens

@Composable
fun QuickResumeBar(
    module: ModuleUi?,
    onResumeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val isVisible = module != null && visible
    val density = LocalDensity.current
    val initialTranslationPx = with(density) { 150.dp.toPx() }

    val translateY by animateFloatAsState(
        targetValue = if (isVisible) 0f else initialTranslationPx,
        animationSpec = tween(
            durationMillis = if (isVisible) 420 else 300,
            easing = FastOutSlowInEasing,
        ),
        label = "resumeBarTranslationY",
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = if (isVisible) 400 else 300),
        label = "resumeBarAlpha",
    )

    if (module != null) {
        // Outer container Box with generous padding (32.dp vertical tolerance) so the inner Surface's
        // 8.dp elevation shadow stays completely inside the outer Box's layout bounds and never gets cropped.
        Box(
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = translateY
                    this.alpha = alpha
                    clip = false
                }
                .padding(vertical = Dimens.SpaceExtraLarge, horizontal = Dimens.SpaceMedium),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        clip = false,
                    )
                    .border(
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        shape = CircleShape,
                    ),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpaceMedium, vertical = Dimens.SpaceSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
                    ) {
                        // Solid Circular Play Avatar
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(26.dp),
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.quick_resume_title),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Button(
                        onClick = { onResumeClick(module.id) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier.padding(start = Dimens.SpaceSmall),
                    ) {
                        Text(
                            text = stringResource(R.string.module_status_resume),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun QuickResumeBarPreview() {
    QuizFlowPreview {
        QuickResumeBar(
            module = ModuleUi(
                id = "1",
                title = "Jetpack Compose",
                description = "Declarative UI",
                status = ModuleStatus.IN_PROGRESS,
                totalQuestions = 10,
                lastScore = null,
                highScore = null,
            ),
            onResumeClick = {},
        )
    }
}

