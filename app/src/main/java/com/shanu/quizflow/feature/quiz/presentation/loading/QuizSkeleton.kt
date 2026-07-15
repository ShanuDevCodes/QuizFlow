package com.shanu.quizflow.feature.quiz.presentation.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.ui.components.SkipButton
import com.shanu.quizflow.core.ui.components.SpotlightSurface
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuestionProgressBar
import com.shanu.quizflow.core.ui.components.StreakBadge

private val QuestionCardSkeletonHeight = 140.dp

@Composable
fun QuizSkeleton(modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "skeletonShimmer").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "skeletonAlpha",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Question 0 of 10",
                style = MaterialTheme.typography.labelLarge,
            )
            StreakBadge(currentStreak = 0, active = false)
        }
        QuestionProgressBar(
            currentQuestion = 0,
            totalQuestions = 10,
            modifier = Modifier.padding(top = Dimens.SpaceSmall, bottom = Dimens.SpaceLarge)
        )
        SpotlightSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.SpaceLarge)
                .height(QuestionCardSkeletonHeight),
        ) {
            SkeletonBlock(
                height = QuestionCardSkeletonHeight - Dimens.SpaceLarge * 2,
                alpha = alpha,
                widthFraction = 0.9f,
            )
        }
        repeat(4) {
            SkeletonBlock(
                height = Dimens.OptionCardMinHeight,
                alpha = alpha,
                modifier = Modifier.padding(bottom = Dimens.SpaceSmall),
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            SkipButton(onClick = {}, enabled = false)
        }
    }
}

@Composable
private fun SkeletonBlock(
    height: Dp,
    alpha: Float,
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .alpha(alpha)
            .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

@Preview(showBackground = true)
@Composable
private fun QuizSkeletonPreview() {
    QuizFlowTheme(dynamicColor = false) {
        QuizSkeleton()
    }
}