package br.com.luanadev.calculacompra.data.source.produto.remote

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import br.com.luanadev.calculacompra.data.entity.produto.Produtos
import br.com.luanadev.calculacompra.data.source.produto.ProdutoDataSource
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Error
import br.com.luanadev.calculacompra.data.Result.Success
import kotlinx.coroutines.delay

object ProdutoRemoteDataSource : ProdutoDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var PRODUTO_SERVICE_DATA = LinkedHashMap<String, Produtos>(0)

    init {
        addProduto("Pizza", 3, 10.50)
    }

    private val observableProduto = MutableLiveData<Result<List<Produtos>>>()

    @SuppressLint("NullSafeMutableLiveData")
    override suspend fun refreshProduto() {
        observableProduto.value = getProduto()
    }

    override suspend fun refreshProduto(produtoId: String) {
        refreshProduto()
    }

    override fun observeProduto(): LiveData<Result<List<Produtos>>> {
        return observableProduto
    }

    override fun observeProduto(produtoId: String): LiveData<Result<Produtos>> {
        return observableProduto.map { produto ->
            when (produto) {
                is Result.Loading -> Result.Loading
                is Error -> Error(produto.exception)
                is Success -> {
                    val produtos = produto.data.firstOrNull() { it.produtoId == produtoId }
                        ?: return@map Error(Exception("Not found"))
                    Success(produtos)
                }
            }
        }
    }

    override suspend fun getProduto(): Result<List<Produtos>> {
        // Simulate network by delaying the execution.
        val produtos = PRODUTO_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(produtos)
    }

    override suspend fun getProduto(produtoId: String): Result<Produtos> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        PRODUTO_SERVICE_DATA[produtoId]?.let {
            return Success(it)
        }
        return Error(Exception("Produto not found"))
    }

    private fun addProduto(nome: String, quantidade: Int, valor: Double) {
        val newProduto = Produtos(nome, quantidade, valor)
        PRODUTO_SERVICE_DATA[newProduto.produtoId] = newProduto
    }

    override suspend fun saveProduto(produto: Produtos) {
        PRODUTO_SERVICE_DATA[produto.produtoId] = produto
    }

    override suspend fun deleteAllProdutos() {
        PRODUTO_SERVICE_DATA.clear()
    }

    override suspend fun deleteProduto(produtoId: String) {
        PRODUTO_SERVICE_DATA.remove(produtoId)
    }
}
