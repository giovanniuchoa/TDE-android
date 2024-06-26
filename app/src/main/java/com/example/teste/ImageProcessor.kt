package com.example.teste

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

object ImageProcessor {

    fun applyGrayscale(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = originalBitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val grayscaleValue = (red + green + blue) / 3
                val grayscalePixel = Color.rgb(grayscaleValue, grayscaleValue, grayscaleValue)
                grayscaleBitmap.setPixel(x, y, grayscalePixel)
            }
        }

        return grayscaleBitmap
    }

    fun applyNegative(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        val negativeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = originalBitmap.getPixel(x, y)
                val red = 255 - Color.red(pixel)
                val green = 255 - Color.green(pixel)
                val blue = 255 - Color.blue(pixel)
                val negativePixel = Color.rgb(red, green, blue)
                negativeBitmap.setPixel(x, y, negativePixel)
            }
        }

        return negativeBitmap
    }


    fun applySepia(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        val sepiaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val sepiaDepth = 20 // Adjust the depth of sepia effect as needed

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = originalBitmap.getPixel(x, y)
                val originalRed = Color.red(pixel)
                val originalGreen = Color.green(pixel)
                val originalBlue = Color.blue(pixel)

                val sepiaRed = (originalRed * 0.393 + originalGreen * 0.769 + originalBlue * 0.189).coerceAtMost(255.0)
                val sepiaGreen = (originalRed * 0.349 + originalGreen * 0.686 + originalBlue * 0.168).coerceAtMost(255.0)
                val sepiaBlue = (originalRed * 0.272 + originalGreen * 0.534 + originalBlue * 0.131).coerceAtMost(255.0)

                val newRed = (sepiaRed + sepiaDepth).coerceAtMost(255.0).toInt()
                val newGreen = (sepiaGreen + sepiaDepth).coerceAtMost(255.0).toInt()
                val newBlue = (sepiaBlue + sepiaDepth).coerceAtMost(255.0).toInt()

                val sepiaPixel = Color.rgb(newRed, newGreen, newBlue)
                sepiaBitmap.setPixel(x, y, sepiaPixel)
            }
        }

        return sepiaBitmap
    }

    private const val SMOOTHNESS_FACTOR = 0.005f

    fun adjustBrightness(bitmap: Bitmap, brightness: Float): Bitmap {
        val smoothedBrightness = 1 + (brightness - 1) * SMOOTHNESS_FACTOR

        val colorMatrix = ColorMatrix().apply {
            setScale(smoothedBrightness, smoothedBrightness, smoothedBrightness, 1f)
        }

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }

    fun adjustContrast(bitmap: Bitmap, contrast: Float): Bitmap {
        val smoothedContrast = 1 + (contrast - 1) * SMOOTHNESS_FACTOR

        val colorMatrix = ColorMatrix().apply {
            val scale = smoothedContrast
            val translate = (1 - scale) / 2
            set(floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
        }

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }


    fun detectEdges(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val grayBitmap = applyGrayscale(bitmap)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val gx = (
                        -grayBitmap.getPixel(x - 1, y - 1) - 2 * grayBitmap.getPixel(x - 1, y) - grayBitmap.getPixel(
                            x - 1,
                            y + 1
                        ) + grayBitmap.getPixel(x + 1, y - 1) + 2 * grayBitmap.getPixel(x + 1, y) + grayBitmap.getPixel(
                            x + 1,
                            y + 1
                        )
                        )
                val gy = (
                        -grayBitmap.getPixel(x - 1, y - 1) - 2 * grayBitmap.getPixel(x, y - 1) - grayBitmap.getPixel(
                            x + 1,
                            y - 1
                        ) + grayBitmap.getPixel(x - 1, y + 1) + 2 * grayBitmap.getPixel(x, y + 1) + grayBitmap.getPixel(
                            x + 1,
                            y + 1
                        )
                        )
                val gradientMagnitude = Math.sqrt((gx * gx + gy * gy).toDouble()).toInt()
                val pixel = Color.rgb(gradientMagnitude, gradientMagnitude, gradientMagnitude)
                resultBitmap.setPixel(x, y, pixel)
            }
        }
        return resultBitmap
    }



}