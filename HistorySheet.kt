package com.example.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RunSession
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    pastRuns: List<RunSession>,
    isBengali: Boolean,
    onDeleteRun: (Long) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BorderDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Runs",
                        tint = NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "দৌড়ের ইতিহাস ও রেকর্ড" else "RUN HISTORY & LOGS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                if (pastRuns.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text(
                            text = if (isBengali) "সব মুছুন" else "CLEAR ALL",
                            color = NeonRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (pastRuns.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "No Runs",
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBengali) "এখনও কোনো দৌড়ের রেকর্ড সংরক্ষিত নেই" else "No saved workout runs yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBengali) "টার্গেট সেট করে দৌড় সম্পন্ন করলে রেকর্ড এখানে জমা হবে।" else "Start a race run to record your lap splits and analytics.",
                            fontSize = 11.sp,
                            color = MutedText,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pastRuns, key = { it.id }) { session ->
                        HistoryCard(session = session, isBengali = isBengali, onDelete = { onDeleteRun(session.id) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HistoryCard(
    session: RunSession,
    isBengali: Boolean,
    onDelete: () -> Unit
) {
    val dateStr = remember(session.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(session.timestamp))
    }

    val distKm = session.totalDistanceMeters / 1000.0
    val distStr = String.format("%.2f", distKm)

    val min = session.durationSeconds / 60
    val sec = session.durationSeconds % 60
    val timeStr = String.format("%02d:%02d", min, sec)

    val paceMin = session.avgPaceSecondsPerKm / 60
    val paceSec = session.avgPaceSecondsPerKm % 60
    val paceStr = String.format("%d'%02d\"", paceMin, paceSec)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2436)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Race Title & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (session.isCompletedTarget) Color(0xFF003814) else Color(0xFF1B2233))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${session.raceTargetMeters.toInt()}m TARGET",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (session.isCompletedTarget) NeonGreen else NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateStr,
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$distStr km",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonGreen
                    )
                    Text(
                        text = if (isBengali) "মোট দূরত্ব" else "DISTANCE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }

                Column {
                    Text(
                        text = timeStr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (isBengali) "সময়" else "TIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }

                Column {
                    Text(
                        text = paceStr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan
                    )
                    Text(
                        text = if (isBengali) "গড় পেস /কিমি" else "AVG PACE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }

                Column {
                    Text(
                        text = "${session.caloriesBurned}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonYellow
                    )
                    Text(
                        text = "KCAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Track & Lap Summary tag
            Text(
                text = "${session.lapsCompleted} / ${session.totalLapsTarget} Laps (${session.trackSizeMeters.toInt()}m track) • Avg Speed ${String.format("%.1f", session.avgSpeedKmh)} km/h",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MutedText
            )
        }
    }
}
