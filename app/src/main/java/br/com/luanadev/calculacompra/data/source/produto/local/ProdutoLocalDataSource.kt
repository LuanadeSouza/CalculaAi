package br.com.luanadev.calculacompra.data.source.produto.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import br.com.luanadev.calculacompra.data.entity.produto.Produtos
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Error
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.source.produto.ProdutoDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ProdutoLocalDataSource internal constructor(
    private val produtoDao: ProdutoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProdutoDataSource {

    override fun observeProduto(): LiveData<Result<List<Produtos>>> {
        return produtoDao.observeProduto().map {
            Success(it)
        }
    }

    override fun observeProduto(produtoId: String): LiveData<Result<Produtos>> {
        return produtoDao.observeProdutoById(produtoId).map {
            Success(it)
        }
    }

    override suspend fun refreshProduto(produtoId: String) {
    }

    override suspend fun refreshProduto() {
    }

    override suspend fun getProduto(): Result<List<Produtos>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(produtoDao.getProduto())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getProduto(produtoId: String): Result<Produtos> =
        withContext(ioDispatcher) {
            try {
                val produto = produtoDao.getProdutoById(produtoId)
                if (produto != null) {
                    return@withContext Success(produto)
                } else {
                    return@withContext Error(Exception("Produto not found!"))
                }
            } catch (e: Exception) {
                return@withContext Error(e)
            }
        }

    override suspend fun saveProduto(produto: Produtos) = withContext(ioDispatcher) {
        produtoDao.insertProduto(produto)
    }

    override suspend fun deleteProduto(produtoId: String) = withContext<Unit>(ioDispatcher) {
        produtoDao.deleteProdutoById(produtoId)
    }

    override suspend fun deleteAllProdutos() = withContext(ioDispatcher) {
        produtoDao.deleteProdutos()
    }
}
