package br.com.luanadev.calculacompra.data.source.produto

import androidx.lifecycle.LiveData
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.entity.produto.Produtos

interface ProdutoDataSource {
    fun observeProduto(): LiveData<Result<List<Produtos>>>

    suspend fun getProduto(): Result<List<Produtos>>

    suspend fun refreshProduto()

    fun observeProduto(produtoId: String): LiveData<Result<Produtos>>

    suspend fun getProduto(produtoId: String): Result<Produtos>

    suspend fun refreshProduto(produtoId: String)

    suspend fun saveProduto(produto: Produtos)

    suspend fun deleteAllProdutos()

    suspend fun deleteProduto(produtoId: String)
}