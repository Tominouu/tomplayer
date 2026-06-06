package com.tomplayer.app.ui.search

import androidx.lifecycle.ViewModel
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.domain.usecase.SearchAllUseCase
import com.tomplayer.app.domain.usecase.SearchResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel(
    private val searchAllUseCase: SearchAllUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Channel>>(emptyList())
    val results: StateFlow<List<Channel>> = _results.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        if (newQuery.length >= 2) {
            _isSearching.value = true
            _results.value = searchAllUseCase(newQuery).channels
            _isSearching.value = false
        } else {
            _results.value = emptyList()
        }
    }

    fun clearSearch() {
        _query.value = ""
        _results.value = emptyList()
    }
}
