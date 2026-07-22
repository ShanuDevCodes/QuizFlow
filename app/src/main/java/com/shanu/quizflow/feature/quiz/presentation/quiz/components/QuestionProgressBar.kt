package com.shanu.quizflow.feature.quiz.presentation.quiz.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview

internal const val ProgressSegmentTag = "progressSegment"
internal const val ProgressSegmentFillTagPrefix = "progressSegmentFill_"

internal const val RevealFillDurationMillis = 1_000

@Composable
fun QuestionProgressBar(
    currentQuestion: Int,
    totalQuestions: Int,
    revealing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ProgressSegmentHeight),
        horizontalArrangement = Arrangement.spacedBy(Dimens.ProgressSegmentSpacing),
    ) {
        repeat(totalQuestions) { index ->
            val segmentNumber = index + 1
            ProgressSegment(
                state = when {
                    segmentNumber < currentQuestion -> SegmentState.COMPLETED
                    segmentNumber == currentQuestion && revealing -> SegmentState.FILLING
                    segmentNumber == currentQuestion -> SegmentState.ACTIVE
                    else -> SegmentState.UPCOMING
                },
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

private enum class SegmentState(val fillFraction: Float) {
    COMPLETED(1f),
    FILLING(1f),
    ACTIVE(0f),
    UPCOMING(0f),
}

@Composable
private fun ProgressSegment(state: SegmentState, modifier: Modifier = Modifier) {
    val fillFraction by animateFloatAsState(
        targetValue = state.fillFraction,
        animationSpec = if (state == SegmentState.FILLING) {
            tween(durationMillis = RevealFillDurationMillis)
        } else {
            tween(durationMillis = 0)
        },
        label = "progressSegmentFill",
    )

    Box(
        modifier = modifier
            .testTag(ProgressSegmentTag)
            .background(
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(50),
            ),
    ) {
        Box(
            modifier = Modifier
                .testTag(ProgressSegmentFillTagPrefix + state.name.lowercase())
                .fillMaxHeight()
                .fillMaxWidth(fillFraction)
                .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50)),
        )
    }
}

@ComponentPreviews
@Composable
private fun QuestionProgressBarPreview() {
    QuizFlowPreview {
        QuestionProgressBar(
            currentQuestion = 3,
            totalQuestions = 10,
            revealing = false,
        )
    }
}

