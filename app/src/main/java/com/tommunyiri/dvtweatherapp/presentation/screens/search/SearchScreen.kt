package com.tommunyiri.dvtweatherapp.presentation.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.algolia.instantsearch.android.paging3.flow
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.SearchResult
import kotlinx.coroutines.launch

/**
 * Composable function that represents the search screen of the application.
 */
@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {
    Column(modifier = Modifier.padding(top = 45.dp, bottom = 80.dp)) {
        val paginator = viewModel.hitsPaginator
        val searchBoxState = viewModel.searchBoxState
        val scope = rememberCoroutineScope()
        val pagingHits = paginator.flow.collectAsLazyPagingItems()
        val listState = rememberLazyListState()
        SearchBox(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            searchBoxState = searchBoxState,
            onValueChange = { scope.launch { listState.scrollToItem(0) } },
        )
        SearchResultList(
            modifier = Modifier
                .fillMaxSize(),
            pagingHits = pagingHits,
            listState = listState
        )
    }
}

@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    searchBoxState: SearchBoxState = SearchBoxState(),
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier,
        // set query as text value
        value = searchBoxState.query,
        // update text on value change
        onValueChange = {
            searchBoxState.setText(it)
            onValueChange(it)
        },
        // set ime action to "search"
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        // set text as query submit on search action
        keyboardActions = KeyboardActions(
            onSearch = { searchBoxState.setText(searchBoxState.query, true) }
        ),
        placeholder = {
            Text(text = stringResource(id = R.string.enter_city_text))
        },
        maxLines = 1,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        }
    )
}

@Composable
fun SearchResultList(
    modifier: Modifier = Modifier,
    pagingHits: LazyPagingItems<SearchResult>,
    listState: LazyListState
) {
    LazyColumn(modifier, listState) {
        items(pagingHits.itemCount) { item ->
            val searchItem = pagingHits[item]
            if (searchItem != null) {
                Text(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    text = "${searchItem.name}, ${searchItem.country}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp)
            )
        }
    }
}