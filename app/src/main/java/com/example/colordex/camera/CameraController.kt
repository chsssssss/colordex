package com.example.colordex.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.colordex.core.color.RgbColor
import com.example.colordex.core.color.sampleCenterRegionAverageRgb
import java.nio.ByteBuffer

class LiveColorAnalyzer(
    private val onColorSampled: (RgbColor) -> Unit,
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        try {
            val yPlane = image.planes[0]
            val uPlane = image.planes[1]
            val vPlane = image.planes[2]

            val rgb = sampleCenterRegionAverageRgb(
                yPlane = yPlane.buffer.toByteArray(), yRowStride = yPlane.rowStride, yPixelStride = yPlane.pixelStride,
                uPlane = uPlane.buffer.toByteArray(), uRowStride = uPlane.rowStride, uPixelStride = uPlane.pixelStride,
                vPlane = vPlane.buffer.toByteArray(), vRowStride = vPlane.rowStride, vPixelStride = vPlane.pixelStride,
                imageWidth = image.width, imageHeight = image.height,
            )
            onColorSampled(rgb)
        } finally {
            image.close() // 반드시 close — 안 하면 다음 프레임이 안 들어옴
        }
    }
}

private fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val arr = ByteArray(remaining())
    get(arr)
    return arr
}