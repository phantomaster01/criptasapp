package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.CriptasApp.extensions.IdPUE
import com.gownetwork.criptasapp.CriptasApp.extensions.toPesos
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.PagosRepository
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import com.gownetwork.criptasapp.network.entities.PagoCreate
import com.gownetwork.criptasapp.viewmodel.PagosViewModel
import mx.com.gownetwork.criptas.databinding.BottomsheetDetalleCriptaBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CriptaDetalleBottomSheet(private val cripta: CriptasByIglesia) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetDetalleCriptaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PagosViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetDetalleCriptaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = PagosViewModel(PagosRepository(ApiClient.service), requireContext())
        with(binding){
            txtCripta.text = "Cripta: ${cripta.cripta}"
            txtSeccion.text = "Sección: ${cripta.seccion}"
            txtZona.text = "Zona: ${cripta.zona}"
            txtIglesia.text = "Iglesia: ${cripta.iglesia}"
            txtUbicacion.text = "Precio: ${cripta.precio.toPesos()}"
            txtDisponibilidad.text = if (cripta.disponible) "Disponible" else "No Disponible"
            val color = if (cripta.disponible) {
                @Suppress("DEPRECATION")
                resources.getColor(android.R.color.holo_green_dark)
            } else {
                @Suppress("DEPRECATION")
                resources.getColor(android.R.color.holo_red_dark)
            }

            txtDisponibilidad.setTextColor(color)

            // Deshabilitar botón si la cripta no está disponible
            btnComprarCripta.isEnabled = cripta.disponible

            btnComprarCripta.setOnClickListener {
                if (cripta.disponible) {
                    procesarCompra()
                } else {
                    Toast.makeText(requireContext(), "Cripta no disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }


        viewModel.pago.observe(viewLifecycleOwner) { pago ->
            pago?.let {
                Toast.makeText(requireContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                binding.btnComprarCripta.isGone = isLoading
                binding.progressBar.isGone = !isLoading
            }
        }
    }

    private fun procesarCompra() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaLimite = dateFormat.format(calendar.time)
        val pagoCreate = PagoCreate(
            IdCliente = viewModel.getId(),
            IdCripta = cripta.id,
            IdTipoPago = IdPUE,
            MontoTotal=cripta.precio,
            FechaLimite=fechaLimite,
            TipoPago = 1
        )
        viewModel.realizarPago(pagoCreate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
