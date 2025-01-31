package com.gownetwork.criptasapp.CriptasApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gownetwork.ctiptasapp.databinding.FragmentMisCriptasBinding

class CriptasFragment: Fragment() {

    private var _binding: FragmentMisCriptasBinding? = null
    private val binding get() = _binding!! // Uso seguro de View Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflamos el layout usando View Binding
        _binding = FragmentMisCriptasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita memory leaks
    }
}