package ap.mobile.myapplication.feature.healthcheck.data.model

enum class StoolColor(val label: String) {
    COKLAT("Coklat"),
    KUNING("Kuning"),
    HIJAU("Hijau"),
    PUTIH_PUCAT("Putih Pucat"),
    HITAM("Hitam"),
    BERDARAH("Berdarah");

    companion object {
        fun fromLabel(label: String): StoolColor? {
            return entries.find { it.label.equals(label, ignoreCase = true) }
        }
    }
}