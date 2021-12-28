package br.com.luanadev.calculacompra.presentation.listacompraDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.luanadev.calculacompra.R
import br.com.luanadev.calculacompra.databinding.DetailListaComprasFragmentBinding
import br.com.luanadev.calculacompra.presentation.listacompra.DELETE_RESULT_OK
import br.com.luanadev.calculacompra.util.EventObserver
import br.com.luanadev.calculacompra.util.setupRefreshLayout
import br.com.luanadev.calculacompra.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

class DetailListaComprasFragment : Fragment() {
    private lateinit var binding: DetailListaComprasFragmentBinding

    private val detailListaComprasFragmentArgs: DetailListaComprasFragmentArgs by navArgs()

    private val viewModel by viewModels<DetailListaComprasViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailListaComprasFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupNavigation()
        setupFab()
        setupRefreshLayout(binding.refreshLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteListaCompras()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lista_compras_details_menu, menu)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)

    }

    private fun setupNavigation() {
        viewModel.deleteListaCompraskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = DetailListaComprasFragmentDirections.
            actionListaComprasDetailFragmentToListaComprasFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
        viewModel.editListaComprasEvent.observe(viewLifecycleOwner, EventObserver {
            val action = DetailListaComprasFragmentDirections
                .actionListaComprasDetailFragmentToAddEditTaskFragment(
                    detailListaComprasFragmentArgs.listaCompraId,
                    resources.getString(R.string.edit_lista_compras)
                )
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        binding.editListaCompraDetailFab?.setOnClickListener {
            viewModel.editListaCompras()
        }
    }
}
