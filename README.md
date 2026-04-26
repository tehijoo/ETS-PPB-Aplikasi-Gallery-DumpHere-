# DumpHere! - Aplikasi Gallery Android

DumpHere! adalah aplikasi galeri Android modern yang dibangun dengan **Kotlin** dan **Jetpack Compose**. Aplikasi ini memungkinkan pengguna untuk mengelola, melihat, dan berbagi foto dengan antarmuka yang intuitif dan responsif.

## Fitur Utama

- **Galeri Foto** - Tampilkan foto dalam grid layout yang responsif
- **Kamera Terintegrasi** - Ambil foto langsung dari aplikasi menggunakan CameraX
- **Pratinjau Gambar** - Lihat detail gambar dengan pratinjau penuh layar
- **Navigasi Halaman** - Navigasi antar halaman foto menggunakan horizontal pager
- **Material Design 3** - Desain modern dengan Material 3 design system
- **Performa Tinggi** - Dioptimalkan untuk kecepatan dan responsivitas
- **Kompatibilitas Luas** - Mendukung Android 7.0 (API 24) ke atas

## Teknologi yang Digunakan

### Framework & Libraries
- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - Modern UI toolkit untuk Android
- **Material 3** - Design system terbaru dari Google
- **CameraX** - Library untuk menangani akses kamera
- **Coil** - Efficient image loading library
- **AndroidX** - Android compatibility libraries

### Versi Target
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **Java Compatibility**: 11

## Prerequisites

Sebelum memulai, pastikan Anda memiliki:

- **Android Studio** - versi terbaru (Arctic Fox atau lebih baru)
- **JDK 11** atau lebih tinggi
- **Gradle** - versi terbaru (sudah termasuk dalam Android Studio)
- **Android SDK** - API level 36 dan Android 7.0+ untuk testing
- **Git** - untuk version control (opsional)

## Instalasi & Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd ETS-PPB-Aplikasi-Gallery-DumpHere
```

### 2. Buka Project di Android Studio
```bash
# Buka Android Studio dan pilih "Open an Existing Project"
# Navigate ke folder project yang telah di-clone
```

### 3. Sinkronisasi Dependencies
```bash
# Gradle akan otomatis download dependencies saat membuka project
# Atau jalankan manual:
./gradlew build
```

### 4. Verifikasi Setup
- Pastikan SDK Manager memiliki Android 15 (API 36) dan Android 7.0 (API 24) terinstall
- Periksa bahwa JDK version 11 sudah dikonfigurasi di Android Studio

## Struktur Project

```
ETS-PPB-Aplikasi-Gallery-DumpHere/
├── app/                              # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/dumphere/
│   │   │   │   ├── MainActivity.kt          # Activity utama aplikasi
│   │   │   │   └── ui/theme/
│   │   │   │       ├── Color.kt             # Definisi warna
│   │   │   │       ├── Theme.kt             # Tema aplikasi
│   │   │   │       └── Type.kt              # Tipografi
│   │   │   ├── res/
│   │   │   │   ├── drawable/               # Asset drawable
│   │   │   │   ├── mipmap-*/               # App icons
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml          # Warna aplikasi
│   │   │   │   │   ├── strings.xml         # String resources
│   │   │   │   │   └── themes.xml          # Tema resources
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml    # Backup configuration
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml         # App manifest
│   │   ├── androidTest/                     # Instrumented tests
│   │   └── test/                            # Unit tests
│   ├── build.gradle.kts                     # Build configuration
│   └── proguard-rules.pro                   # ProGuard rules
├── gradle/
│   ├── libs.versions.toml                   # Dependency versions
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts                         # Root build configuration
├── settings.gradle.kts                      # Settings configuration
└── gradle.properties                        # Gradle properties
```

## Build & Compile

### Build Development
```bash
./gradlew build
```

### Build APK Debug
```bash
./gradlew assembleDebug
```

### Build APK Release
```bash
./gradlew assembleRelease
```

Hasil build akan tersimpan di `app/build/outputs/apk/`

## Menjalankan Aplikasi

### Menggunakan Android Studio
1. Buka Android Studio
2. Pilih device atau emulator target
3. Klik tombol Run atau tekan `Shift + F10`

### Menggunakan Command Line
```bash
# Install dan run di connected device/emulator
./gradlew installDebug
adb shell am start -n com.example.dumphere/.MainActivity
```

## Testing

### Menjalankan Unit Tests
```bash
./gradlew test
```

### Menjalankan Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

Test files tersimpan di:
- Unit tests: `app/src/test/java/`
- Instrumented tests: `app/src/androidTest/java/`

## Permissions

Aplikasi membutuhkan permissions berikut (terdefinisi di `AndroidManifest.xml`):

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
```

