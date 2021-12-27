package br.com.luanadev.calculacompra.data.source.produto

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Error
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.entity.produto.Produtos
import br.com.luanadev.calculacompra.data.source.produto.local.ProdutoDatabase
import br.com.luanadev.calculacompra.data.source.produto.local.ProdutoLocalDataSource
import br.com.luanadev.calculacompra.data.source.produto.remote.ProdutoRemoteDataSource
import kotlinx.coroutines.*

class DefaultProdutoRepository(application: Application) {

    private val produtosRemoteDataSource: ProdutoDataSource
    private val produtosLocalDataSource: ProdutoDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: DefaultProdutoRepository? = null

        fun getRepository(app: Application): DefaultProdutoRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultProdutoRepository(app).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
        val database = Room.databaseBuilder(
            application.applicationContext,
            ProdutoDatabase::class.java, "Produtos.db"
        )
            .build()

        produtosRemoteDataSource = ProdutoRemoteDataSource
        produtosLocalDataSource = ProdutoLocalDataSource(database.produtoDao())
    }

    suspend fun getProduto(forceUpdate: Boolean = false): Result<List<Produtos>> {
        if (forceUpdate) {
            try {
                updateProdutosFromRemoteDataSource()
            } catch (ex: Exception) {
                return Error(ex)
            }
        }
        return produtosLocalDataSource.getProduto()
    }

    suspend fun refreshProduto() {
        updateProdutosFromRemoteDataSource()
    }

    fun observeProduto(): LiveData<Result<List<Produtos>>> {
        return produtosLocalDataSource.observeProduto()
    }

    suspend fun refreshProduto(produtoId: String) {
        updateProdutosFromRemoteDataSource(produtoId)
    }

    private suspend fun updateProdutosFromRemoteDataSource() {
        val remoteProdutos = produtosRemoteDataSource.getProduto()

        if (remoteProdutos is Success) {
            // Real apps might want to do a proper sync.
            produtosLocalDataSource.deleteAllProdutos()
            remoteProdutos.data.forEach { produto ->
                produtosLocalDataSource.saveProduto(produto)
            }
        } else if (remoteProdutos is Error) {
            throw remoteProdutos.exception
        }
    }

    fun observeProdutos(produtoId: String): LiveData<Result<Produtos>> {
        return produtosLocalDataSource.observeProduto(produtoId)
    }

    private suspend fun updateProdutosFromRemoteDataSource(produtoId: String) {
        val remoteProdutos = produtosRemoteDataSource.getProduto(produtoId)

        if (remoteProdutos is Success) {
            produtosLocalDataSource.saveProduto(remoteProdutos.data)
        }
    }

    suspend fun getProduto(produtoId: String, forceUpdate: Boolean = false): Result<Produtos> {
        if (forceUpdate) {
            updateProdutosFromRemoteDataSource(produtoId)
        }
        return produtosLocalDataSource.getProduto(produtoId)
    }

    suspend fun saveProdutos(produto: Produtos) {
        coroutineScope {
            launch { produtosRemoteDataSource.saveProduto(produto) }
            launch { produtosLocalDataSource.saveProduto(produto) }
        }
    }

    suspend fun deleteAllProdutos() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { produtosRemoteDataSource.deleteAllProdutos() }
                launch { produtosLocalDataSource.deleteAllProdutos() }
            }
        }
    }

    suspend fun deleteProdutos(produtosId: String) {
        coroutineScope {
            launch { produtosRemoteDataSource.deleteProduto(produtosId) }
            launch { produtosLocalDataSource.deleteProduto(produtosId) }
        }
    }

    private suspend fun getProdutosWithId(produtosId: String): Result<Produtos> {
        return produtosLocalDataSource.getProduto(produtosId)
    }
}