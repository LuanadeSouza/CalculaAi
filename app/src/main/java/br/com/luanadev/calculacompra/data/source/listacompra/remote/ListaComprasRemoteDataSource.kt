package br.com.luanadev.calculacompra.data.source.listacompra.remote

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Error
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.data.source.listacompra.ListaComprasDataSource
import kotlinx.coroutines.delay

object ListaComprasRemoteDataSource : ListaComprasDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var LISTA_COMPRAS_SERVICE_DATA = LinkedHashMap<String, ListaCompras>(0)

    private val observableListaCompras = MutableLiveData<Result<List<ListaCompras>>>()

    init {
        addListaCompras("Farmacia", false)
    }

    @SuppressLint("NullSafeMutableLiveData")
    override suspend fun refreshListaCompras() {
        observableListaCompras.value = getListaCompras()
    }

    override suspend fun refreshListaCompras(listaCompraId: String) {
        refreshListaCompras()
    }

    override fun observeListaCompras(): LiveData<Result<List<ListaCompras>>> {
        return observableListaCompras
    }

    override fun observeListaCompras(listaId: String): LiveData<Result<ListaCompras>> {
        return observableListaCompras.map { listaCompras ->
            when (listaCompras) {
                is Result.Loading -> Result.Loading
                is Error -> Error(listaCompras.exception)
                is Success -> {
                    val listaCompra =
                        listaCompras.data.firstOrNull() { it.listaComprasId == listaId }
                            ?: return@map Error(Exception("Not found"))
                    Success(listaCompra)
                }
            }
        }
    }

    override suspend fun getListaCompras(): Result<List<ListaCompras>> {
        // Simulate network by delaying the execution.
        val listaCompra = LISTA_COMPRAS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(listaCompra)
    }

    override suspend fun getListaCompras(listaCompraId: String): Result<ListaCompras> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        LISTA_COMPRAS_SERVICE_DATA[listaCompraId]?.let {
            return Success(it)
        }
        return Error(Exception("Lista de Compras not found"))
    }

    private fun addListaCompras(nome: String, complete: Boolean) {
        val newListaCompras = ListaCompras(nome, complete)
        LISTA_COMPRAS_SERVICE_DATA[newListaCompras.listaComprasId] = newListaCompras
    }

    override suspend fun saveListaCompras(listaCompra: ListaCompras) {
        LISTA_COMPRAS_SERVICE_DATA[listaCompra.listaComprasId] = listaCompra
    }

    override suspend fun completeListaCompras(listaCompra: ListaCompras) {
        val completeListaCompras =
            ListaCompras(listaCompra.nome, listaCompra.isCompleted, listaCompra.listaComprasId)
        LISTA_COMPRAS_SERVICE_DATA[listaCompra.listaComprasId] = completeListaCompras
    }

    override suspend fun completeListaCompras(listaId: String) {
        // Not required for the remote data source
    }

    override suspend fun activateListaCompras(listaCompra: ListaCompras) {
        val listaCompras =
            ListaCompras(listaCompra.nome, listaCompra.isCompleted, listaCompra.listaComprasId)
        LISTA_COMPRAS_SERVICE_DATA[listaCompras.listaComprasId] = listaCompras
    }

    override suspend fun activateListaCompras(listaCompraId: String) {
        // Not required for the remote data source
    }

    override suspend fun clearCompletedListaCompras() {
        LISTA_COMPRAS_SERVICE_DATA = LISTA_COMPRAS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, ListaCompras>
    }

    override suspend fun deleteAllListaCompras() {
        LISTA_COMPRAS_SERVICE_DATA.clear()
    }

    override suspend fun deleteListaCompras(listaCompraId: String) {
        LISTA_COMPRAS_SERVICE_DATA.remove(listaCompraId)
    }
}
