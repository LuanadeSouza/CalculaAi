package br.com.luanadev.calculacompra.data.source.listacompra.local

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras

@Dao
interface ListaComprasDao {

    @Query("SELECT * FROM ListaCompras")
    fun observeListaCompra(): LiveData<List<ListaCompras>>

    @Query("SELECT * FROM ListaCompras WHERE listaComprasId = :listaCompraId")
    fun observeListaComprasById(listaCompraId: String): LiveData<ListaCompras>

    @Query("SELECT * FROM ListaCompras")
    suspend fun getListaCompras(): List<ListaCompras>

    @Query("SELECT * FROM ListaCompras WHERE listaComprasId = :listaCompraId")
    suspend fun getListaComprasById(listaCompraId: String): ListaCompras?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListaCompras(listaCompra: ListaCompras)

    @Update
    suspend fun updateListaCompra(listaCompra: ListaCompras): Int

    @Query("UPDATE ListaCompras SET completed = :completed WHERE listaComprasId = :listaComprasId")
    suspend fun updateCompleted(listaComprasId: String, completed: Boolean)

    @Query("DELETE FROM ListaCompras WHERE listaComprasId = :listaComprasId")
    suspend fun deleteListaComprasById(listaComprasId: String): Int

    @Query("DELETE FROM ListaCompras")
    suspend fun deleteListaCompras()

    @Query("DELETE FROM ListaCompras WHERE completed = 1")
    suspend fun deleteCompletedListaCompras()
}
