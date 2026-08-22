package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.components.HudDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.tracker.RunStatus
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        HudDashboard(
          status = RunStatus.IDLE,
          distanceMeters = 0.0,
          elapsedSeconds = 0,
          currentSpeedKmh = 0.0,
          paceSecondsPerKm = 0,
          caloriesBurned = 0,
          currentLap = 1,
          totalLaps = 4,
          targetLapSeconds = 82.5,
          lapSplits = emptyList(),
          isBengali = true,
          onStartRun = {},
          onPauseRun = {},
          onResumeRun = {},
          onStopRun = {},
          onOpenHistory = {},
          onToggleLanguage = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
