package ap.mobile.myapplication.feature.nutrition.ui
import ap.mobile.myapplication.feature.nutrition.viewmodel.*

import ap.mobile.myapplication.navigation.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ap.mobile.myapplication.core.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahMenuScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TambahMenuViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Menu",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Tambah Menu", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = uiState.menu, onValueChange = viewModel::onMenuChange, label = { Text("Menu") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.protein, onValueChange = viewModel::onProteinChange, label = { Text("Protein") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.lemak, onValueChange = viewModel::onLemakChange, label = { Text("Lemak") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.karbohidrat, onValueChange = viewModel::onKarbohidratChange, label = { Text("Karbohidrat") }, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.addMenu()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = "Tambah Menu")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TambahMenuScreenPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        TambahMenuScreen(navController = navController)
    }
}
