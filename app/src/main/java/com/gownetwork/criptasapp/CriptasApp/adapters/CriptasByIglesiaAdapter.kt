package com.gownetwork.criptasapp.CriptasApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import mx.com.gownetwork.criptas.R
import mx.com.gownetwork.criptas.databinding.ItemCriptaByIglesiaBinding

class CriptasByIglesiaAdapter(private val criptas: List<CriptasByIglesia>, private val openBottomSheet: (CriptasByIglesia) -> Unit) :
    RecyclerView.Adapter<CriptasByIglesiaAdapter.CriptasViewHolder>() {

    class CriptasViewHolder(private val binding: ItemCriptaByIglesiaBinding, private val openBottomSheet: (CriptasByIglesia) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cripta: CriptasByIglesia) {
            with(binding){
                tvCriptaNombre.text = "Cripta: ${cripta.cripta}"
                tvUbicacion.text = "Ubicación: Sección ${cripta.seccion} - Zona ${cripta.zona}"
                tvEstatus.text = if (cripta.estatus) "Disponible" else "Apartado"

                tvEstatus.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if(!cripta.estatus) R.color.colorGris else R.color.peso_normal
                    )
                )
                open.setOnClickListener{
                    openBottomSheet(cripta)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriptasViewHolder {
        val binding = ItemCriptaByIglesiaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CriptasViewHolder(binding, openBottomSheet)
    }

    override fun onBindViewHolder(holder: CriptasViewHolder, position: Int) {
        holder.bind(criptas[position])
    }

    override fun getItemCount() = criptas.size
}
