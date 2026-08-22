package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdBanner
import com.example.ui.components.HudDashboard
import com.example.ui.components.SetupBar
import com.example.ui.components.TrackMapView
import com.example.ui.dialogs.CustomTargetDialog
import com.example.ui.dialogs.RunSummaryDialog
import com.example.ui.history.HistorySheet
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.tracker.RunStatus
import com.example.ui.tracker.TrackerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val pastRuns by viewModel.pastRuns.collectAsStateWithLifecycle()
                val context = LocalContext.current

                // Keep screen on during active workouts
                DisposableEffect(uiState.status) {
                    if (uiState.status == RunStatus.RUNNING) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    onDispose {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                // Permission launcher
                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                    val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

                    if (fineLocationGranted || coarseLocationGranted) {
                        viewModel.startRun()
                    } else {
                        // Permission denied, auto-fallback to high precision simulator
                        viewModel.toggleSimulationMode()
                        viewModel.startRun()
                    }
                }

                val onStartRunClick = {
                    val hasFine = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    val hasCoarse = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFine || hasCoarse || uiState.isSimulationMode) {
                        viewModel.startRun()
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBg,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(DarkBg)
                    ) {
                        // Top Ad / Status Bar
                        AdBanner(
                            isSimulationMode = uiState.isSimulationMode,
                            isGpsLocked = uiState.isGpsLocked,
                            isVoiceEnabled = uiState.isVoiceEnabled,
                            onToggleVoice = { viewModel.toggleVoice() },
                            onToggleSim = { viewModel.toggleSimulationMode() }
                        )

                        // Setup Bar (Disabled during active run)
                        SetupBar(
                            racePresets = viewModel.racePresets,
                            trackPresets = viewModel.trackPresets,
                            selectedRaceIdx = uiState.selectedRaceTargetIndex,
                            selectedTrackIdx = uiState.selectedTrackSizeIndex,
                            isBengali = uiState.isBengali,
                            onSelectRace = { viewModel.selectRacePreset(it) },
                            onSelectTrack = { viewModel.selectTrackPreset(it) },
                            onOpenCustomDialog = { viewModel.openCustomTargetDialog() }
                        )

                        // Dynamic Radar Map View
                        TrackMapView(
                            pathPoints = uiState.pathPoints,
                            currentSpeedKmh = uiState.currentSpeedKmh,
                            isTracking = uiState.status == RunStatus.RUNNING,
                            isSimulating = uiState.isSimulationMode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        // HUD Dashboard Panel
                        HudDashboard(
                            status = uiState.status,
                            distanceMeters = uiState.distanceMeters,
                            elapsedSeconds = uiState.elapsedSeconds,
                            currentSpeedKmh = uiState.currentSpeedKmh,
                            paceSecondsPerKm = uiState.paceSecondsPerKm,
                            caloriesBurned = uiState.caloriesBurned,
                            currentLap = uiState.currentLap,
                            totalLaps = uiState.totalLaps,
                            targetLapSeconds = uiState.targetLapSeconds,
                            lapSplits = uiState.lapSplits,
                            isBengali = uiState.isBengali,
                            onStartRun = onStartRunClick,
                            onPauseRun = { viewModel.pauseRun() },
                            onResumeRun = { viewModel.resumeRun() },
                            onStopRun = { viewModel.stopRun() },
                            onOpenHistory = { viewModel.openHistory() },
                            onToggleLanguage = { viewModel.toggleLanguage() }
                        )
                    }

                    // Run Completion Dialog
                    if (uiState.showCompletionDialog && uiState.lastCompletedSession != null) {
                        RunSummaryDialog(
                            session = uiState.lastCompletedSession!!,
                            isBengali = uiState.isBengali,
                            onDismiss = { viewModel.dismissCompletionDialog() }
                        )
                    }

                    // Custom Target Setup Dialog
                    if (uiState.showCustomTargetDialog) {
                        CustomTargetDialog(
                            initialRaceMeters = uiState.raceMeters,
                            initialTrackMeters = uiState.trackMeters,
                            initialTargetSeconds = uiState.targetTotalSeconds,
                            isBengali = uiState.isBengali,
                            onSave = { meters, trackMeters, targetSec ->
                                viewModel.setCustomTarget(meters, trackMeters, targetSec)
                                viewModel.closeCustomTargetDialog()
                            },
                            onDismiss = { viewModel.closeCustomTargetDialog() }
                        )
                    }

                    // History Bottom Sheet
                    if (uiState.showHistorySheet) {
                        HistorySheet(
                            pastRuns = pastRuns,
                            isBengali = uiState.isBengali,
                            onDeleteRun = { viewModel.deletePastRun(it) },
                            onClearAll = { viewModel.clearAllHistory() },
                            onDismiss = { viewModel.closeHistory() }
                        )
                    }
                }
            }
        }
    }
}
