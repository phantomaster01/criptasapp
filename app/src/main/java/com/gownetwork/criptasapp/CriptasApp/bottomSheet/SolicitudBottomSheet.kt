package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.CriptasRepository
import com.gownetwork.criptasapp.viewmodel.CriptasViewModel
import mx.com.gownetwork.criptas.databinding.BottomsheetSolicitudBinding

class SolicitudBottomSheet(private val previewMensaje: String, private val idServicio: String, val title :String, ) :
    BottomSheetDialogFragment() {

    private var _binding: BottomsheetSolicitudBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CriptasViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetSolicitudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = CriptasViewModel(CriptasRepository(ApiClient.service), requireContext())
        binding.title.text = title
        binding.btnEnviarSolicitud.setOnClickListener {
            val mensaje = previewMensaje.plus(": ").plus(binding.etMensaje.text.toString())
            if (mensaje.isNotEmpty()) {
                viewModel.enviarSolicitud(idServicio, mensaje.plus(mensaje))
            } else {
                Toast.makeText(requireContext(), "Escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.solicitudEnviada.observe(viewLifecycleOwner) { enviada ->
            if (enviada) {
                Toast.makeText(requireContext(), "Solicitud enviada con éxito", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}