package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToLogin
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToRegister
import mx.com.gownetwork.criptas.databinding.BottomsheetSolicitarSesionBinding

class SolicitarSessionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetSolicitarSesionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSolicitarSesionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí agregas los eventos de los botones
        binding.btnLogin.setOnClickListener {
            (requireActivity() as AppCompatActivity).navigateToLogin()
        }

        binding.btnRegister.setOnClickListener {
            (requireActivity() as AppCompatActivity).navigateToRegister()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
