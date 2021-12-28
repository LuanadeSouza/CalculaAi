package br.com.luanadev.calculacompra.presentation.addlistacompras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.luanadev.calculacompra.R
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.data.source.listacompra.DefaultListaComprasRepository
import br.com.luanadev.calculacompra.util.Event
import kotlinx.coroutines.launch

class AddListaComprasViewModel (application: Application) : AndroidViewModel(application) {

    // Note, for testing and architecture purposes, it's bad practice to construct the repository
    // here. We'll show you how to fix this during the codelab
    private val listaComprasRepository = DefaultListaComprasRepository.getRepository(application)

    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _listaComprasUpdatedEvent = MutableLiveData<Event<Unit>>()
    val listaComprasUpdatedEvent: LiveData<Event<Unit>> = _listaComprasUpdatedEvent

    private var listaComprasId: String? = null

    private var isNewListaCompras: Boolean = false

    private var isDataLoaded = false

    private var listaComprasCompleted = false

    fun start(listaComprasId: String?) {
        if (_dataLoading.value == true) {
            return
        }

        this.listaComprasId = listaComprasId
        if (listaComprasId == null) {
            // No need to populate, it's a new listaCompras
            isNewListaCompras = true
            return
        }
        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        isNewListaCompras = false
        _dataLoading.value = true

        viewModelScope.launch {
            listaComprasRepository.getListaCompras(listaComprasId).let { result ->
                if (result is Result.Success) {
                    onListaComprasLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onListaComprasLoaded(listaCompras: ListaCompras) {
        title.value = listaCompras.title
        description.value = listaCompras.description
        listaComprasCompleted = listaCompras.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    // Called when clicking on fab.
    fun saveListaCompras() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_lista_compras_message)
            return
        }
        if (ListaCompras(currentTitle, currentDescription).isEmpty) {
            _snackbarText.value = Event(R.string.empty_lista_compras_message)
            return
        }

        val currentListaCompraskId = listaComprasId
        if (isNewListaCompras || currentListaCompraskId == null) {
            createListaCompras(ListaCompras(currentTitle, currentDescription))
        } else {
            val listaCompras = ListaCompras(currentTitle, currentDescription, listaComprasCompleted, currentListaCompraskId)
            updateListaCompras(listaCompras)
        }
    }

    private fun createListaCompras(newListaCompras: ListaCompras) = viewModelScope.launch {
        listaComprasRepository.saveListaCompras(newListaCompras)
        _listaComprasUpdatedEvent.value = Event(Unit)
    }

    private fun updateListaCompras(listaCompras: ListaCompras) {
        if (isNewListaCompras) {
            throw RuntimeException("updateListaCompras() was called but listaCompras is new.")
        }
        viewModelScope.launch {
            listaComprasRepository.saveListaCompras(listaCompras)
            _listaComprasUpdatedEvent.value = Event(Unit)
        }
    }
}
