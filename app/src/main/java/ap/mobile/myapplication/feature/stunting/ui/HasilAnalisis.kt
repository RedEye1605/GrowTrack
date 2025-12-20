package ap.mobile.myapplication.feature.stunting.ui


import ap.mobile.myapplication.feature.stunting.data.model.ResultSummary


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HasilAnalisis(
   navController: NavController,
   gender: String,
   usiaBulan: Int,
   beratBadan: Float,
   tinggiBadan: Float
) {
   val zScore = remember(usiaBulan, beratBadan, tinggiBadan) {
       val tinggiStandar = when (gender) {
           "Laki-laki" -> 75 + 0.5f * usiaBulan
           "Perempuan" -> 74 + 0.5f * usiaBulan
           else -> 75f
       }
       val sd = 3f
       (tinggiBadan - tinggiStandar) / sd
   }


   val kategori = when {
       zScore >= -2f -> "Normal"
       zScore < -2f && zScore >= -3f -> "Risiko Stunting"
       else -> "Stunting"
   }


   val kalimatAnalisis = when (kategori) {
       "Normal" -> "Pertumbuhan anak berada dalam kategori Normal, sesuai dengan standar tinggi badan menurut usia. Tidak terdapat tanda-tanda stunting."
       "Risiko Stunting" -> "Pertumbuhan anak berada dalam kategori Risiko Stunting. Perlu perhatian lebih pada pola makan dan pemantauan pertumbuhan."
       "Stunting" -> "Pertumbuhan anak berada dalam kategori Stunting. Disarankan untuk segera berkonsultasi dengan tenaga kesehatan."
       else -> ""
   }


   val rekomendasi = listOf(
       "Pertahankan pola makan bergizi seimbang setiap hari.",
       "Rutin melakukan pemantauan pertumbuhan minimal satu bulan sekali.",
       "Pastikan anak mendapatkan waktu istirahat yang cukup dan bermain aktif.",
       "Berikan ASI atau susu sesuai kebutuhan usia.",
       "Lakukan pemeriksaan rutin di posyandu/puskesmas untuk memantau perkembangan selanjutnya."
   )


   Scaffold(
       topBar = {
           CenterAlignedTopAppBar(
               title = { Text("Hasil Analisis") },
               navigationIcon = {
                   IconButton(onClick = { navController.popBackStack() }) {
                       Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                   }
               }
           )
       }
   ) { paddingValues ->


       LazyColumn(
           modifier = Modifier
               .fillMaxSize()
               .padding(paddingValues)
               .padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(16.dp)
       ) {
           item {
               Card(
                   colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                   shape = RoundedCornerShape(12.dp),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Column(
                       modifier = Modifier.padding(16.dp),
                       verticalArrangement = Arrangement.spacedBy(8.dp)
                   ) {
                       Text(
                           text = "Z-score: %.2f".format(zScore),
                           fontSize = 18.sp,
                           fontWeight = FontWeight.Medium
                       )


                       Card(
                           colors = CardDefaults.cardColors(
                               containerColor = when (kategori) {
                                   "Normal" -> Color(0xFF4CAF50)
                                   "Risiko Stunting" -> Color(0xFFFFC107)
                                   "Stunting" -> Color(0xFFF44336)
                                   else -> Color.Gray
                               }
                           ),
                           shape = RoundedCornerShape(8.dp),
                           modifier = Modifier.fillMaxWidth()
                       ) {
                           Text(
                               text = kategori,
                               color = Color.White,
                               fontSize = 16.sp,
                               fontWeight = FontWeight.Bold,
                               modifier = Modifier.padding(8.dp)
                           )
                       }


                       Text(text = kalimatAnalisis, fontSize = 16.sp)
                   }
               }
           }


           item {
               Card(
                   colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                   shape = RoundedCornerShape(12.dp),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Column(
                       modifier = Modifier.padding(16.dp),
                       verticalArrangement = Arrangement.spacedBy(8.dp)
                   ) {
                       Text(text = "Rekomendasi:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                       rekomendasi.forEach { rec ->
                           Text("• $rec", fontSize = 16.sp)
                       }
                   }
               }
           }


           item {
               Spacer(modifier = Modifier.height(8.dp))


               Button(
                   onClick = {
                       navController.previousBackStackEntry
                           ?.savedStateHandle
                           ?.set(
                               "hasilRingkas",
                               ResultSummary(
                                   zScore = zScore,
                                   kategori = kategori,
                                   analisis = kalimatAnalisis,
                                   rekomendasi = rekomendasi
                               )
                           )
                       navController.popBackStack()
                   },
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text("Oke")
               }
           }
       }
   }
}