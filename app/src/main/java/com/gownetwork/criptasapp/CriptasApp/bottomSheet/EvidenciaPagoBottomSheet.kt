package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.CriptasApp.extensions.toBase64Async
import com.gownetwork.criptasapp.CriptasApp.extensions.toPesos
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.PagosRepository
import com.gownetwork.criptasapp.network.entities.EvidenciaCreate
import com.gownetwork.criptasapp.network.entities.Pago
import com.gownetwork.criptasapp.viewmodel.PagosViewModel
import mx.com.gownetwork.criptas.databinding.BottomsheetEvidenciaPagoBinding
import java.util.concurrent.Executors

class EvidenciaPagoBottomSheet(private val pago: Pago) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetEvidenciaPagoBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var pagosViewModel: PagosViewModel
    private var base64 : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetEvidenciaPagoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagosViewModel = PagosViewModel(PagosRepository(ApiClient.service), requireContext())
        binding.txtPagoInfo.text = "Evidencia de Pago: ${pago.MontoTotal?.toPesos()}"
        // Configurar los launchers para tomar foto o seleccionar imagen
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    binding.imgPreview.setImageURI(it)
                    convertirUriABitmap(it) { bitmap ->
                        convertirBitmapBase64(bitmap)
                        binding.btnCerrar.isEnabled = true
                    }
                }
            }
        }

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    binding.imgPreview.setImageBitmap(it)
                    convertirBitmapBase64(it)
                    binding.btnCerrar.isEnabled = true
                }
            }
        }

        binding.btnSubirFoto.setOnClickListener { seleccionarFoto() }
        binding.btnTomarFoto.setOnClickListener { tomarFoto() }
        binding.btnCerrar.setOnClickListener {
            if(base64==null){
                Toast.makeText(requireContext(),"Agrega Imagen", Toast.LENGTH_SHORT).show()
            }else{
                val create = EvidenciaCreate(
                    IdPago = pago.Id,
                    Evidencia = base64
                )
                pagosViewModel.subirEvidencia(
                    create
                )
            }

        }
        initObservers()
    }

    private fun initObservers(){
        pagosViewModel.evidencia.observe(this) { item ->
            if (item != null) {
                dismiss()
            }
        }

        pagosViewModel.isLoading.observe(this){
            binding.progressIndicator.isGone = !it
            binding.btnSubirFoto.isGone = it
            binding.btnTomarFoto.isGone = it
            binding.btnCerrar.isGone = it
        }

        pagosViewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertirUriABitmap(uri: Uri, callback: (Bitmap) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                requireActivity().runOnUiThread {
                    callback(bitmap)
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun convertirBitmapBase64(bitmap: Bitmap) {
        bitmap.toBase64Async { base64String ->
            base64 = base64String
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Imagen convertida a Base64", Toast.LENGTH_SHORT).show()
                // Aquí puedes enviar el base64 al servidor o guardarlo en tu base de datos.
            }
        }
    }


    private fun seleccionarFoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun tomarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
