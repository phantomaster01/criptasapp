package com.gownetwork.criptasapp.CriptasApp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gownetwork.criptasapp.CriptasApp.activitys.MenuCriptaActivity
import com.gownetwork.criptasapp.CriptasApp.adapters.MisCriptasAdapter
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToLogin
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToRegister
import com.gownetwork.criptasapp.CriptasApp.extensions.setTitle
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.Repository.CriptasRepository
import com.gownetwork.criptasapp.viewmodel.CriptasViewModel
import mx.com.gownetwork.criptas.databinding.FragmentMisCriptasBinding

class CriptasFragment: Fragment() {

    private var _binding: FragmentMisCriptasBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CriptasViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("Mis Criptas")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCriptasBinding.inflate(inflater, container, false)
        viewModel = CriptasViewModel(CriptasRepository(ApiClient.service), requireContext())
        with(binding){
            var gone = viewModel.getId() == null
            layoutNoRegistrado.isGone = !gone
            rv.isGone = gone
            if(gone)
                initViewsGo()
            else{
                initRecyclerView()
                initObservers()
                viewModel.fetchMisCriptas()
            }
        }
        return binding.root
    }

    private fun initViewsGo()=with(binding){
        btnIniciarSesion.setOnClickListener{
            requireActivity().navigateToLogin()
        }
        btnRegistrarse.setOnClickListener{
            requireActivity().navigateToRegister()
        }
    }

    private fun initRecyclerView() {
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initObservers() {
        viewModel.misCriptas.observe(viewLifecycleOwner) { criptas ->
            binding.rv.adapter = MisCriptasAdapter(criptas,::goToMenuCriptaActivity)
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

    private fun goToMenuCriptaActivity(id_cripta:String) {
        val intent = Intent(requireContext(), MenuCriptaActivity::class.java)
        intent.putExtra("IdCripta", id_cripta)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita memory leaks
    }
}