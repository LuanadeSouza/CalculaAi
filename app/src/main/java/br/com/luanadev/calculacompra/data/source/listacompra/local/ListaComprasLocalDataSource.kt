package br.com.luanadev.calculacompra.data.source.listacompra.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Error
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.data.source.listacompra.ListaComprasDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ListaComprasLocalDataSource internal constructor(
    private val listaComprasDao: ListaComprasDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ListaComprasDataSource {

    override fun observeListaCompras(): LiveData<Result<List<ListaCompras>>> {
        return listaComprasDao.observeListaCompra().map {
            Success(it)
        }
    }

    override fun observeListaCompras(listaCompraId: String): LiveData<Result<ListaCompras>> {
        return listaComprasDao.observeListaComprasById(listaCompraId).map {
            Success(it)
        }
    }

    override suspend fun refreshListaCompras(listaCompraId: String) {
    }

    override suspend fun refreshListaCompras() {
    }

    override suspend fun getListaCompras(): Result<List<ListaCompras>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(listaComprasDao.getListaCompras())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getListaCompras(listaCompraId: String): Result<ListaCompras> =
        withContext(ioDispatcher) {
            try {
                val listaComprasDao = listaComprasDao.getListaComprasById(listaCompraId)
                if (listaComprasDao != null) {
                    return@withContext Success(listaComprasDao)
                } else {
                    return@withContext Error(Exception("Lista Compra not found!"))
                }
            } catch (e: Exception) {
                return@withContext Error(e)
            }
        }

    override suspend fun saveListaCompras(listaCompras: ListaCompras) = withContext(ioDispatcher) {
        listaComprasDao.insertListaCompras(listaCompras)
    }

    override suspend fun completeListaCompras(listaCompras: ListaCompras) =
        withContext(ioDispatcher) {
            listaComprasDao.updateCompleted(listaCompras.listaComprasId, true)
        }

    override suspend fun completeListaCompras(listaComprasId: String) {
        listaComprasDao.updateCompleted(listaComprasId, true)
    }

    override suspend fun activateListaCompras(listaCompras: ListaCompras) =
        withContext(ioDispatcher) {
            listaComprasDao.updateCompleted(listaCompras.listaComprasId, false)
        }

    override suspend fun activateListaCompras(listaCompraId: String) {
        listaComprasDao.updateCompleted(listaCompraId, false)
    }

    override suspend fun clearCompletedListaCompras() = withContext(ioDispatcher) {
        listaComprasDao.deleteCompletedListaCompras()
    }

    override suspend fun deleteAllListaCompras() = withContext(ioDispatcher) {
        listaComprasDao.deleteListaCompras()
    }

    override suspend fun deleteListaCompras(listaCompraId: String) =
        withContext<Unit>(ioDispatcher) {
            listaComprasDao.deleteListaComprasById(listaCompraId)
        }
}
