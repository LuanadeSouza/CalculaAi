package br.com.luanadev.calculacompra.data.entity.listacompra

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "listaCompras")
data class ListaCompras @JvmOverloads constructor(
    @ColumnInfo(name = "nome") var nome: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
    @PrimaryKey @ColumnInfo(name = "listaComprasId") var listaComprasId: String = UUID.randomUUID()
        .toString()

) {
    val titleForList: String
        get() = if (nome.isNotEmpty()) nome else description


    val isActive
        get() = !isCompleted

    val isEmpty
        get() = nome.isEmpty() || description.isEmpty()
}
