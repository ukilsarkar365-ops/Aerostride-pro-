package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LapSplit
import com.example.ui.theme.*
import com.example.ui.tracker.RunStatus

@Composable
fun HudDashboard(
    status: RunStatus,
    distanceMeters: Double,
    elapsedSeconds: Long,
    currentSpeedKmh: Double,
    paceSecondsPerKm: Long,
    caloriesBurned: Int,
    currentLap: Int,
    totalLaps: Int,
    targetLapSeconds: Double,
    lapSplits: List<LapSplit>,
    isBengali: Boolean,
    onStartRun: () -> Unit,
    onPauseRun: () -> Unit,
    onResumeRun: () -> Unit,
    onStopRun: () -> Unit,
    onOpenHistory: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val distKm = distanceMeters / 1000.0
    val distString = String.format("%.2f", distKm)

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val paceFormatted = if (paceSecondsPerKm > 0) {
        val pMin = paceSecondsPerKm / 60
        val pSec = paceSecondsPerKm % 60
        String.format("%d'%02d\"", pMin, pSec)
    } else "--'--\""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 20.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(CardBg)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(Color(0xFF2E3852), Color(0xFF141722))),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // HUD Top Bar with History & Language quick icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (status == RunStatus.RUNNING) NeonGreen else if (status == RunStatus.PAUSED) NeonYellow else MutedText)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (status) {
                            RunStatus.RUNNING -> if (isBengali) "রানিং ট্র্যাকার সক্রিয়" else "LIVE TRACKING"
                            RunStatus.PAUSED -> if (isBengali) "সাময়িক বিরতি" else "PAUSED"
                            else -> if (isBengali) "অ্যাথলেটিক মোড প্রস্তুত" else "READY TO SPRINT"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            RunStatus.RUNNING -> NeonGreen
                            RunStatus.PAUSED -> NeonYellow
                            else -> MutedText
                        }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // History Icon
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Workout History",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Language toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBg)
                            .border(0.5.dp, BorderDark, RoundedCornerShape(6.dp))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBengali) "বাংলা" else "ENG",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }
            }

            // Main Metric: Huge Distance Display & Lap Target counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Distance Value
                Column {
                    Text(
                        text = distString,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = NeonGreen,
                        lineHeight = 48.sp
                    )
                    Text(
                        text = if (isBengali) "কিলোমিটার / KM" else "KILOMETERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 1.sp
                    )
                }

                // Lap Tracker & Target Pace Info
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (isBengali) "Lap: ${currentLap.coerceAtMost(totalLaps)} / $totalLaps" else "Lap: ${currentLap.coerceAtMost(totalLaps)} / $totalLaps",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonYellow
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Target: ${String.format("%.1f", targetLapSeconds)}s/lap",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Column Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F121C))
                    .border(1.dp, Color(0xFF1E2436), RoundedCornerShape(14.dp))
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox(
                    label = if (isBengali) "সময়" else "TIME",
                    value = timeFormatted,
                    valueColor = Color.White
                )
                StatBox(
                    label = if (isBengali) "গতি (km/h)" else "SPEED",
                    value = String.format("%.1f", currentSpeedKmh),
                    valueColor = NeonCyan
                )
                StatBox(
                    label = if (isBengali) "পেস /কিমি" else "PACE",
                    value = paceFormatted,
                    valueColor = Color.White
                )
                StatBox(
                    label = if (isBengali) "ক্যালোরি" else "KCAL",
                    value = "$caloriesBurned",
                    valueColor = NeonYellow
                )
            }

            // Lap Split Performance Log / Drawer
            if (lapSplits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 110.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A0C13))
                        .border(0.5.dp, Color(0xFF191F2E), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isBengali) "লাইভ ল্যাপ পারফরম্যান্স (SPLITS)" else "LIVE LAP SPLITS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(lapSplits.reversed()) { _, split ->
                            LapSplitRow(split = split, isBengali = isBengali)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            when (status) {
                RunStatus.IDLE, RunStatus.FINISHED -> {
                    Button(
                        onClick = onStartRun,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "START RUN (দৌড় শুরু করুন)" else "START RUN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                RunStatus.RUNNING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onPauseRun,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF222838),
                                contentColor = NeonYellow
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("pause_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = NeonYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "PAUSE (বিরতি)" else "PAUSE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onStopRun,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("stop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "STOP RUN" else "STOP RUN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                RunStatus.PAUSED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onResumeRun,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("resume_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "RESUME" else "RESUME",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Button(
                            onClick = onStopRun,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("finish_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Finish",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "FINISH & SAVE" else "FINISH & SAVE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MutedText,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun LapSplitRow(
    split: LapSplit,
    isBengali: Boolean
) {
    val diffFormatted = String.format("%.1f", Math.abs(split.diffSeconds))
    val diffLabel = if (split.diffSeconds > 0) "+${diffFormatted}s Slow" else "-${diffFormatted}s Fast"
    val diffColor = if (split.isFast) NeonGreen else NeonRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lap ${split.lapNumber} (${split.lapDistanceMeters.toInt()}m)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            text = "${split.lapDurationSeconds}s",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = diffLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = diffColor
        )
    }
}
