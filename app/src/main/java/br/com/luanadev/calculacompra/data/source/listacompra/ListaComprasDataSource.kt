package br.com.luanadev.calculacompra.data.source.listacompra

import androidx.lifecycle.LiveData
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras

interface ListaComprasDataSource {

    fun observeListaCompras(): LiveData<Result<List<ListaCompras>>>

    suspend fun getListaCompras(): Result<List<ListaCompras>>

    suspend fun refreshListaCompras()

    fun observeListaCompras(listaCompraId: String): LiveData<Result<ListaCompras>>

    suspend fun getListaCompras(listaCompraId: String): Result<ListaCompras>

    suspend fun saveListaCompras(listaCompra: ListaCompras)

    suspend fun refreshListaCompras(listaCompraId: String)

    suspend fun deleteAllListaCompras()

    suspend fun deleteListaCompras(listaCompraId: String)

    suspend fun completeListaCompras(listaCompraId: ListaCompras)

    suspend fun completeListaCompras(listaCompraId: String)

    suspend fun activateListaCompras(listaCompra: ListaCompras)

    suspend fun activateListaCompras(listaCompraId: String)

    suspend fun clearCompletedListaCompras()
}