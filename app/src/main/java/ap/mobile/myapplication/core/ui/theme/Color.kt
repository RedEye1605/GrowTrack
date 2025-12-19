package ap.mobile.myapplication.core.ui.theme

import androidx.compose.ui.graphics.Color
val PrimaryBlue = Color(0xFF4A90E2)
val PrimaryVariant = Color(0xFF357ABD)
val SecondaryPink = Color(0xFFFF80AB)
val SecondaryOrange = Color(0xFFFFB74D)

val BackgroundLight = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF2D3436)
val TextSecondary = Color(0xFF636E72)

// Status Colors
val StatusSuccess = Color(0xFF00C853)
val StatusWarning = Color(0xFFFFD600)
val StatusError = Color(0xFFD50000)
val StatusDanger = StatusError

// Legacy Mappings for compatibility (mapped to new palette)
val Purple80 = PrimaryBlue.copy(alpha = 0.8f)
val PurpleGrey80 = TextSecondary
val Pink80 = SecondaryPink

val Purple40 = PrimaryBlue
val PurpleGrey40 = TextSecondary
val Pink40 = SecondaryPink