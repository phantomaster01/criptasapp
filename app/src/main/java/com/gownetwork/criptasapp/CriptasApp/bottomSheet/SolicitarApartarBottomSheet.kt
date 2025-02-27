package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import mx.com.gownetwork.criptas.databinding.BottomsheetSolicitarApartarBinding

class SolicitarApartarBottomSheet(private val cripta: CriptasByIglesia, private val fetch: () -> Unit) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetSolicitarApartarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetSolicitarApartarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSolicitarInfo.setOnClickListener {
            dismiss()
            val solicitudBottomSheet = SolicitudBottomSheet(
                previewMensaje = "Información de cripta",
                idServicio = cripta.id,
                title = "Solicitar Información"
            )
            solicitudBottomSheet.show(parentFragmentManager, solicitudBottomSheet.tag)
        }

        binding.btnApartarCripta.setOnClickListener {
            dismiss()
            val detalleBottomSheet = CriptaDetalleBottomSheet(cripta,fetch)
            detalleBottomSheet.show(parentFragmentManager, detalleBottomSheet.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
