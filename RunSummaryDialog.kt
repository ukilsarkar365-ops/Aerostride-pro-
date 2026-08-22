package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RunSession
import com.example.ui.theme.*
import org.json.JSONArray

@Composable
fun RunSummaryDialog(
    session: RunSession,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    val distKm = session.totalDistanceMeters / 1000.0
    val distStr = String.format("%.2f", distKm)

    val min = session.durationSeconds / 60
    val sec = session.durationSeconds % 60
    val timeStr = String.format("%02d:%02d", min, sec)

    val paceMin = session.avgPaceSecondsPerKm / 60
    val paceSec = session.avgPaceSecondsPerKm % 60
    val paceStr = String.format("%d'%02d\"", paceMin, paceSec)

    // Parse splits json
    val splitList = mutableListOf<Triple<Int, Long, Double>>()
    try {
        val arr = JSONArray(session.splitsJson)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val num = obj.getInt("lapNumber")
            val dur = obj.getLong("lapDurationSeconds")
            val diff = obj.getDouble("diffSeconds")
            splitList.add(Triple(num, dur, diff))
        }
    } catch (e: Exception) {
        // Fallback
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy / Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (session.isCompletedTarget) Color(0xFF003814) else Color(0xFF1E2436)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (session.isCompletedTarget) Icons.Default.EmojiEvents else Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = if (session.isCompletedTarget) NeonYellow else NeonGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (session.isCompletedTarget) {
                        if (isBengali) "টার্গেট সম্পন্ন হয়েছে!" else "TARGET ACHIEVED!"
                    } else {
                        if (isBengali) "ওয়ার্কআউট সম্পন্ন!" else "WORKOUT COMPLETED!"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (session.isCompletedTarget) NeonYellow else NeonGreen
                )

                Text(
                    text = if (isBengali) "আপনার দৌড়ের রেকর্ড ডাটাবেসে সেভ হয়েছে" else "Run record saved to database",
                    fontSize = 11.sp,
                    color = MutedText,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Stats Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBg)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryMetric(label = if (isBengali) "দূরত্ব" else "DISTANCE", value = "$distStr km", color = NeonGreen)
                    SummaryMetric(label = if (isBengali) "সময়" else "TIME", value = timeStr, color = Color.White)
                    SummaryMetric(label = if (isBengali) "গড় পেস" else "AVG PACE", value = paceStr, color = NeonCyan)
                    SummaryMetric(label = if (isBengali) "ক্যালোরি" else "KCAL", value = "${session.caloriesBurned}", color = NeonYellow)
                }

                // Splits list if available
                if (splitList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isBengali) "ল্যাপ স্প্লিট বিস্তারিত" else "LAP SPLITS BREAKDOWN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F121C))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(splitList) { (num, dur, diff) ->
                            val diffStr = String.format("%.1f", Math.abs(diff))
                            val isFast = diff <= 0
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lap $num (${session.trackSizeMeters.toInt()}m)",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${dur}s",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isFast) "-${diffStr}s Fast" else "+${diffStr}s Slow",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFast) NeonGreen else NeonRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = if (isBengali) "ঠিক আছে (DONE)" else "DONE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MutedText)
    }
}
