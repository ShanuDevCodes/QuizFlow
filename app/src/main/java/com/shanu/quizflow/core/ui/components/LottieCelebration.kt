package com.shanu.quizflow.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieCelebration(
    assetFileName: String,
    modifier: Modifier = Modifier,
    iterations: Int = 1,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetFileName))
    val progress by animateLottieCompositionAsState(composition, iterations = iterations)
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
    )
}
