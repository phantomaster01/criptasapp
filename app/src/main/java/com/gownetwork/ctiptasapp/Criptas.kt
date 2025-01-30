import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.gownetwork.ctiptasapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criptas_login2)

        // Obtener el LinearLayout
        val layout = findViewById<LinearLayout>(R.id.LoginActivity)

        // Crear el degradado en código
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, // Dirección del degradado
            intArrayOf(0x565656.toInt(), 0x63747E.toInt())
        )

        // Aplicar el degradado al fondo del LinearLayout

    }
}