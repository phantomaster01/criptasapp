package com.gownetwork.criptasapp.CriptasApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.gownetwork.criptasapp.CriptasApp.extensions.toReadableDate
import com.gownetwork.criptasapp.network.entities.MisCriptas
import mx.com.gownetwork.criptas.databinding.ItemMisCriptasBinding
import java.text.NumberFormat
import java.util.Locale

class MisCriptasAdapter(
    private val criptasList: List<MisCriptas>,
    private val open: (String) -> Unit
) : RecyclerView.Adapter<MisCriptasAdapter.MisCriptasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisCriptasViewHolder {
        val binding = ItemMisCriptasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MisCriptasViewHolder(binding,open)
    }

    override fun onBindViewHolder(holder: MisCriptasViewHolder, position: Int) {
        holder.bind(criptasList[position])
    }

    override fun getItemCount(): Int = criptasList.size

    class MisCriptasViewHolder(private val binding: ItemMisCriptasBinding, private val open: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cripta: MisCriptas) {
            with(binding){
                contenedor.setOnClickListener{
                    open(cripta.id)
                }
                tvCriptaNombre.text = "Cripta: ${cripta.cripta}"
                tvUbicacion.text = "Ubicación: Sección ${cripta.seccion} - Zona ${cripta.zona} - Iglesia ${cripta.iglesia}"
                tvBeneficiarios.isGone=!cripta.pagado
                tvVisitas.isGone=!cripta.pagado
                val color = if (cripta.pagado) {
                    @Suppress("DEPRECATION")
                    tvFechaCompra.context.resources.getColor(android.R.color.holo_green_dark)
                } else {
                    @Suppress("DEPRECATION")
                    tvFechaCompra.context.resources.getColor(android.R.color.holo_red_dark)
                }

                tvFechaCompra.setTextColor(color)
                if(cripta.pagado){
                    tvFechaCompra.text = "Fecha de Compra: ${cripta.fechaCompra}"
                    tvFallecidos.text = "Fallecidos: ${cripta.fallecidos}"
                    tvBeneficiarios.text = "Beneficiarios: ${cripta.beneficiarios}"
                    tvVisitas.text = "Visitas: ${cripta.visitas}"
                }else{
                    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                    val precioFormateado = formatoMoneda.format(cripta.precio)
                    tvFechaCompra.text = "Fecha de Limite de Pago:\n${cripta.fechaCompra.toReadableDate()}"
                    tvFallecidos.text = "Pago Pendiente: ${precioFormateado}"
                }
            }

        }
    }
}
