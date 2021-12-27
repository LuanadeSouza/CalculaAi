package br.com.luanadev.calculacompra.data.entity.produto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "produtos")
data class Produtos @JvmOverloads constructor(
    @ColumnInfo(name = "nome") var nome: String = "",
    @ColumnInfo(name = "quantidade") var quantidade: Int = 0,
    @ColumnInfo(name = "valor") var valor: Double = 0.0,
    @PrimaryKey @ColumnInfo(name = "produtoId") var produtoId: String = UUID.randomUUID().toString()

)