- **INTERNET** - Untuk mengakses resource online
- **CAMERA** - Untuk mengambil foto menggunakan kamera perangkat

User akan diminta memberikan permission saat menjalankan fitur camera untuk pertama kalinya.

## Customization

### Mengubah Tema
Edit file berikut untuk menyesuaikan tema aplikasi:
- `app/src/main/res/values/colors.xml` - Warna aplikasi
- `app/src/main/res/values/themes.xml` - Tema resources
- `app/src/main/java/com/example/dumphere/ui/theme/` - Compose theme configuration

### Mengubah String Resources
Edit `app/src/main/res/values/strings.xml` untuk mengubah teks yang ditampilkan di aplikasi.

## Dependencies Management

Dependencies dikonfigurasi di `gradle/libs.versions.toml`:

### Core Dependencies
- **androidx.core:core-ktx** - Kotlin extensions untuk AndroidX
- **androidx.lifecycle:lifecycle-runtime-ktx** - Lifecycle management
- **androidx.activity:activity-compose** - Compose activity support

### UI Dependencies
- **androidx.compose.ui** - Compose UI components
- **androidx.compose.material3** - Material 3 components
- **coil-compose** - Image loading

### Camera Dependencies
- **androidx.camera:camera-core** - Core camera library
- **androidx.camera:camera-camera2** - Camera2 integration
- **androidx.camera:camera-lifecycle** - Lifecycle support
- **androidx.camera:camera-view** - Camera view component

## Troubleshooting

### Issue: Gradle sync gagal
**Solusi:**
1. Buka `File > Invalidate Caches / Restart` di Android Studio
2. Jalankan `./gradlew --refresh-dependencies`
3. Periksa Java version compatibility

### Issue: Camera tidak bekerja
**Solusi:**
1. Pastikan permission CAMERA sudah diberikan
2. Verifikasi device memiliki kamera
3. Periksa logcat untuk error messages

### Issue: Build error dengan Compose
**Solusi:**
1. Pastikan Compose version kompatibel
2. Update Android Studio ke versi terbaru
3. Clear build cache: `./gradlew clean build`

## Dokumentasi Tambahan

### Official Documentation
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [CameraX Documentation](https://developer.android.com/training/camerax)

### Useful Resources
- [Kotlin Language](https://kotlinlang.org/)
- [Coil Documentation](https://coil-kt.github.io/coil/)
- [AndroidX Libraries](https://developer.android.com/jetpack/androidx)

## Kontribusi

Untuk berkontribusi pada proyek ini:

1. Fork repository
2. Buat branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buka Pull Request

## Lisensi

Proyek ini dibuat sebagai bagian dari tugas **ETS PPB (Evaluasi Tengah Semester Pemrograman Perangkat Bergerak)**.

## Author

ALYA RAHMATILLAH MACHMUD - 5025231315

THALYTA VIUS PRAMESTI - 5025231055  
Departemen: Teknik Informatika  
Mata Kuliah: Pemrograman Perangkat Bergerak B (PPB B)  
Tugas: ETS - Aplikasi Gallery DumpHere

## Support

Untuk pertanyaan atau issues, silakan:
1. Buka GitHub Issues
2. Hubungi instructor/dosen pembimbing
3. Konsultasi dokumentasi Android developer

---

**Last Updated**: April 2026  
**Version**: 1.0
