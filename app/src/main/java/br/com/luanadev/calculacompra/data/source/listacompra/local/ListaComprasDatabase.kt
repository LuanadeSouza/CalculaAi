package br.com.luanadev.calculacompra.data.source.listacompra.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras

@Database(entities = [ListaCompras::class], version = 1, exportSchema = false)
abstract class ListaComprasDatabase : RoomDatabase() {

    abstract fun listaComprasDao(): ListaComprasDao
}
