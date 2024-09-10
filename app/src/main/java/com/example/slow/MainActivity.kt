package com.example.slow

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.core.view.doOnPreDraw
import com.example.slow.ui.theme.MySlowApplicationTheme

class MainActivity : ComponentActivity() {

  private var viewTapCount = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val viewCounter = findViewById<TextView>(R.id.view_button)
    viewCounter.setOnClickListener {
      viewTapCount++
      viewCounter.text = "View Tap Count: $viewTapCount"
      simulateHeavyMainThreadWorkflow()
    }

    findViewById<ComposeView>(R.id.compose_view).setContent {
      var composeTapCount by remember {
        mutableIntStateOf(0)
      }
      MySlowApplicationTheme {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .scrollable(rememberScrollState(), orientation = Vertical),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(onClick = {
            composeTapCount++
            simulateHeavyMainThreadWorkflow()
          }) {
            Text("Compose Tap Count: $composeTapCount")
          }
          Button(onClick = waitAfterNextFrame {
            composeTapCount++
            simulateHeavyMainThreadWorkflow()
          }) {
            Text("Better Compose Tap Count: $composeTapCount")
          }
        }
      }
    }
  }

  private fun simulateHeavyMainThreadWorkflow() {
    Thread.sleep(1000)
  }

  @Composable
  private fun waitAfterNextFrame(onClick: () -> Unit): () -> Unit {
    val composeView = LocalView.current
    return {
      composeView.doAfterNextFrame(onClick)
    }
  }

  private fun View.doAfterNextFrame(block: () -> Unit) {
    doOnPreDraw {
      handler.postAtFrontOfQueue(block)
    }
  }
}

