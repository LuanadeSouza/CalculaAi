package br.com.luanadev.calculacompra.presentation.listacompra

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.databinding.ListaComprasItemBinding

class ListaComprasAdapter (private val viewModel: ListaComprasViewModel) :
    ListAdapter<ListaCompras, ListaComprasAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListaComprasItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: ListaComprasViewModel, item: ListaCompras) {

            binding.viewmodel = viewModel
            binding.listaCompra = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListaComprasItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<ListaCompras>() {
    override fun areItemsTheSame(oldItem: ListaCompras, newItem: ListaCompras): Boolean {
        return oldItem.listaComprasId == newItem.listaComprasId
    }

    override fun areContentsTheSame(oldItem: ListaCompras, newItem: ListaCompras): Boolean {
        return oldItem == newItem
    }
}
