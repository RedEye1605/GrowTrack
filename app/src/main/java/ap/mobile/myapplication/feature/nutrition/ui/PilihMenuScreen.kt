package ap.mobile.myapplication.feature.nutrition.ui
import ap.mobile.myapplication.feature.nutrition.viewmodel.*

import ap.mobile.myapplication.navigation.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import ap.mobile.myapplication.feature.nutrition.data.model.FoodItem
import ap.mobile.myapplication.core.ui.theme.MyApplicationTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihMenuScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    pilihMenuViewModel: PilihMenuViewModel = viewModel(),
    sharedViewModel: AnalisisKaloriViewModel
) {
    val foodItems by pilihMenuViewModel.foodItems.collectAsState()
    val searchQuery by pilihMenuViewModel.searchQuery.collectAsState()
    val selectedItems by sharedViewModel.selectedItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pilih Menu",
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
        ) {
            DateSelector(navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(value = searchQuery, onValueChange = pilihMenuViewModel::onSearchQueryChanged)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f) // <-- This is the fix

            ) {
                items(foodItems, key = { it.id }) { item ->
                    FoodItemRow(
                        item = item,
                        isSelected = selectedItems.any { it.id == item.id },
                        onItemSelected = { sharedViewModel.toggleFoodSelection(item) },
                        onDelete = { pilihMenuViewModel.deleteFoodItem(item) } // Hubungkan ke ViewModel
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Added space before buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate(Screen.TambahMenu.route) }, modifier = Modifier.weight(1f)) {
                    Text(text = "Tambah Menu")
                }
                Button(onClick = { navController.navigate(Screen.AnalisisKalori.route) }, modifier = Modifier.weight(1f)) {
                    Text(text = "Analisis")
                }
            }
        }
    }
}

@Composable
fun DateSelector(modifier: Modifier = Modifier, navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Previous Day")
        }
        Text(text = "Hari Ini, 15 Jan 2025", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        IconButton(onClick = { navController.navigate(Screen.PilihMenu.route) }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Next Day")
        }
    }
}

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Cari makanan...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun FoodItemRow(
    item: FoodItem,
    isSelected: Boolean,
    onItemSelected: () -> Unit,
    onDelete: () -> Unit // Tambahkan parameter ini
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = "Image of ${item.name}",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, fontWeight = FontWeight.SemiBold)
            Text(text = "${item.kkal} kkal", fontSize = 12.sp, color = Color.Gray)
        }

        // Tombol Hapus (Tong Sampah)
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Tombol Tambah/Check
        IconButton(onClick = onItemSelected, modifier = Modifier.size(32.dp)) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).padding(4.dp)
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Select",
                    tint = Color.Gray,
                    modifier = Modifier.background(Color.LightGray, CircleShape).padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PilihMenuScreenPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        PilihMenuScreen(navController = navController, sharedViewModel = viewModel())
    }
}