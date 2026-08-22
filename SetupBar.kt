package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CardBg
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MutedText
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonYellow
import com.example.ui.tracker.RaceTargetPreset
import com.example.ui.tracker.TrackSizePreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupBar(
    racePresets: List<RaceTargetPreset>,
    trackPresets: List<TrackSizePreset>,
    selectedRaceIdx: Int,
    selectedTrackIdx: Int,
    isBengali: Boolean,
    onSelectRace: (Int) -> Unit,
    onSelectTrack: (Int) -> Unit,
    onOpenCustomDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var raceMenuExpanded by remember { mutableStateOf(false) }
    var trackMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardBg,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Race Target Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "রেস টার্গেট" else "RACE TARGET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBg)
                                .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                                .clickable { raceMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val target = racePresets.getOrNull(selectedRaceIdx)
                            Text(
                                text = if (isBengali) target?.titleBn ?: "১৬০০ মিটার" else target?.titleEn ?: "1600m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = raceMenuExpanded,
                            onDismissRequest = { raceMenuExpanded = false },
                            modifier = Modifier.background(CardBg)
                        ) {
                            racePresets.forEachIndexed { idx, preset ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (isBengali) preset.titleBn else preset.titleEn,
                                            fontSize = 13.sp,
                                            fontWeight = if (idx == selectedRaceIdx) FontWeight.Bold else FontWeight.Normal,
                                            color = if (idx == selectedRaceIdx) NeonGreen else Color.White
                                        )
                                    },
                                    onClick = {
                                        onSelectRace(idx)
                                        raceMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Track Size Selector
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "গ্রাউন্ড ট্র্যাক" else "GROUND TRACK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText,
                            letterSpacing = 0.5.sp
                        )
                        IconButton(
                            onClick = onOpenCustomDialog,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Custom Settings",
                                tint = NeonYellow,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBg)
                                .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                                .clickable { trackMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val track = trackPresets.getOrNull(selectedTrackIdx)
                            Text(
                                text = if (isBengali) track?.titleBn ?: "৪০০ মিটার" else track?.titleEn ?: "400m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = trackMenuExpanded,
                            onDismissRequest = { trackMenuExpanded = false },
                            modifier = Modifier.background(CardBg)
                        ) {
                            trackPresets.forEachIndexed { idx, preset ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (isBengali) preset.titleBn else preset.titleEn,
                                            fontSize = 13.sp,
                                            fontWeight = if (idx == selectedTrackIdx) FontWeight.Bold else FontWeight.Normal,
                                            color = if (idx == selectedTrackIdx) NeonGreen else Color.White
                                        )
                                    },
                                    onClick = {
                                        onSelectTrack(idx)
                                        trackMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
