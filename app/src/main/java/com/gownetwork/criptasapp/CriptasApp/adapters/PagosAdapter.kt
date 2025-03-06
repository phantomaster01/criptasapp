package com.gownetwork.criptasapp.CriptasApp.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gownetwork.criptasapp.CriptasApp.extensions.toPesos
import com.gownetwork.criptasapp.CriptasApp.extensions.toReadableDate
import mx.com.gownetwork.criptas.databinding.ItemPagoBinding
import com.gownetwork.criptasapp.network.entities.Pago
import mx.com.gownetwork.criptas.R

@Suppress("DEPRECATION")
class PagosAdapter(
    private var pagos: MutableList<Pago>,
    private val onPagoClick: (Pago,Int) -> Unit,
    private val onEliminarClick: (Pago,Int) -> Unit
) : RecyclerView.Adapter<PagosAdapter.PagoViewHolder>() {

    inner class PagoViewHolder(private val binding: ItemPagoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pago: Pago)=with(binding) {
            if(pago.Pagado==true){
                txtMonto.text = "Monto: ${pago.MontoTotal?.toPesos()}"
                txtFecha.text = "Fecha: ${pago.FechaPagado?.toReadableDate()}"
                txtEstatus.isGone = false
                txtEstatus.text = "Pagado"
                val color = txtFecha.context.resources.getColor(android.R.color.holo_green_light)
                txtEstatus.setBackgroundColor(color)
            }else{
                txtMonto.text = "Monto: ${pago.MontoTotal?.toPesos()}"
                txtFecha.text = "Fecha: ${pago.FechaLimite?.toReadableDate()}"
                if(pago.Estatus==true){
                    txtEstatus.isGone = false
                    txtEstatus.text = "Evidencia enviada"
                    val color = txtFecha.context.resources.getColor(android.R.color.holo_blue_light)
                    txtEstatus.setBackgroundColor(color)
                }else if(pago.EstatusSolicitud==false){
                    txtEstatus.isGone = false
                    txtEstatus.text = "Evidencia rechazada"
                    val color = txtFecha.context.resources.getColor(android.R.color.holo_red_light)
                    txtEstatus.setBackgroundColor(color)
                }else{
                    txtEstatus.isGone = true
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val binding = ItemPagoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        holder.bind(pagos[position])
    }

    override fun getItemCount(): Int = pagos.size

    fun actualizarLista(nuevaLista: List<Pago>) {
        pagos.clear()
        pagos.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    fun getSwipeHandler(context: Context): ItemTouchHelper? {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            private val iconPagar: Drawable = context.getDrawable(R.drawable.ic_upload_white)!!
            private val iconEliminar: Drawable = context.getDrawable(R.drawable.ic_delete_white)!!
            private val backgroundPagar = ColorDrawable(Color.parseColor("#4CAF50")) // Verde
            private val backgroundEliminar = ColorDrawable(Color.RED) // Rojo

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> onPagoClick(pagos[position],position)  // Swipe izquierda → Pagar
                    ItemTouchHelper.RIGHT -> onEliminarClick(pagos[position],position)      // Swipe derecha → Eliminar
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - iconPagar.intrinsicHeight) / 2

                if (dX < 0) { // Swipe izquierda → Pagar
                    backgroundPagar.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    backgroundPagar.draw(c)

                    val iconLeft = itemView.right - iconMargin - iconPagar.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconTop = itemView.top + (itemView.height - iconPagar.intrinsicHeight) / 2
                    val iconBottom = iconTop + iconPagar.intrinsicHeight

                    iconPagar.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    iconPagar.draw(c)
                } else if (dX > 0) { // Swipe derecha → Eliminar
                    backgroundEliminar.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    backgroundEliminar.draw(c)

                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + iconEliminar.intrinsicWidth
                    val iconTop = itemView.top + (itemView.height - iconEliminar.intrinsicHeight) / 2
                    val iconBottom = iconTop + iconEliminar.intrinsicHeight

                    iconEliminar.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    iconEliminar.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
    }
}
