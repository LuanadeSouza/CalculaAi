package br.com.luanadev.calculacompra.data.source.listacompra

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.data.source.listacompra.local.ListaComprasDatabase
import br.com.luanadev.calculacompra.data.source.listacompra.local.ListaComprasLocalDataSource
import br.com.luanadev.calculacompra.data.source.listacompra.remote.ListaComprasRemoteDataSource
import kotlinx.coroutines.*

class DefaultListaComprasRepository(application: Application) {
    private val listaComprasRemoteDataSource: ListaComprasDataSource
    private val listaComprasLocalDataSource: ListaComprasDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: DefaultListaComprasRepository? = null

        fun getRepository(app: Application): DefaultListaComprasRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultListaComprasRepository(app).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
        val database = Room.databaseBuilder(
            application.applicationContext,
            ListaComprasDatabase::class.java, "ListaCompras.db"
        )
            .build()

        listaComprasRemoteDataSource = ListaComprasRemoteDataSource
        listaComprasLocalDataSource = ListaComprasLocalDataSource(database.listaComprasDao())
    }

    suspend fun getListaCompras(forceUpdate: Boolean = false): Result<List<ListaCompras>> {
        if (forceUpdate) {
            try {
                updateListaComprasFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }
        return listaComprasLocalDataSource.getListaCompras()
    }

    suspend fun refreshListaCompras() {
        updateListaComprasFromRemoteDataSource()
    }

    fun observeListaCompras(): LiveData<Result<List<ListaCompras>>> {
        return listaComprasLocalDataSource.observeListaCompras()
    }

    suspend fun refreshListaCompras(listaComprasId: String) {
        updateListaComprasFromRemoteDataSource(listaComprasId)
    }

    private suspend fun updateListaComprasFromRemoteDataSource() {
        val remoteListaCompras = listaComprasRemoteDataSource.getListaCompras()

        if (remoteListaCompras is Success) {
            // Real apps might want to do a proper sync.
            listaComprasLocalDataSource.deleteAllListaCompras()
            remoteListaCompras.data.forEach { listaCompra ->
                listaComprasLocalDataSource.saveListaCompras(listaCompra)
            }
        } else if (remoteListaCompras is Result.Error) {
            throw remoteListaCompras.exception
        }
    }

    fun observeListaCompras(listaComprasId: String): LiveData<Result<ListaCompras>> {
        return listaComprasLocalDataSource.observeListaCompras(listaComprasId)
    }

    private suspend fun updateListaComprasFromRemoteDataSource(listaComprasId: String) {
        val remoteListaCompras = listaComprasRemoteDataSource.getListaCompras(listaComprasId)

        if (remoteListaCompras is Success) {
            listaComprasLocalDataSource.saveListaCompras(remoteListaCompras.data)
        }
    }

    suspend fun getListaCompras(listaComprasID: String, forceUpdate: Boolean = false)
    : Result<ListaCompras> {
        if (forceUpdate) {
            updateListaComprasFromRemoteDataSource(listaComprasID)
        }
        return listaComprasLocalDataSource.getListaCompras(listaComprasID)
    }

    suspend fun saveListaCompras(listaCompras: ListaCompras) {
        coroutineScope {
            launch { listaComprasRemoteDataSource.saveListaCompras(listaCompras) }
            launch { listaComprasLocalDataSource.saveListaCompras(listaCompras) }
        }
    }

    suspend fun completeListaCompras(listaCompras: ListaCompras) {
        coroutineScope {
            launch { listaComprasRemoteDataSource.completeListaCompras(listaCompras) }
            launch { listaComprasLocalDataSource.completeListaCompras(listaCompras) }
        }
    }

    suspend fun completeListaCompras(listaComprasID: String) {
        withContext(ioDispatcher) {
            (getListaComprasWithId(listaComprasID) as? Success)?.let { it ->
                completeListaCompras(it.data)
            }
        }
    }

    suspend fun activateListaCompras(listaCompras: ListaCompras) = withContext<Unit>(ioDispatcher) {
        coroutineScope {
            launch { listaComprasRemoteDataSource.activateListaCompras(listaCompras) }
            launch { listaComprasLocalDataSource.activateListaCompras(listaCompras) }
        }
    }

    suspend fun activatelistaCompras(listaCompra: String) {
        withContext(ioDispatcher) {
            (getListaComprasWithId(listaCompra) as? Success)?.let { it ->
                completeListaCompras(it.data)
            }
        }
    }

    suspend fun clearCompletedListaCompras() {
        coroutineScope {
            launch { listaComprasRemoteDataSource.clearCompletedListaCompras() }
            launch { listaComprasLocalDataSource.clearCompletedListaCompras() }
        }
    }

    suspend fun deleteAllTListaCompras() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { listaComprasRemoteDataSource.deleteAllListaCompras() }
                launch { listaComprasLocalDataSource.deleteAllListaCompras() }
            }
        }
    }

    suspend fun deleteListaCompras(listaComprasId: String) {
        coroutineScope {
            launch { listaComprasRemoteDataSource.deleteListaCompras(listaComprasId) }
            launch { listaComprasLocalDataSource.deleteListaCompras(listaComprasId) }
        }
    }

    private suspend fun getListaComprasWithId(id: String): Result<ListaCompras> {
        return listaComprasLocalDataSource.getListaCompras(id)
    }
}