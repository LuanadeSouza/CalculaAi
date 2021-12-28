package br.com.luanadev.calculacompra.presentation.addlistacompras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.luanadev.calculacompra.databinding.AddListaComprasFragmentBinding
import br.com.luanadev.calculacompra.presentation.listacompra.ADD_EDIT_RESULT_OK
import br.com.luanadev.calculacompra.util.EventObserver
import br.com.luanadev.calculacompra.util.setupRefreshLayout
import br.com.luanadev.calculacompra.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

class AddListaComprasFragment : Fragment() {

    private lateinit var binding: AddListaComprasFragmentBinding

    private val addListaComprasFragmentArgs: AddListaComprasFragmentArgs by navArgs()

    private val viewModel by viewModels<AddListaComprasViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddListaComprasFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewmodel = viewModel
        setupSnackbar()
        setupNavigation()
        setupRefreshLayout(binding.refreshLayout)
        viewModel.start(addListaComprasFragmentArgs.listaCompraId)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.listaComprasUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddListaComprasFragmentDirections
                .actionAddListaComprasFragmentToListaComprasFragment(ADD_EDIT_RESULT_OK)
            findNavController().navigate(action)
        })
    }
}