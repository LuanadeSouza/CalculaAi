package br.com.luanadev.calculacompra.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.luanadev.calculacompra.data.Produtos

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM Produtos")
    fun observeProduto(): LiveData<List<Produtos>>

    @Query("SELECT * FROM Produtos WHERE itemId = :itemId")
    fun observeProdutoById(itemId: String): LiveData<Produtos>

    @Query("SELECT * FROM Produtos")
    suspend fun getProduto(): List<Produtos>

    @Query("SELECT * FROM Produtos WHERE itemId = :itemId")
    suspend fun getProdutoById(itemId: String): Produtos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduto(task: Produtos)

    @Update
    suspend fun updateListaCompra(produto: Produtos): Int

    @Query("UPDATE Produtos SET completed = :completed WHERE itemId = :itemId")
    suspend fun updateCompleted(itemId: String, completed: Boolean)

    @Query("DELETE FROM Produtos WHERE itemId = :itemId")
    suspend fun deleteProdutoById(itemId: String): Int

    @Query("DELETE FROM Produtos")
    suspend fun deleteProdutos()

    @Query("DELETE FROM Produtos")
    suspend fun deleteListaCompras()

    @Query("DELETE FROM Produtos WHERE completed = 1")
    suspend fun deleteCompletedListaCompras()

    @Query("DELETE FROM Produtos WHERE itemId = :itemId")
    suspend fun deleteListaComprasById(itemId: String): Int
}
