package com.example.colordex.camera

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.colordex.core.color.RgbColor
import java.util.concurrent.Executors

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    onColorSampled: (RgbColor) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val analysis = ImageAnalysis.Builder()
                    // STRATEGY_KEEP_ONLY_LATEST: 분석이 느려도 큐가 안 쌓이고
                    // 항상 최신 프레임만 처리 — 실시간 표시엔 이게 맞다.
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    LiveColorAnalyzer(onColorSampled = onColorSampled)
                )

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis,
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "bind 실패", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
    )
}