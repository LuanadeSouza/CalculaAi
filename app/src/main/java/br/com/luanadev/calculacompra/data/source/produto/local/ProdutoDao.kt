package br.com.luanadev.calculacompra.data.source.produto.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.luanadev.calculacompra.data.entity.produto.Produtos

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM Produtos")
    fun observeProduto(): LiveData<List<Produtos>>

    @Query("SELECT * FROM Produtos WHERE produtoId = :produtoId")
    fun observeProdutoById(produtoId: String): LiveData<Produtos>

    @Query("SELECT * FROM Produtos")
    suspend fun getProduto(): List<Produtos>

    @Query("SELECT * FROM Produtos WHERE produtoId = :produtoId")
    suspend fun getProdutoById(produtoId: String): Produtos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduto(task: Produtos)

    @Query("DELETE FROM Produtos WHERE produtoId = :produtoId")
    suspend fun deleteProdutoById(produtoId: String): Int

    @Query("DELETE FROM Produtos")
    suspend fun deleteProdutos()
}
