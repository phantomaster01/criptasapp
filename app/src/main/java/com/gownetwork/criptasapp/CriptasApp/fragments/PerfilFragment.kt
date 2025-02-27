package com.gownetwork.criptasapp.CriptasApp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToLogin
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToRegister
import com.gownetwork.criptasapp.CriptasApp.extensions.setTitle
import mx.com.gownetwork.criptas.databinding.FragmentMiPerfilBinding
import com.gownetwork.criptasapp.network.entities.formatFecha
import com.gownetwork.criptasapp.viewmodel.AuthViewModel
import com.gownetwork.criptasapp.viewmodel.AuthViewModelFactory

class PerfilFragment : Fragment() {

    private var _binding: FragmentMiPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiPerfilBinding.inflate(inflater, container, false)
        setTitle("Mi Perfil")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(requireContext()))
            .get(AuthViewModel::class.java)
        with(binding){
            var gone = authViewModel.getToken() == null
            layoutNoRegistrado.isGone = !gone
            layoutRegistrado.isGone = gone
            if(gone)
                initViewsGo()
            else
                initViews()
        }

    }

    private fun initViewsGo()=with(binding){
        btnIniciarSesion.setOnClickListener{
            requireActivity().navigateToLogin()
        }
        btnRegistrarse.setOnClickListener{
            requireActivity().navigateToRegister()
        }
    }

    private fun initViews()=with(binding){
        // Observar los datos del perfil
        authViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            txtNombre.text = "${user.Nombres} ${user.Apellidos}"
            txtEmail.text = user.Email
            txtTelefono.text = user.Telefono
            txtDireccion.text = user.Direccion
            txtFechaNac.text = user.FechaNac
            txtEdad.text = user.Edad.toString()
            txtSexo.text = user.Sexo
            txtFechaRegistro.text = formatFecha(user.FechaRegistro)
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressIndicator.visibility = View.VISIBLE
                scroll.visibility = View.GONE
            } else {
                progressIndicator.visibility = View.GONE
                scroll.visibility = View.VISIBLE
            }
        }

        // Observar errores
        authViewModel.profileError.observe(viewLifecycleOwner) { errorMessage ->
            txtNombre.text = errorMessage
        }

        // Llamar a la API para obtener datos del perfil
        authViewModel.fetchUserProfile()

        // Cerrar sesión
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            requireActivity().navigateToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
