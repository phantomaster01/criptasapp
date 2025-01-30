package com.gownetwork.ctiptasapp.CriptasApp

import android.R
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gownetwork.ctiptasapp.databinding.ActivityCriptasRegisterBinding
import com.gownetwork.ctiptasapp.viewmodel.AuthViewModel
import com.gownetwork.ctiptasapp.viewmodel.AuthViewModelFactory
import java.util.Calendar

class CriptasRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriptasRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriptasRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(applicationContext))[AuthViewModel::class.java]
        val sexoOptions = listOf("Hombre", "Mujer")
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, sexoOptions)

        binding.editTextSexo.setAdapter(adapter)
        binding.editTextSexo.setOnItemClickListener { _, _, position, _ ->
            val selectedSex = sexoOptions[position]
            Log.d("SEXO_SELECCIONADO", "Seleccionado: $selectedSex")
        }
        binding.editTextFechaNac.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.editTextFechaNac.setText(formattedDate)
                }, year, month, day
            )

            datePickerDialog.datePicker.firstDayOfWeek = Calendar.MONDAY // Opcional: establecer el primer día
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Estilo
            datePickerDialog.show()
        }

        // Observa la carga y errores
        authViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressIndicator.visibility = View.VISIBLE
                binding.btnRegisterSubmit.visibility = View.GONE
            } else {
                binding.progressIndicator.visibility = View.GONE
                binding.btnRegisterSubmit.visibility = View.VISIBLE
            }
        }

        authViewModel.registerMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        authViewModel.registerSuccess.observe(this) { result ->
            if(result){
                finish()
            }
        }

        // Botón para registrar usuario
        binding.btnRegisterSubmit.setOnClickListener {
            val nombres = binding.editTextNombre.text.toString().trim()
            val apellidos = binding.editTextApellido.text.toString().trim()
            val direccion = binding.editTextDireccion.text.toString().trim()
            val telefono = binding.editTextTelefono.text.toString().trim()
            val fechaNac = binding.editTextFechaNac.text.toString().trim()
            val sexo = binding.editTextSexo.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            // Validaciones
            if (nombres.isEmpty() || apellidos.isEmpty() || direccion.isEmpty() ||
                telefono.isEmpty() || fechaNac.isEmpty() || sexo.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Registrar usuario en el ViewModel
            authViewModel.register(nombres, apellidos, direccion, telefono, password, email, fechaNac, sexo)
        }



        // Evento de botón para regresar al login
        binding.btnBackToLogin.setOnClickListener {
            finish() // Cierra la actividad y regresa al login
        }
    }
}
