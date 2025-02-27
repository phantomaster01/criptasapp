package com.gownetwork.criptasapp.CriptasApp.activitys

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gownetwork.criptasapp.CriptasApp.adapters.PagosAdapter
import com.gownetwork.criptasapp.CriptasApp.bottomSheet.EvidenciaPagoBottomSheet
import com.gownetwork.criptasapp.CriptasApp.extensions.setupFullScreen
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.PagosRepository
import com.gownetwork.criptasapp.network.entities.Pago
import com.gownetwork.criptasapp.viewmodel.PagosViewModel
import mx.com.gownetwork.criptas.databinding.ActivityMenuCriptaBinding

class MenuCriptaActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuCriptaBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pagosViewModel: PagosViewModel
    private lateinit var pagosAdapter: PagosAdapter
    private var IdCripta: String? = null
    private var mostrandoPagos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuCriptaBinding.inflate(layoutInflater)
        pagosViewModel = PagosViewModel(PagosRepository(ApiClient.service), this)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Opciones"
        requestPermissions()
        setupFullScreen(false)
        loadId()
        setupObservers()
        setupListeners()
        setupRecyclerView()
    }

    private fun loadId() {
        sharedPreferences = getSharedPreferences("criptas_prefs", Context.MODE_PRIVATE)
        IdCripta = intent.getStringExtra("IdCripta") ?: sharedPreferences.getString("IdCripta", null)
        if (IdCripta == null) {
            Toast.makeText(this, "No se encontró el ID de la cripta", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            sharedPreferences.edit().putString("IdCripta", IdCripta).apply()
        }
    }

    private fun setupObservers() {
        pagosViewModel.pagos.observe(this) { pagos ->
            binding.progressBar.visibility = View.GONE
            if (pagos.isNotEmpty()) {
                pagosAdapter.actualizarLista(pagos)
                mostrarPagos(true)
            } else {
                Toast.makeText(this, "No hay pagos disponibles", Toast.LENGTH_SHORT).show()
                mostrarPagos(false)
            }
        }

        pagosViewModel.error.observe(this) { error ->
            binding.progressBar.visibility = View.GONE
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                mostrarPagos(false)
            }
        }
    }

    private fun next(title: String){
        supportActionBar?.title = title

        binding.toolbarServicios.setNavigationOnClickListener {
            back()
        }
    }
    private fun back(){
        if(binding.contenido.isGone){
            mostrarPagos(false)
            supportActionBar?.title = "Opciones"
        }else{
            finish()
        }
    }

    private fun setupListeners() {
        binding.btnPagos.setOnClickListener {
            toggleVistaPagos()
        }
    }

    private fun setupRecyclerView() {
        pagosAdapter = PagosAdapter(
            mutableListOf(),
            onPagoClick = { pago,position ->
                val bottomSheet = EvidenciaPagoBottomSheet(pago)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                pagosAdapter.notifyItemChanged(position)
            },
            onEliminarClick = { pago,position ->
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Eliminar p|ago")
                    .setMessage("¿Estás seguro de que deseas eliminar este pago?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        // Aquí podrías agregar la lógica para eliminarlo en el backend si es necesario.
                        Toast.makeText(this, "Pago eliminado", Toast.LENGTH_SHORT).show()
                        pagosAdapter.notifyItemChanged(position)
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        pagosAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .show()
            }
        )
        binding.recyclerPagos.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerPagos.adapter = pagosAdapter
        val itemTouchHelper = pagosAdapter.getSwipeHandler(this)
        itemTouchHelper.attachToRecyclerView(binding.recyclerPagos)
    }

    private fun toggleVistaPagos() {
        if (mostrandoPagos) {
            mostrarPagos(false)
        } else {
            binding.progressBar.visibility = View.VISIBLE
            pagosViewModel.obtenerPagosCliente()
        }
    }

    private fun mostrarPagos(mostrar: Boolean) {
        mostrandoPagos = mostrar
        if (mostrar) {
            binding.contenido.visibility = View.GONE
            binding.adapterLoad.visibility = View.VISIBLE
            next("Pagos pendientes")
        } else {
            binding.contenido.visibility = View.VISIBLE
            binding.adapterLoad.visibility = View.GONE
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        when {
            Build.VERSION.SDK_INT in Build.VERSION_CODES.N..Build.VERSION_CODES.P -> {
                if (!hasPermission(Manifest.permission.CAMERA)) permissions.add(Manifest.permission.CAMERA)
                if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                if (!hasPermission(Manifest.permission.CAMERA)) permissions.add(Manifest.permission.CAMERA)
                if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (!hasPermission(Manifest.permission.CAMERA)) permissions.add(Manifest.permission.CAMERA)
                if (!hasPermission(Manifest.permission.READ_MEDIA_IMAGES)) permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        if (permissions.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions.toTypedArray(), 100)
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(applicationContext, "Permisos concedidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Permisos denegados", Toast.LENGTH_SHORT).show()
        }
    }

}
