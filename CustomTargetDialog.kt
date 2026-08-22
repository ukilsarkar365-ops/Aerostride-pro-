package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun CustomTargetDialog(
    initialRaceMeters: Double,
    initialTrackMeters: Double,
    initialTargetSeconds: Long,
    isBengali: Boolean,
    onSave: (meters: Double, trackMeters: Double, targetSeconds: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var raceMetersText by remember { mutableStateOf(initialRaceMeters.toInt().toString()) }
    var trackMetersText by remember { mutableStateOf(initialTrackMeters.toInt().toString()) }
    var targetMinText by remember { mutableStateOf((initialTargetSeconds / 60).toString()) }
    var targetSecText by remember { mutableStateOf((initialTargetSeconds % 60).toString()) }

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
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isBengali) "কাস্টম টার্গেট ও ট্র্যাক সেটআপ" else "CUSTOM RACE & TRACK SETUP",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonYellow
                )
                Text(
                    text = if (isBengali) "আপনার সুবিধা অনুযায়ী দৌড়ের দূরত্ব ও টার্গেট টাইম সেট করুন" else "Set custom race distance, ground track size and target time",
                    fontSize = 11.sp,
                    color = MutedText,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Race Distance Input
                Text(
                    text = if (isBengali) "মোট দৌড়ের দূরত্ব (মিটার)" else "TOTAL RACE DISTANCE (METERS)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = raceMetersText,
                    onValueChange = { raceMetersText = it.filter { ch -> ch.isDigit() } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ground Track Size Input
                Text(
                    text = if (isBengali) "গ্রাউন্ড ট্র্যাক পরিধি (মিটার)" else "GROUND TRACK SIZE (METERS)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = trackMetersText,
                    onValueChange = { trackMetersText = it.filter { ch -> ch.isDigit() } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Target Time (Min & Sec)
                Text(
                    text = if (isBengali) "টার্গেট সমাপ্তির সময়" else "TARGET TIME (MIN : SEC)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = targetMinText,
                        onValueChange = { targetMinText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Minutes / মিনিট", fontSize = 10.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = targetSecText,
                        onValueChange = { targetSecText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Seconds / সেকেন্ড", fontSize = 10.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBengali) "বাতিল" else "CANCEL")
                    }

                    Button(
                        onClick = {
                            val raceM = raceMetersText.toDoubleOrNull() ?: 1600.0
                            val trackM = trackMetersText.toDoubleOrNull() ?: 400.0
                            val min = targetMinText.toLongOrNull() ?: 5L
                            val sec = targetSecText.toLongOrNull() ?: 30L
                            val totalSec = min * 60 + sec
                            onSave(raceM.coerceAtLeast(100.0), trackM.coerceAtLeast(50.0), totalSec.coerceAtLeast(10))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isBengali) "সংরক্ষণ করুন" else "APPLY",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
