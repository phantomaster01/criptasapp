package com.gownetwork.criptasapp.CriptasApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gownetwork.criptasapp.network.entities.MisCriptas
import mx.com.gownetwork.criptas.databinding.ItemMisCriptasBinding

class MisCriptasAdapter(
    private val criptasList: List<MisCriptas>
) : RecyclerView.Adapter<MisCriptasAdapter.MisCriptasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisCriptasViewHolder {
        val binding = ItemMisCriptasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MisCriptasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MisCriptasViewHolder, position: Int) {
        holder.bind(criptasList[position])
    }

    override fun getItemCount(): Int = criptasList.size

    class MisCriptasViewHolder(private val binding: ItemMisCriptasBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cripta: MisCriptas) {
            binding.tvCriptaNombre.text = "Cripta: ${cripta.cripta}"
            binding.tvUbicacion.text = "Ubicación: Sección ${cripta.seccion} - Zona ${cripta.zona} - Iglesia ${cripta.iglesia}"
            binding.tvFechaCompra.text = "Fecha de Compra: ${cripta.fechaCompra}"
            binding.tvFallecidos.text = "Fallecidos: ${cripta.fallecidos}"
            binding.tvBeneficiarios.text = "Beneficiarios: ${cripta.beneficiarios}"
            binding.tvVisitas.text = "Visitas: ${cripta.visitas}"
        }
    }
}
