package ap.mobile.myapplication.feature.growth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ap.mobile.myapplication.feature.growth.data.model.GrowthTip
import ap.mobile.myapplication.feature.growth.data.model.MeasurementData
import ap.mobile.myapplication.feature.growth.data.model.StatusPertumbuhan
import coil.compose.rememberAsyncImagePainter
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyList: List<MeasurementData>,
    growthTips: List<GrowthTip>,
    onBackClick: () -> Unit,
    onItemClick: (MeasurementData) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val locale = remember { Locale.Builder().setLanguage("id").setRegion("ID").build() }
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Pengukuran",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (historyList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "📋",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum Ada Riwayat",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mulai analisis pertumbuhan bayi untuk melihat riwayat pengukuran",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Navigasi langsung ke halaman proses analisis
                            // NOTE: this navigation might not work as expected if "processAnalysisScreen" is not a valid route
                            // or if navController is not connected to the main graph. 
                            // Using the provided navController checks to trigger user action.
                            // However, since this is an internal nav controller, it won't actually navigate the app.
                            // The user should likely be guided back to home or a callback provided.
                            // For now, let's assume the user goes back to home to start.
                            onBackClick() // Temporary fallback as we don't have 'onStartAnalysis' callback
                        },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Kembali ke Menu")
                    }
                }
            }
        } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { 

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TipsAndUpdates,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Insight Pertumbuhan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ketuk tips untuk menampilkan snackbar detail",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            items(growthTips, key = { it.id }) { tip ->
                GrowthTipRow(
                    tip = tip,
                    modifier = Modifier.clickable {
                        scope.launch {
                            snackbarHostState.showSnackbar("${tip.title}: ${tip.detail}")
                        }
                    }
                )
            }
            item {
                Text(
                    text = "Riwayat Tersimpan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            items(historyList) { measurement ->
                HistoryItemCard(
                    measurement = measurement,
                    locale = locale,
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "${measurement.date} • ${String.format(locale, "%.1f", measurement.tinggiBadan)} cm"
                            )
                        }
                        onItemClick(measurement)
                    }
                )
            }
            } 
        }
    }
}

@Composable
private fun HistoryItemCard(
    measurement: MeasurementData,
    locale: Locale,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image Thumbnail dengan Border
                if (measurement.imageUri != null) {
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(measurement.imageUri),
                            contentDescription = "Thumbnail",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👶", fontSize = 40.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Measurement Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Date dengan Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = measurement.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Status dengan Icon dan Background
                    Surface(
                        color = getStatusColor(measurement.statusPertumbuhan).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getStatusIcon(measurement.statusPertumbuhan),
                                contentDescription = null,
                                tint = getStatusColor(measurement.statusPertumbuhan),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = getStatusText(measurement.statusPertumbuhan),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = getStatusColor(measurement.statusPertumbuhan)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(16.dp))
            
            // Measurements Summary dalam Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MeasurementSmallCard(
                    icon = "📏",
                    label = "Tinggi",
                    value = String.format(locale, "%.1f cm", measurement.tinggiBadan),
                    color = MaterialTheme.colorScheme.primary
                )
                
                MeasurementSmallCard(
                    icon = "⚖️",
                    label = "Berat",
                    value = String.format(locale, "%.1f kg", measurement.beratBadan),
                    color = Color(0xFF4CAF50) // Keep custom distinct color for weight
                )
                
                MeasurementSmallCard(
                    icon = "👶",
                    label = "Lingkar",
                    value = String.format(locale, "%.1f cm", measurement.lingkarKepala),
                    color = Color(0xFFFF9800) // Keep custom distinct color for head
                )
            }
        }
    }
}

@Composable
private fun MeasurementSmallCard(
    icon: String,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.15f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GrowthTipRow(
    tip: GrowthTip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = tip.emoji, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tip.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = tip.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getStatusIcon(status: StatusPertumbuhan): ImageVector {
    return when (status) {
        StatusPertumbuhan.NORMAL -> Icons.Default.CheckCircle
        StatusPertumbuhan.BERISIKO_STUNTING -> Icons.Default.Warning
        StatusPertumbuhan.STUNTING -> Icons.Default.Error
    }
}

private fun getStatusColor(status: StatusPertumbuhan): Color {
    return when (status) {
        StatusPertumbuhan.NORMAL -> Color(0xFF2E7D32)
        StatusPertumbuhan.BERISIKO_STUNTING -> Color(0xFFE65100)
        StatusPertumbuhan.STUNTING -> Color(0xFFC62828)
    }
}

private fun getStatusText(status: StatusPertumbuhan): String {
    return when (status) {
        StatusPertumbuhan.NORMAL -> "Normal"
        StatusPertumbuhan.BERISIKO_STUNTING -> "Berisiko"
        StatusPertumbuhan.STUNTING -> "Stunting"
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    val sampleHistory = listOf(
        MeasurementData(
            date = "12 Jan 2025",
            tinggiBadan = 64.5f,
            lingkarKepala = 41.0f,
            beratBadan = 7.8f,
            statusPertumbuhan = StatusPertumbuhan.NORMAL,
            imageUri = null
        )
    )
    val sampleTips = listOf(
        GrowthTip("tip-preview", "Nutrisi", "Tambahkan puree alpukat", "🥑")
    )
    HistoryScreen(
        historyList = sampleHistory,
        growthTips = sampleTips,
        onBackClick = {},
        onItemClick = {},
        modifier = Modifier.fillMaxSize()
    )
}
