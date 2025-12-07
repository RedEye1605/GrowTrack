package ap.mobile.myapplication.ui.screens

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
import ap.mobile.myapplication.data.GrowthTip
import ap.mobile.myapplication.data.MeasurementData
import ap.mobile.myapplication.data.StatusPertumbuhan
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
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                    .background(Color(0xFFF5F7FA))
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
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mulai analisis pertumbuhan bayi untuk melihat riwayat pengukuran",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Navigasi langsung ke halaman proses analisis
                            navController.navigate("processAnalysisScreen")
                        },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Mulai Analisis")
                    }
                }
            }
        } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { // The content lambda for LazyColumn starts here

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TipsAndUpdates,
                            contentDescription = null,
                            tint = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Insight Pertumbuhan",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ketuk tips untuk menampilkan snackbar detail",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF757575)
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
                    modifier = Modifier.padding(top = 8.dp)
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
            } // The content lambda for LazyColumn ends here
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
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
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
                        modifier = Modifier.size(100.dp),
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👶", fontSize = 48.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
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
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Status dengan Icon dan Background
                    Surface(
                        color = getStatusColor(measurement.statusPertumbuhan).copy(alpha = 0.15f),
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
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = getStatusText(measurement.statusPertumbuhan),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = getStatusColor(measurement.statusPertumbuhan)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = Color(0xFFE0E0E0))

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
                    color = Color(0xFF2196F3)
                )
                
                MeasurementSmallCard(
                    icon = "⚖️",
                    label = "Berat",
                    value = String.format(locale, "%.1f kg", measurement.beratBadan),
                    color = Color(0xFF4CAF50)
                )
                
                MeasurementSmallCard(
                    icon = "👶",
                    label = "Lingkar",
                    value = String.format(locale, "%.1f cm", measurement.lingkarKepala),
                    color = Color(0xFFFF9800)
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
            fontSize = 11.sp,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                color = Color(0xFFE3F2FD)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = tip.emoji, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tip.title, fontWeight = FontWeight.SemiBold)
                Text(
                    text = tip.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF616161)
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
