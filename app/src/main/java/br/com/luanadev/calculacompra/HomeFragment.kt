package br.com.luanadev.calculacompra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.luanadev.calculacompra.databinding.HomeFragmentBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.android.synthetic.main.list_item_produto.*
import net.objecthunter.exp4j.ExpressionBuilder

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private val binding by viewBinding {
        HomeFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {}



}