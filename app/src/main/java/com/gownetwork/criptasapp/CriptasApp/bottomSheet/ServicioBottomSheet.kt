package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.CriptasApp.extensions.IdServicioCriptas
import com.gownetwork.criptasapp.CriptasApp.extensions.removeBase64Prefix
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.ServiciosRepository
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import com.gownetwork.criptasapp.network.entities.Servicio
import com.gownetwork.criptasapp.viewmodel.ServiciosViewModel
import mx.com.gownetwork.criptas.R
import mx.com.gownetwork.criptas.databinding.BottomSheetServicioBinding

class ServicioBottomSheet(private val idServicio: String) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetServicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ServiciosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetServicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheet)
        viewModel = ServiciosViewModel(ServiciosRepository(ApiClient.service), requireContext())
        initViews()
        initObservers()
        fetchServicioDetalle()
    }

    private fun onSolicitarClick(servicio: Servicio) {
        if(viewModel.getId() == null){
            val bottomSheet = SolicitarSessionBottomSheet()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }else{
            val bottomSheet = SolicitudBottomSheet("Informes del servicio ".plus(servicio.Nombre), servicio.Id, "Informes del servicio: ".plus(servicio.Nombre))
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun initViews()=with(binding){
        fabShare.setOnClickListener{
            val url = "https://gestioncriptas.gownetwork.com.mx/servicio/$idServicio"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Consulta este servicio: $url")
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir vía"))
        }
        fabMessage.setOnClickListener{
            viewModel.servicioDetalle.value?.let {
                onSolicitarClick(it)
            }
        }
    }

    private fun initObservers() {
        viewModel.servicioDetalle.observe(viewLifecycleOwner) { servicio ->
            if (servicio != null) {
                mostrarServicio(servicio)
            } else {
                Toast.makeText(requireContext(), "No se pudo cargar el servicio", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun fetchServicioDetalle() {
        viewModel.fetchServicioDetalle(idServicio)
    }

    private fun mostrarServicio(servicio: Servicio) = with(binding) {
        txtNombreServicio.text = servicio.Nombre ?: "Sin nombre"
        txtDescripcionServicio.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(servicio.Descripcion ?: "Sin descripción", Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(servicio.Descripcion ?: "Sin descripción")
        }
        if (servicio.Img.isNotEmpty()) {
            val decodedBytes = Base64.decode(servicio.Img.removeBase64Prefix(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imgServicio.setImageBitmap(bitmap)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val frame = it.findViewById<FrameLayout>(R.id.frame)

            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val toolbarHeight = requireActivity().findViewById<View>(R.id.toolbarServicios)?.height ?: 0

                val newHeight = screenHeight
                frame?.layoutParams?.height = newHeight
                frame?.requestLayout()
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                behavior.isFitToContents = false
                behavior.peekHeight = newHeight

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}