package br.com.luanadev.calculacompra.data.source

import androidx.lifecycle.LiveData
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Produtos

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

    suspend fun refreshListaCompras(listaCompraId: String)

    suspend fun refreshListaCompras()

    suspend fun deleteAllListaCompras()

    suspend fun deleteListaCompras(listaCompraId: String)

    suspend fun completeListaCompras(listaCompraId: Produtos)

    suspend fun completeListaCompras(produtoId: String)

    suspend fun activateListaCompras(produto: Produtos)

    suspend fun activateListaCompras(listaCompraId: String)

    suspend fun clearCompletedListaCompras()
}