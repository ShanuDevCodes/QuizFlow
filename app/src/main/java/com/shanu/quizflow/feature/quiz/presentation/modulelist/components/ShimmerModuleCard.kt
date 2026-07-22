package com.shanu.quizflow.feature.quiz.presentation.modulelist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.ui.components.shimmerEffect
import com.shanu.quizflow.core.ui.theme.Dimens

@Composable
fun ShimmerModuleCard(modifier: Modifier = Modifier) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(Dimens.SpaceSmall))
                    .shimmerEffect(),
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceExtraSmall))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(Dimens.SpaceSmall))
                    .shimmerEffect(),
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(Dimens.SpaceSmall))
                    .shimmerEffect(),
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(84.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
                        .shimmerEffect(),
                )
            }
        }
    }
}
