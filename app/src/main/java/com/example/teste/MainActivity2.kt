package com.example.teste

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var originalBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        imageView = findViewById(R.id.imageView)

        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)

        originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        imageView.setImageBitmap(originalBitmap)

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonGrayscale).setOnClickListener {
            val grayscaleBitmap = ImageProcessor.applyGrayscale(originalBitmap)
            imageView.setImageBitmap(grayscaleBitmap)
        }

        findViewById<Button>(R.id.buttonNegative).setOnClickListener {
            val negativeBitmap = ImageProcessor.applyNegative(originalBitmap)
            imageView.setImageBitmap(negativeBitmap)
        }

        findViewById<Button>(R.id.buttonSepia).setOnClickListener {
            val sepiaBitmap = ImageProcessor.applySepia(originalBitmap)
            imageView.setImageBitmap(sepiaBitmap)
        }

        findViewById<Button>(R.id.buttonBrightnessContrast).setOnClickListener {
            val adjustedBitmap = ImageProcessor.adjustBrightnessContrast(originalBitmap, 0.5f, 1.5f)
            imageView.setImageBitmap(adjustedBitmap)
        }

        findViewById<Button>(R.id.buttonEdgeDetection).setOnClickListener {
            val edgeDetectedBitmap = ImageProcessor.detectEdges(originalBitmap)
            imageView.setImageBitmap(edgeDetectedBitmap)
        }
    }
}
