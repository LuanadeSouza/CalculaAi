package br.com.luanadev.calculacompra.presentation.listacompra

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.luanadev.calculacompra.R
import br.com.luanadev.calculacompra.databinding.ListaComprasFragmentBinding
import br.com.luanadev.calculacompra.util.EventObserver
import br.com.luanadev.calculacompra.util.setupRefreshLayout
import br.com.luanadev.calculacompra.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class ListaComprasFragment : Fragment() {

    private val viewModel by viewModels<ListaComprasViewModel>()
    private val listaCompraArgs: ListaComprasFragmentArgs by navArgs()
    private lateinit var binding: ListaComprasFragmentBinding
    private lateinit var adapter: ListaComprasAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListaComprasFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedListaCompras()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadListaCompras(true)
                true
            }
            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lista_compras_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(binding.refreshLayout, binding.listCompras)
        setupNavigation()
        setupFab()
    }

    private fun setupNavigation() {
        viewModel.openListaComprasEvent.observe(viewLifecycleOwner, EventObserver {
            openListaComprasDetails(it)
        })
        viewModel.newListaComprasEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewListaCompra()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(listaCompraArgs.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_list_compra_menu, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> ListaComprasFilterType.ACTIVE_LISTA_COMPRAS
                        R.id.completed -> ListaComprasFilterType.COMPLETED_LISTA_COMPRAS
                        else -> ListaComprasFilterType.ALL_LISTA_COMPRAS
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupFab() {
        binding.addListaCompraFab.setOnClickListener {
            navigateToAddNewListaCompra()
        }
    }

    private fun navigateToAddNewListaCompra() {
        val action = ListaComprasFragmentDirections
            .actionListaComprasFragmentToAddEditlistaComprasFragment(
                null,
                resources.getString(R.string.add_lista_compras)
            )
        findNavController().navigate(action)
    }

    private fun openListaComprasDetails(listaCompraId: String) {
        val action =
            ListaComprasFragmentDirections.actionListaComprasFragmentToListaComprasDetailFragment(
                listaCompraId
            )
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null) {
            adapter = ListaComprasAdapter(viewModel)
            binding.listCompras.adapter = adapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
