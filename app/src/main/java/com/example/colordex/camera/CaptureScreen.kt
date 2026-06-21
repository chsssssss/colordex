package com.example.colordex.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.colordex.core.color.RgbColor

// camera/CaptureScreen.kt
@Composable
fun CaptureScreen() {
    val permissionState = rememberCameraPermissionState()
    var liveColor by remember { mutableStateOf(RgbColor(0, 0, 0)) }

    LaunchedEffect(Unit) {
        if (!permissionState.hasPermission) permissionState.requestPermission()
    }

    if (!permissionState.hasPermission) {
        PermissionRequiredContent(onRequestClick = permissionState.requestPermission)
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewScreen(
            modifier = Modifier.fillMaxSize(),
            onColorSampled = { liveColor = it },
        )

        // 중앙 조준점 — 실제 샘플링 영역을 시각적으로 표시
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp)
                .border(2.dp, Color.White, CircleShape)
        )

        // 하단 색 표시 패널
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(liveColor.r, liveColor.g, liveColor.b),
                        CircleShape
                    )
            )
            Spacer(Modifier.width(12.dp))
            Text(liveColor.toHex(), color = Color.White)
        }
    }
}