package br.com.luanadev.calculacompra.presentation.listacompra

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<ListaCompras>?) {
    items?.let {
        (listView.adapter as ListaComprasAdapter).submitList(items)
    }
}

@BindingAdapter("app:completedTask")
fun setStyle(textView: TextView, enabled: Boolean) {
    if (enabled) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}