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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gownetwork.criptasapp.CriptasApp.extensions.toBase64Async
import com.gownetwork.criptasapp.CriptasApp.extensions.toPesos
import com.gownetwork.criptasapp.network.entities.Pago
import mx.com.gownetwork.criptas.databinding.BottomsheetEvidenciaPagoBinding
import java.util.concurrent.Executors

class EvidenciaPagoBottomSheet(private val pago: Pago) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetEvidenciaPagoBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetEvidenciaPagoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtPagoInfo.text = "Evidencia de Pago: ${pago.MontoTotal?.toPesos()}"
        // Configurar los launchers para tomar foto o seleccionar imagen
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    binding.imgPreview.setImageURI(it)
                    convertirUriABitmap(it) { bitmap ->
                        convertirBitmapBase64(bitmap)
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
                }
            }
        }

        binding.btnSubirFoto.setOnClickListener { seleccionarFoto() }
        binding.btnTomarFoto.setOnClickListener { tomarFoto() }
        binding.btnCerrar.setOnClickListener { dismiss() }
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
