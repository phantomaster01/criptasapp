package com.gownetwork.ctiptasapp.CriptasApp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gownetwork.criptasapp.CriptasApp.CriptasDisponiblesActivity
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.entities.Iglesia
import com.gownetwork.criptasapp.viewmodel.AuthViewModel
import com.gownetwork.ctiptasapp.R
import com.gownetwork.ctiptasapp.databinding.FragmentMapsBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MapsFragment :  Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var authViewModel: AuthViewModel
    private val iglesiasList = mutableListOf<Iglesia>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflamos el layout usando View Binding
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el fragmento del mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicializar ViewModel para obtener el token
        authViewModel = AuthViewModel(requireContext())
        with(binding){
            btnRefresh.setOnClickListener{
                fetchIglesias()
            }
        }
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            with(binding){
                if (isLoading) {
                    progress.visibility = View.VISIBLE
                    btnRefresh.visibility = View.GONE
                } else {
                    progress.visibility = View.GONE
                    btnRefresh.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun fetchIglesias() {
        authViewModel.changeLoanding(true)
        val token = authViewModel.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No hay token disponible", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getIglesias("Bearer $token")
                if (response.HttpCode == 200) {
                    iglesiasList.clear()
                    iglesiasList.addAll(response.Result)
                    drawMarkers()
                } else {
                    Toast.makeText(requireContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show()
                }
                authViewModel.changeLoanding(false)
            } catch (e: HttpException) {
                Log.e("MapsFragment", "Error HTTP: ${e.response()?.code()}")
                Toast.makeText(requireContext(), "Error en la consulta", Toast.LENGTH_SHORT).show()
                authViewModel.changeLoanding(false)
            } catch (e: Exception) {
                Log.e("MapsFragment", "Error de conexión: ${e.message}")
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                authViewModel.changeLoanding(false)
            }
        }
    }

    private fun drawMarkers() {
        if (!this::googleMap.isInitialized) return

        googleMap.clear() // Limpiar el mapa antes de agregar nuevos marcadores

        for (iglesia in iglesiasList) {
            val position = LatLng(iglesia.Latitud, iglesia.Longitud)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(iglesia.Nombre)
                    .snippet(iglesia.Direccion)
            )
        }

        // Configurar clic en los marcadores
        googleMap.setOnMarkerClickListener { marker ->
            showBottomSheet(marker)
            false
        }
    }

    private fun showBottomSheet(marker: Marker) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_options, null)
        bottomSheetDialog.setContentView(view)

        // Obtener referencias a los TextView
        val tvTitle = view.findViewById<TextView>(R.id.tvMarkerTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvMarkerDescription)

        // Establecer datos del marcador
        tvTitle.text = marker.title ?: "Sin nombre"
        tvDescription.text = marker.snippet ?: "Sin descripción"

        // Botones
        val btnNavigate = view.findViewById<Button>(R.id.btnNavigate)
        val btnViewCriptas = view.findViewById<Button>(R.id.btnViewCriptas)

        btnNavigate.setOnClickListener {
            openGoogleMaps(marker.position)
            bottomSheetDialog.dismiss()
        }

        btnViewCriptas.setOnClickListener {
            openCriptasScreen()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun openGoogleMaps(latLng: LatLng) {
        val uri = "google.navigation:q=${latLng.latitude},${latLng.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCriptasScreen() {
        val intent = Intent(requireContext(), CriptasDisponiblesActivity::class.java)
        startActivity(intent)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            moveToUserLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        // Obtener datos de la API
        fetchIglesias()
    }

    private fun moveToUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
            } else {
                Log.e("MapsFragment", "Ubicación del usuario no disponible")
            }
        }.addOnFailureListener { e ->
            Log.e("MapsFragment", "Error obteniendo ubicación: ${e.message}")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, habilitar ubicación
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    try {
                        googleMap.isMyLocationEnabled = true
                        moveToUserLocation()
                    } catch (e: SecurityException) {
                        Log.e("MapsFragment", "No se puede habilitar la ubicación: ${e.message}")
                    }
                }
            } else {
                // Permiso denegado, mostrar mensaje al usuario
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita memory leaks
    }
}