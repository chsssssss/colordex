package com.example.colordex.core.color

// Android 타입에 의존하지 않는 순수 함수로 분리 — JVM 단위테스트 가능.

/**
 * YUV420 평면 데이터에서 (centerX, centerY) 기준 regionSize x regionSize
 * 영역의 평균 RGB를 계산한다.
 * 단일 픽셀이 아니라 영역 평균을 쓰는 이유: 센서 노이즈·손떨림으로 인한
 * 픽셀 단위 색 튐을 평균으로 완화한다.
 */
fun sampleCenterRegionAverageRgb(
    yPlane: ByteArray, yRowStride: Int, yPixelStride: Int,
    uPlane: ByteArray, uRowStride: Int, uPixelStride: Int,
    vPlane: ByteArray, vRowStride: Int, vPixelStride: Int,
    imageWidth: Int, imageHeight: Int,
    regionSize: Int = 9,
): RgbColor {
    val half = regionSize / 2
    val cx = imageWidth / 2
    val cy = imageHeight / 2

    var rSum = 0L; var gSum = 0L; var bSum = 0L
    var count = 0

    for (dy in -half..half) {
        val py = cy + dy
        if (py < 0 || py >= imageHeight) continue
        for (dx in -half..half) {
            val px = cx + dx
            if (px < 0 || px >= imageWidth) continue

            val yIndex = py * yRowStride + px * yPixelStride
            val uvRow = py / 2
            val uvCol = px / 2
            val uIndex = uvRow * uRowStride + uvCol * uPixelStride
            val vIndex = uvRow * vRowStride + uvCol * vPixelStride

            val yVal = (yPlane[yIndex].toInt() and 0xFF)
            val uVal = (uPlane[uIndex].toInt() and 0xFF) - 128
            val vVal = (vPlane[vIndex].toInt() and 0xFF) - 128

            // BT.601 YUV -> RGB
            val r = (yVal + 1.402 * vVal).toInt().coerceIn(0, 255)
            val g = (yVal - 0.344136 * uVal - 0.714136 * vVal).toInt().coerceIn(0, 255)
            val b = (yVal + 1.772 * uVal).toInt().coerceIn(0, 255)

            rSum += r; gSum += g; bSum += b
            count++
        }
    }

    if (count == 0) return RgbColor(0, 0, 0)
    return RgbColor(
        r = (rSum / count).toInt(),
        g = (gSum / count).toInt(),
        b = (bSum / count).toInt(),
    )
}

data class RgbColor(val r: Int, val g: Int, val b: Int) {
    fun toHex(): String = "#%02X%02X%02X".format(r, g, b)
}