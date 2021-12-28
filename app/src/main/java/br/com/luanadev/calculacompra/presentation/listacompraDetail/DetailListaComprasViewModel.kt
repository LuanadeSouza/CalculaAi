package br.com.luanadev.calculacompra.presentation.listacompraDetail

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import br.com.luanadev.calculacompra.R
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.data.source.listacompra.DefaultListaComprasRepository
import br.com.luanadev.calculacompra.util.Event
import kotlinx.coroutines.launch

class DetailListaComprasViewModel (application: Application) : AndroidViewModel(application) {

    private val listaComprasRepository = DefaultListaComprasRepository.getRepository(application)

    private val _listaComprasId = MutableLiveData<String>()

    private val _listaCompras = _listaComprasId.switchMap { listaComprasId ->
        listaComprasRepository.observeListaCompras(listaComprasId).map { computeResult(it) }
    }
    val listaCompras: LiveData<ListaCompras?> = _listaCompras

    val isDataAvailable: LiveData<Boolean> = _listaCompras.map { it != null }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editListaComprasEvent = MutableLiveData<Event<Unit>>()
    val editListaComprasEvent: LiveData<Event<Unit>> = _editListaComprasEvent

    private val _deleteListaComprasEvent = MutableLiveData<Event<Unit>>()
    val deleteListaCompraskEvent: LiveData<Event<Unit>> = _deleteListaComprasEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    // This LiveData depends on another so we can use a transformation.
    val completed: LiveData<Boolean> = _listaCompras.map { input: ListaCompras? ->
        input?.isCompleted ?: false
    }

    fun deleteListaCompras() = viewModelScope.launch {
        _listaComprasId.value?.let {
            listaComprasRepository.deleteListaCompras(it)
            _deleteListaComprasEvent.value = Event(Unit)
        }
    }

    fun editListaCompras() {
        _editListaComprasEvent.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val listaCompras = _listaCompras.value ?: return@launch
        if (completed) {
            listaComprasRepository.completeListaCompras(listaCompras)
            showSnackbarMessage(R.string.lista_compras_marked_complete)
        } else {
            listaComprasRepository.activateListaCompras(listaCompras)
            showSnackbarMessage(R.string.lista_compras_marked_active)
        }
    }

    fun start(listaComprasId: String) {
        // If we're already loading or already loaded, return (might be a config change)
        if (_dataLoading.value == true || listaComprasId == _listaComprasId.value) {
            return
        }
        // Trigger the load
        _listaComprasId.value = listaComprasId
    }

    private fun computeResult(listaComprasResult: Result<ListaCompras>): ListaCompras? {
        return if (listaComprasResult is Success) {
            listaComprasResult.data
        } else {
            showSnackbarMessage(R.string.loading_lista_compras_error)
            null
        }
    }


    fun refresh() {
        // Refresh the repository and the task will be updated automatically.
        _listaCompras.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                listaComprasRepository.refreshListaCompras(it.listaComprasId)
                _dataLoading.value = false
            }
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
