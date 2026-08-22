package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CardBg
import com.example.ui.theme.MutedText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonYellow

@Composable
fun AdBanner(
    isSimulationMode: Boolean,
    isGpsLocked: Boolean,
    isVoiceEnabled: Boolean,
    onToggleVoice: () -> Unit,
    onToggleSim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.Black)
            .border(width = 0.5.dp, color = BorderDark)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand / Tag
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E2433))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "AD / PRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Google AdMob • AeroStride GPS",
                fontSize = 11.sp,
                color = MutedText
            )
        }

        // Quick status badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice coach toggle button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isVoiceEnabled) Color(0xFF003814) else CardBg)
                    .clickable { onToggleVoice() }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Voice Coach",
                        tint = if (isVoiceEnabled) NeonGreen else MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isVoiceEnabled) "VOICE" else "MUTE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVoiceEnabled) NeonGreen else MutedText
                    )
                }
            }

            // GPS / SIM Mode toggle button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSimulationMode) Color(0xFF332A00) else if (isGpsLocked) Color(0xFF002933) else CardBg)
                    .clickable { onToggleSim() }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSimulationMode) Icons.Default.FastForward else Icons.Default.GpsFixed,
                        contentDescription = "GPS / Sim Mode",
                        tint = if (isSimulationMode) NeonYellow else if (isGpsLocked) NeonCyan else MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isSimulationMode) "SIM 13.5k" else "GPS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSimulationMode) NeonYellow else if (isGpsLocked) NeonCyan else MutedText
                    )
                }
            }
        }
    }
}
