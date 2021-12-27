package br.com.luanadev.calculacompra.presentation.listacompra

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import br.com.luanadev.calculacompra.R
import br.com.luanadev.calculacompra.data.Result
import br.com.luanadev.calculacompra.data.Result.Success
import br.com.luanadev.calculacompra.data.entity.listacompra.ListaCompras
import br.com.luanadev.calculacompra.data.source.listacompra.DefaultListaComprasRepository
import br.com.luanadev.calculacompra.util.Event
import kotlinx.coroutines.launch

class ListaComprasViewModel(application: Application) : AndroidViewModel(application) {

    private val listaComprasRepository = DefaultListaComprasRepository.getRepository(application)

    private val _forceUpdate = MutableLiveData(false)

    private val _items: LiveData<List<ListaCompras>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                listaComprasRepository.refreshListaCompras()
                _dataLoading.value = false
            }
        }
        listaComprasRepository.observeListaCompras().switchMap { filterlistaCompras(it) }

    }

    val items: LiveData<List<ListaCompras>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noListaComprasLabel = MutableLiveData<Int>()
    val noListaComprasLabel: LiveData<Int> = _noListaComprasLabel

    private val _noListaComprasIconRes = MutableLiveData<Int>()
    val noListaComprasIconRes: LiveData<Int> = _noListaComprasIconRes

    private val _listaComprasAddViewVisible = MutableLiveData<Boolean>()
    val listaComprasAddViewVisible: LiveData<Boolean> = _listaComprasAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var currentFiltering = ListaComprasFilterType.ALL_LISTA_COMPRAS

    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openListaComprasEvent = MutableLiveData<Event<String>>()
    val openListaComprasEvent: LiveData<Event<String>> = _openListaComprasEvent

    private val _newListaComprasEvent = MutableLiveData<Event<Unit>>()
    val newListaComprasEvent: LiveData<Event<Unit>> = _newListaComprasEvent

    private var resultMessageShown: Boolean = false

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        // Set initial state
        setFiltering(ListaComprasFilterType.ALL_LISTA_COMPRAS)
        loadListaCompras(true)
    }

    fun setFiltering(requestType: ListaComprasFilterType) {
        currentFiltering = requestType

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            ListaComprasFilterType.ALL_LISTA_COMPRAS -> {
                setFilter(
                    R.string.label_all, R.string.no_lista_compras_all,
                    R.drawable.logo_no_fill, true
                )
            }
            ListaComprasFilterType.ACTIVE_LISTA_COMPRAS -> {
                setFilter(
                    R.string.label_active, R.string.no_lista_compras_active,
                    R.drawable.ic_check, false
                )
            }
            ListaComprasFilterType.COMPLETED_LISTA_COMPRAS -> {
                setFilter(
                    R.string.label_completed, R.string.no_lista_compras_completed,
                    R.drawable.ic_verified_user, false
                )
            }
        }
        // Refresh list
        loadListaCompras(false)
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int, @StringRes noListaComprasLabelString: Int,
        @DrawableRes noListaComprasIconDrawable: Int, listaComprasAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noListaComprasLabel.value = noListaComprasLabelString
        _noListaComprasIconRes.value = noListaComprasIconDrawable
        _listaComprasAddViewVisible.value = listaComprasAddVisible
    }

    fun clearCompletedListaCompras() {
        viewModelScope.launch {
            listaComprasRepository.clearCompletedListaCompras()
            showSnackbarMessage(R.string.completed_lista_compras_cleared)
        }
    }

    fun completeListaCompras(listaCompras: ListaCompras, completed: Boolean) =
        viewModelScope.launch {
            if (completed) {
                listaComprasRepository.completeListaCompras(listaCompras)
                showSnackbarMessage(R.string.lista_compras_marked_complete)
            } else {
                listaComprasRepository.activateListaCompras(listaCompras)
                showSnackbarMessage(R.string.lista_compras_marked_active)
            }
        }

    fun addNewListaCompras() {
        _newListaComprasEvent.value = Event(Unit)
    }

    fun openListaCompras(listaComprasId: String) {
        _openListaComprasEvent.value = Event(listaComprasId)
    }

    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_lista_compras_message)
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_lista_compras_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_lista_compras_message)
        }
        resultMessageShown = true
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun filterlistaCompras(listaComprasResult: Result<List<ListaCompras>>): LiveData<List<ListaCompras>> {
        val result = MutableLiveData<List<ListaCompras>>()

        if (listaComprasResult is Success) {
            isDataLoadingError.value = false
            viewModelScope.launch {
                result.value = filterItems(listaComprasResult.data, currentFiltering)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.loading_lista_compras_error)
            isDataLoadingError.value = true
        }

        return result
    }

    fun loadListaCompras(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    private fun filterItems(listaCompras: List<ListaCompras>, filteringType: ListaComprasFilterType
    ): List<ListaCompras> {
        val listaComprasToShow = ArrayList<ListaCompras>()
        for (listaCompra in listaCompras) {
            when (filteringType) {
                ListaComprasFilterType.ALL_LISTA_COMPRAS -> listaComprasToShow.add(listaCompra)
                ListaComprasFilterType.ACTIVE_LISTA_COMPRAS -> if (listaCompra.isActive) {
                    listaComprasToShow.add(listaCompra)
                }
                ListaComprasFilterType.COMPLETED_LISTA_COMPRAS -> if (listaCompra.isCompleted) {
                    listaComprasToShow.add(listaCompra)
                }
            }
        }
        return listaComprasToShow
    }

    fun refresh() {
        _forceUpdate.value = true
    }
}