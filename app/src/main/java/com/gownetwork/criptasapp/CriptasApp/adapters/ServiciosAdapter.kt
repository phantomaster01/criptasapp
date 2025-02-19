package com.gownetwork.criptasapp.CriptasApp.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gownetwork.criptasapp.CriptasApp.extensions.removeBase64Prefix
import com.gownetwork.criptasapp.network.entities.Servicio
import mx.com.gownetwork.criptas.databinding.ItemServiciosBinding

class ServiciosAdapter(
    private val serviciosList: List<Servicio>,
    private val onVerMasClick: (String) -> Unit
) :
    RecyclerView.Adapter<ServiciosAdapter.ServicioViewHolder>() {

    class ServicioViewHolder(
        private val binding: ItemServiciosBinding,
        private val onVerMasClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(servicio: Servicio) = with(binding){
            itemNombre.text = servicio.Nombre

            // Decodificar imagen Base64 si existe
            if (servicio.Img.isNotEmpty()) {
                val decodedBytes = Base64.decode(servicio.Img.removeBase64Prefix(), Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                itemImage.setImageBitmap(bitmap)
            }

            btnVerMas.setOnClickListener {
                onVerMasClick(servicio.Id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val binding = ItemServiciosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServicioViewHolder(binding, onVerMasClick)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        holder.bind(serviciosList[position])
    }

    override fun getItemCount(): Int = serviciosList.size
}