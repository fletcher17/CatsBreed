package com.example.catsbreed.presentation.breedlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.presentation.components.EmptyState
import com.example.catsbreed.presentation.components.FavouriteToggleButton
import com.example.catsbreed.presentation.components.FullScreenError
import com.example.catsbreed.presentation.components.FullScreenLoading
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedListScreen(
    onBreedClick: (String) -> Unit,
) {
    val viewModel = koinViewModel<BreedListViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cat Breeds") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                isSearching = uiState.isSearching
            )

            when {
                uiState.isInitialLoading -> FullScreenLoading()
                uiState.errorMessage != null && uiState.breeds.isEmpty() ->
                    FullScreenError(message = uiState.errorMessage!!, onRetry = viewModel::retry)
                uiState.isEmpty ->
                    EmptyState(
                        message = if (uiState.searchQuery.isNotBlank())
                            "No breeds match \"${uiState.searchQuery}\"."
                        else "No breeds available."
                    )
                else -> BreedList(
                    breeds = uiState.breeds,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = viewModel::loadNextPage,
                    onBreedClick = onBreedClick,
                    onToggleFavourite = viewModel::onToggleFavourite,
                    canPaginate = uiState.searchQuery.isBlank()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search breeds...") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            when {
                isSearching -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
                query.isNotEmpty() -> IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                }
            }
        }
    )
}

@Composable
private fun BreedList(
    breeds: List<Breed>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onBreedClick: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
    canPaginate: Boolean
) {
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(breeds, key = { it.id }) { breed ->
            BreedListItemRow(
                breed = breed,
                onClick = { onBreedClick(breed.id) },
                onToggleFavourite = { onToggleFavourite(breed.id) }
            )
        }
        if (isLoadingMore) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Trigger pagination when the user scrolls near the end of the list.
    val shouldLoadMore = remember {
        androidx.compose.runtime.derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            canPaginate && lastVisible >= breeds.size - 5
        }
    }
    val loadMore by shouldLoadMore
    androidx.compose.runtime.LaunchedEffect(loadMore) {
        if (loadMore) onLoadMore()
    }
}

@Composable
fun BreedListItemRow(
    breed: Breed,
    onClick: () -> Unit,
    onToggleFavourite: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(Modifier.clickableRow(onClick))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = breed.imageUrl,
            contentDescription = "Photo of ${breed.name}",
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(text = breed.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = breed.origin.ifBlank { "Unknown origin" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onToggleFavourite != null) {
            FavouriteToggleButton(isFavourite = breed.isFavourite, onToggle = onToggleFavourite)
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)