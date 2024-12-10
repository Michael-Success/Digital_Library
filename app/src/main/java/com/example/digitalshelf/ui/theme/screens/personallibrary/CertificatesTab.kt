package com.example.digitalshelf.ui.theme.screens.personallibrary



import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digitalshelf.models.Resource
import com.example.digitalshelf.ui.theme.screens.generalhomescreen.ResourceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesTab(resources: List<Resource>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(resources) { resource ->
            ResourceCard(resource)
        }
    }
}
