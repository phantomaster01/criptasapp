package com.gownetwork.criptasapp.CriptasApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gownetwork.criptasapp.CriptasApp.adapters.MisCriptasAdapter
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.CriptasRepository
import com.gownetwork.criptasapp.viewmodel.CriptasViewModel
import mx.com.gownetwork.criptas.databinding.FragmentMisCriptasBinding

class CriptasFragment: Fragment() {

    private var _binding: FragmentMisCriptasBinding? = null
    private val binding get() = _binding!! // Uso seguro de View Binding
    private lateinit var viewModel : CriptasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCriptasBinding.inflate(inflater, container, false)
        viewModel = CriptasViewModel(CriptasRepository(ApiClient.service), requireContext())
        initRecyclerView()
        initObservers()
        viewModel.fetchMisCriptas()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun initObservers() {
        viewModel.misCriptas.observe(viewLifecycleOwner) { criptas ->
            binding.rv.adapter = MisCriptasAdapter(criptas)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita memory leaks
    }
}