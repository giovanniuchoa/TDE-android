package com.example.teste

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

        val seekBarBrightness = findViewById<SeekBar>(R.id.seekBarBrightness)
        val textViewBrightnessValue = findViewById<TextView>(R.id.textViewBrightnessValue)
        val seekBarContrast = findViewById<SeekBar>(R.id.seekBarContrast)
        val textViewContrastValue = findViewById<TextView>(R.id.textViewContrastValue)


        seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val brightness = progress - 100 // Ajusta a faixa de -100 a 100
                val adjustedBitmap = ImageProcessor.adjustBrightness(originalBitmap, brightness.toFloat())

                imageView.setImageBitmap(adjustedBitmap)
                textViewBrightnessValue.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val contrast = progress - 100 // Ajusta a faixa de -100 a 100
                val adjustedBitmap = ImageProcessor.adjustContrast(originalBitmap, contrast.toFloat())

                imageView.setImageBitmap(adjustedBitmap)
                textViewContrastValue.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })



        findViewById<Button>(R.id.buttonEdgeDetection).setOnClickListener {
            val edgeDetectedBitmap = ImageProcessor.detectEdges(originalBitmap)
            imageView.setImageBitmap(edgeDetectedBitmap)
        }


        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            saveImage()
        }
        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            goToMainActivity()
        }

    }


    fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Opcional, se você quiser fechar esta activity após ir para a MainActivity
    }

    private fun saveImage() {
        // Verifique se a imagem está disponível
        if (imageView.drawable == null) {
            Toast.makeText(this, "Não há imagem para salvar", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtenha o bitmap editado
        val editedBitmap = (imageView.drawable).toBitmap()

        // Adicione a imagem à galeria usando o MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "edited_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, editedBitmap.width)
            put(MediaStore.Images.Media.HEIGHT, editedBitmap.height)
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    editedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                Toast.makeText(this, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Falha ao salvar a imagem", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao salvar a imagem", Toast.LENGTH_SHORT).show()
        }
    }
}
