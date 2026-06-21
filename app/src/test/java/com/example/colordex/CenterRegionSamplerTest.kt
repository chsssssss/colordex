package com.example.colordex

import com.example.colordex.core.color.sampleCenterRegionAverageRgb
import kotlin.test.Test
import kotlin.test.assertEquals

class CenterRegionSamplerTest {

    @Test
    fun `균일한 회색 영역은 그 값 그대로 평균된다`() {
        val width = 16; val height = 16
        val yPlane = ByteArray(width * height) { 128.toByte() } // 회색
        val uPlane = ByteArray((width / 2) * (height / 2)) { 128.toByte() } // 무채색 U
        val vPlane = ByteArray((width / 2) * (height / 2)) { 128.toByte() } // 무채색 V

        val result = sampleCenterRegionAverageRgb(
            yPlane = yPlane, yRowStride = width, yPixelStride = 1,
            uPlane = uPlane, uRowStride = width / 2, uPixelStride = 1,
            vPlane = vPlane, vRowStride = width / 2, vPixelStride = 1,
            imageWidth = width, imageHeight = height,
            regionSize = 9,
        )

        // Y=128, U=V=128 → uVal=vVal=0 → BT.601 계산 결과가 정확히 128
        assertEquals(128, result.r)
        assertEquals(128, result.g)
        assertEquals(128, result.b)
    }
}
