package com.tuapp.ejemplo // Reemplaza con el nombre de tu paquete

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asegúrate de que el XML es correcto

        val textView = findViewById<TextView>(R.id.miTextView)

        textView.post {
            val paint = textView.paint
            val width = textView.width.toFloat()

            val shader = LinearGradient(
                0f, 0f, width, 0f, // Degradado horizontal
                intArrayOf(
                    resources.getColor(R.color.colorMorado, null),
                    resources.getColor(R.color.colorRosaFuerte, null)
                ),
                null,
                Shader.TileMode.CLAMP
            )

            paint.shader = shader
        }
    }
}
