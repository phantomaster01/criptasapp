package com.gownetwork.criptasapp.CriptasApp.activitys

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gownetwork.criptasapp.CriptasApp.adapters.CriptasByIglesiaAdapter
import com.gownetwork.criptasapp.CriptasApp.bottomSheet.SolicitarApartarBottomSheet
import com.gownetwork.criptasapp.CriptasApp.bottomSheet.SolicitudBottomSheet
import com.gownetwork.criptasapp.CriptasApp.extensions.IdServicioCriptas
import com.gownetwork.criptasapp.CriptasApp.extensions.setupFullScreen
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.CriptasRepository
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import com.gownetwork.criptasapp.viewmodel.CriptasViewModel
import com.gownetwork.criptasapp.viewmodel.GenericViewModelFactory
import mx.com.gownetwork.criptas.R
import mx.com.gownetwork.criptas.databinding.ActivityCriptasDisponiblesBinding

class CriptasDisponiblesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriptasDisponiblesBinding
    private val viewModel: CriptasViewModel by viewModels {
        GenericViewModelFactory(CriptasViewModel::class.java) {
            CriptasViewModel(CriptasRepository(ApiClient.service), this@CriptasDisponiblesActivity)
        }
    }
    private lateinit var sharedPreferences: SharedPreferences
    private var idIglesia: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriptasDisponiblesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFullScreen(false)
        loadId()
        initView()
        initObservers()
    }

    private fun loadId() {
        sharedPreferences = getSharedPreferences("criptas_prefs", Context.MODE_PRIVATE)
        idIglesia = intent.getStringExtra("ID_IGLESIA") ?: sharedPreferences.getString("ID_IGLESIA", null)
        if (idIglesia == null) {
            Toast.makeText(this, "No se encontró el ID de la iglesia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        sharedPreferences.edit().putString("ID_IGLESIA", idIglesia).apply()
    }

    private fun initView() = with(binding) {
        setSupportActionBar(toolbarServicios)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Criptas Disponibles"

        toolbarServicios.setNavigationOnClickListener {
            finish()
        }

        recyclerServicios.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun initObservers() {
        viewModel.criptasDisponibles.observe(this) { criptas ->
            binding.recyclerServicios.adapter = CriptasByIglesiaAdapter(criptas, ::onSolicitarClick)
            if(criptas.size > 0)
                supportActionBar?.title = criptas[0].iglesia
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }

        idIglesia?.let {
            viewModel.fetchCriptasDisponibles(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_update, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                idIglesia?.let {
                    viewModel.fetchCriptasDisponibles(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        idIglesia = sharedPreferences.getString("ID_IGLESIA", null)
        idIglesia?.let {
            viewModel.fetchCriptasDisponibles(it)
        }
    }
    private fun onSolicitarClick(cripta: CriptasByIglesia) {
        val bottomSheet = SolicitarApartarBottomSheet(cripta)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

}
