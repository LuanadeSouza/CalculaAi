package br.com.luanadev.calculacompra.data.source.produto.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.luanadev.calculacompra.data.entity.produto.Produtos

@Database(entities = [Produtos::class], version = 1, exportSchema = false)
abstract class ProdutoDatabase : RoomDatabase() {

    abstract fun produtoDao(): ProdutoDao
}
