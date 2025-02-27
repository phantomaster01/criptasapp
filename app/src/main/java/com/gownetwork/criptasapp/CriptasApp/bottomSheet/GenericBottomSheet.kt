package com.gownetwork.criptasapp.CriptasApp.bottomSheet

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.com.gownetwork.criptas.R
import mx.com.gownetwork.criptas.databinding.BottomsheetGenericBinding

class GenericBottomSheet(
    private val title: String,
    private val adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>,

) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetGenericBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetGenericBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.WhiteBottomSheet)
        // Configurar Toolbar
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { dismiss() } // Cierra el BottomSheet al presionar el botón de cerrar

        // Configurar RecyclerView con el adaptador recibido
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            // 🚀 Evitar que el BottomSheet bloquee el Swipe horizontal en RecyclerView
            binding.recyclerView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> behavior.isDraggable = false // Deshabilita el arrastre vertical mientras se desliza
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> behavior.isDraggable = true // Reactiva el arrastre vertical
                }
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
