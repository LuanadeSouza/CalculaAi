package br.com.luanadev.calculacompra.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "produtos")
data class Produtos @JvmOverloads constructor(
    @ColumnInfo(name = "tipo") var tipo: String = "",
    @ColumnInfo(name = "quantidade") var quantidade: Int = 0,
    @ColumnInfo(name = "valor") var valor: Double = 0.0,
    @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
    @PrimaryKey @ColumnInfo(name = "itemId") var itemId: String = UUID.randomUUID().toString()

)