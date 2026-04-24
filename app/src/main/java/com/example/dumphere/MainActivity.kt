package com.example.dumphere

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.dumphere.ui.theme.DumpHereTheme
import com.example.dumphere.ui.theme.Line
import com.example.dumphere.ui.theme.Mocha
import com.example.dumphere.ui.theme.MochaDark
import com.example.dumphere.ui.theme.Sand
import com.example.dumphere.ui.theme.TextMuted
import com.example.dumphere.ui.theme.WarmWhite
import kotlinx.coroutines.launch
import androidx.camera.core.Preview as CameraXPreview
import androidx.activity.result.PickVisualMediaRequest
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.ui.text.font.FontFamily
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.ui.graphics.SolidColor


private enum class MainTab(val label: String) {
    Calendar("Calendar"),
    Camera("Camera"),
    Profile("Profile")
}

private data class MemoryItem(
    val day: Int,
    val imageUrl: String
)

private data class MonthMemory(
    val month: String,
    val accent: Color,
    val memories: List<MemoryItem>
)

private data class MemoryData(
    val uri: Uri,
    val location: String,
    val categories: Set<String>,
    val timestamp: Long = System.currentTimeMillis() // Otomatis mencatat tanggal & jam saat disave!
)

private data class MonthMemoryDynamic(
    val month: String,
    val accent: Color,
    val memories: List<MemoryData>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DumpHereTheme {
                DumpHereApp()
            }
        }
    }
}

@Composable
fun DumpHereApp() {
    var selectedTab by remember { mutableStateOf(MainTab.Camera) }

    // Penyimpanan Data Global
    val globalMemories = remember { mutableStateListOf<MemoryData>() }
    var globalProfileName by remember { mutableStateOf("Kendal Jenner") }

    // STATE MODE GELAP BARU
    var isDarkTheme by remember { mutableStateOf(false) }

    // Mengubah warna background utama secara dinamis
    val mainBgColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = mainBgColor, // Background bereaksi terhadap mode gelap
        bottomBar = {
            BottomSidebar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.Calendar -> CalendarScreen(
                    savedMemories = globalMemories,
                    isDarkTheme = isDarkTheme // Kirim status tema ke Kalender
                )

                MainTab.Camera -> CameraScreen(
                    savedMemories = globalMemories,
                    isDarkTheme = isDarkTheme // Kirim status tema ke Kamera
                )

                MainTab.Profile -> ProfileScreen(
                    savedMemories = globalMemories,
                    profileName = globalProfileName,
                    onNameChange = { newName -> globalProfileName = newName },
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }
}

@Composable
private fun BottomSidebar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = WarmWhite,
        selectedTextColor = Sand,
        indicatorColor = MochaDark.copy(alpha = 0.35f),
        unselectedIconColor = Sand.copy(alpha = 0.85f),
        unselectedTextColor = Sand.copy(alpha = 0.85f)
    )

    // Membungkus NavigationBar dengan Box agar bisa diberi jarak (margin) dari tepi layar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp) // Jarak kiri-kanan & bawah agar melayang
    ) {
        NavigationBar(
            modifier = Modifier.clip(RoundedCornerShape(36.dp)), // Sudut sangat tumpul / Kapsul
            containerColor = Mocha,
            tonalElevation = 8.dp
        ) {
            MainTab.entries.forEach { tab ->
                val icon = when (tab) {
                    MainTab.Calendar -> Icons.Rounded.CalendarMonth
                    MainTab.Camera -> Icons.Rounded.PhotoCamera
                    MainTab.Profile -> Icons.Rounded.Person
                }
                NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    icon = { Icon(imageVector = icon, contentDescription = tab.label, modifier = Modifier.size(28.dp)) },
                    label = {
                        Text(
                            text = tab.label,
                             // Font bulat untuk teks menu
                            fontWeight = FontWeight.Medium
                        )
                    },
                    colors = itemColors
                )
            }
        }
    }
}

@Composable
private fun CameraScreen(savedMemories: MutableList<MemoryData>, isDarkTheme: Boolean) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }

    var showHistory by remember { mutableStateOf(false) }
    var showCategorize by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var activeCameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> hasCameraPermission = granted }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) { selectedImageUri = uri; showCategorize = true }
    }

    LaunchedEffect(Unit) { if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA) }

    if (showHistory) {
        HistoryGridScreen(
            memories = savedMemories,
            isDarkTheme = isDarkTheme,
            onClose = { showHistory = false },
            onDelete = { savedMemories.remove(it) }
        )
        return
    }

    if (showCategorize) {
        CategorizeScreen(
            selectedUri = selectedImageUri,
            isDarkTheme = isDarkTheme,
            onClose = { showCategorize = false; selectedImageUri = null },
            onSave = { newMemory -> savedMemories.add(0, newMemory); showCategorize = false; selectedImageUri = null }
        )
        return
    }

    // Warna Dinamis
    val bgColor = if (isDarkTheme) Color(0xFF121212) else WarmWhite
    val textColor = if (isDarkTheme) WarmWhite else MochaDark
    val iconColor = if (isDarkTheme) WarmWhite else Mocha

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(bgColor).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "DumpHere!",
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 12.dp),
                textAlign = TextAlign.Center, color = textColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth().aspectRatio(0.78f).clip(RoundedCornerShape(42.dp))
                    .background(MochaDark.copy(alpha = 0.1f)).border(2.dp, Mocha.copy(alpha = 0.18f), RoundedCornerShape(42.dp))
            ) {
                if (hasCameraPermission) {
                    LiveCameraPreview(modifier = Modifier.fillMaxSize(), imageCapture = imageCapture, cameraSelector = activeCameraSelector)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Meminta izin kamera...", color = textColor) }
                }

                RoundOverlayIcon(
                    modifier = Modifier.padding(18.dp).align(Alignment.TopEnd).clip(CircleShape).clickable {
                        activeCameraSelector = if (activeCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
                    },
                    icon = Icons.Rounded.FlipCameraAndroid
                )
            }

            Text("Live Preview", modifier = Modifier.padding(top = 24.dp), color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 42.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Image, contentDescription = "Gallery", tint = iconColor, modifier = Modifier.size(36.dp).clickable { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })

                Box(
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Memproses gambar...", Toast.LENGTH_SHORT).show()
                        val photoFile = File(context.cacheDir, "dump_memory_${System.currentTimeMillis()}.jpg")
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) { selectedImageUri = Uri.fromFile(photoFile); showCategorize = true }
                            override fun onError(exc: ImageCaptureException) { Toast.makeText(context, "Gagal: ${exc.message}", Toast.LENGTH_SHORT).show() }
                        })
                    }
                ) {
                    Box(modifier = Modifier.size(86.dp).border(4.dp, iconColor, CircleShape).padding(8.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(iconColor))
                    }
                }

                Icon(Icons.Rounded.PhotoLibrary, contentDescription = "History", tint = iconColor, modifier = Modifier.size(36.dp).clickable { showHistory = true })
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun CategorizeScreen(
    selectedUri: Uri?, isDarkTheme: Boolean, onClose: () -> Unit, onSave: (MemoryData) -> Unit
) {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    val scrollState = rememberScrollState()

    val bgColor = if (isDarkTheme) Color(0xFF121212) else WarmWhite
    val textColor = if (isDarkTheme) WarmWhite else MochaDark
    val fieldBg = if (isDarkTheme) Color(0xFF2C2C2C) else Color.Transparent

    Column(modifier = Modifier.fillMaxSize().background(bgColor).statusBarsPadding().padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = textColor, modifier = Modifier.size(28.dp).clickable { onClose() })
            Text("Categorize Memory", color = textColor, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(start = 16.dp))
        }

        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            Box(modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(30.dp)).border(2.dp, Mocha.copy(alpha = 0.5f), RoundedCornerShape(30.dp)).background(MochaDark.copy(alpha = 0.1f))) {
                if (selectedUri != null) {
                    AsyncImage(model = selectedUri, contentDescription = "Selected Image", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Hasil Jepretan Kamera", textAlign = TextAlign.Center, color = textColor) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Location", color = Mocha, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = locationText, onValueChange = { locationText = it },
                placeholder = { Text("Add Address/Location", color = textColor.copy(alpha = 0.6f)) },
                trailingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = "Location", tint = Mocha) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBg, unfocusedContainerColor = fieldBg,
                    focusedIndicatorColor = Mocha, unfocusedIndicatorColor = Mocha,
                    focusedTextColor = textColor, unfocusedTextColor = textColor, cursorColor = Mocha
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Categories", color = Mocha, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val allCategories = listOf("Adventure", "Food", "Nature", "Friends", "Travel", "Self")
                allCategories.chunked(3).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { category ->
                            CategoryChip(
                                label = category, selected = selectedCategories.contains(category), modifier = Modifier.weight(1f),
                                onClick = { selectedCategories = if (selectedCategories.contains(category)) selectedCategories - category else selectedCategories + category }
                            )
                        }
                        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 8.dp).clip(RoundedCornerShape(20.dp)).background(Mocha).clickable {
                if (selectedUri != null) {
                    Toast.makeText(context, "Memory Saved to History!", Toast.LENGTH_SHORT).show()
                    onSave(MemoryData(selectedUri, if (locationText.isNotBlank()) locationText else "Unknown Location", selectedCategories))
                }
            }.padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) { Text("Save to Dump", color = WarmWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) }
    }
}

// Komponen Pembantu 1: Menjalankan Kamera Asli di dalam UI Compose
@Composable
private fun LiveCameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    cameraSelector: CameraSelector // Menambahkan penerima settingan kamera (depan/belakang)
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    // LaunchedEffect akan berjalan ulang setiap kali 'cameraSelector' berubah (tombol ditekan)
    LaunchedEffect(cameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()
        val preview = CameraXPreview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            cameraProvider.unbindAll() // Hentikan kamera sebelumnya
            // Nyalakan kamera yang baru dipilih
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )
}

// Komponen Pembantu 2: Layar History Grid Ala Locket
@Composable
private fun HistoryGridScreen(memories: List<MemoryData>, isDarkTheme: Boolean, onClose: () -> Unit, onDelete: (MemoryData) -> Unit) {
    var selectedMemory by remember { mutableStateOf<MemoryData?>(null) }
    val bgColor = if (isDarkTheme) Color(0xFF121212) else WarmWhite
    val textColor = if (isDarkTheme) WarmWhite else MochaDark

    if (selectedMemory != null) {
        MemoryDetailScreen(memory = selectedMemory!!, isDarkTheme = isDarkTheme, onClose = { selectedMemory = null }, onDelete = { onDelete(it); selectedMemory = null })
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(bgColor).statusBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = textColor, modifier = Modifier.size(28.dp).clickable { onClose() })
            Text("Your History", color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
        }

        if (memories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) { Text("No memories yet.", color = textColor.copy(alpha = 0.6f), fontSize = 16.sp) }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()
            ) {
                items(memories.size) { index ->
                    val memory = memories[index]
                    AsyncImage(model = memory.uri, contentDescription = "History Item", contentScale = ContentScale.Crop, modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).border(2.dp, Mocha.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).clickable { selectedMemory = memory })
                }
            }
        }
    }
}

@Composable
private fun MemoryDetailScreen(memory: MemoryData, isDarkTheme: Boolean, onClose: () -> Unit, onDelete: (MemoryData) -> Unit) {
    val scrollState = rememberScrollState()
    val bgColor = if (isDarkTheme) Color(0xFF121212) else WarmWhite
    val textColor = if (isDarkTheme) WarmWhite else MochaDark

    Column(modifier = Modifier.fillMaxSize().background(bgColor).statusBarsPadding().padding(horizontal = 24.dp).verticalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = textColor, modifier = Modifier.size(28.dp).clickable { onClose() })
            Icon(Icons.Rounded.Delete, contentDescription = "Delete Memory", tint = Color(0xFFC04F4F), modifier = Modifier.size(28.dp).clickable { onDelete(memory) })
        }

        Box(modifier = Modifier.fillMaxWidth().height(360.dp).clip(RoundedCornerShape(30.dp)).border(2.dp, Mocha.copy(alpha = 0.5f), RoundedCornerShape(30.dp))) {
            AsyncImage(model = memory.uri, contentDescription = "Memory Preview", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Location", color = Mocha, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = memory.location, color = textColor, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Categories", color = Mocha, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (memory.categories.isEmpty()) {
            Text("No categories selected", color = textColor.copy(alpha = 0.6f))
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                memory.categories.forEach { cat ->
                    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Mocha).padding(horizontal = 16.dp, vertical = 8.dp)) { Text(cat, color = WarmWhite, fontSize = 14.sp) }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CalendarScreen(savedMemories: List<MemoryData>, isDarkTheme: Boolean) {
    var selectedMonthForSlider by remember { mutableStateOf<MonthMemoryDynamic?>(null) }
    var initialImageIndex by remember { mutableStateOf(0) }

    val groupedMemories = remember(savedMemories) {
        val formatMonthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val groups = savedMemories.groupBy { formatMonthYear.format(it.timestamp) }
        val colors = listOf(Sand, Color(0xFFB88C64), Color(0xFF8F5E3B), Mocha)
        var cIndex = 0
        groups.map { (monthYear, memories) ->
            MonthMemoryDynamic(monthYear, colors[cIndex++ % colors.size], memories.sortedBy { it.timestamp })
        }
    }

    // Penyesuaian Warna Tema
    val bgColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background
    val textColor = if (isDarkTheme) WarmWhite else Mocha

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor) // Background menyesuaikan tema
                .statusBarsPadding()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = textColor, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Monthly Memory Calendar",
                        color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 32.sp
                    )
                }
            }

            if (groupedMemories.isEmpty()) {
                item {
                    Text(
                        text = "Kalender masih kosong.\nMulai jepret dan save foto untuk mengisi kalender ini!",
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp)
                    )
                }
            } else {
                items(groupedMemories.size) { index ->
                    MonthCard(
                        month = groupedMemories[index],
                        isDarkTheme = isDarkTheme, // Kirim ke kartu
                        onMemoryClick = { clickedMonth, clickedIndex ->
                            selectedMonthForSlider = clickedMonth
                            initialImageIndex = clickedIndex
                        }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        if (selectedMonthForSlider != null) {
            MemorySliderOverlay(selectedMonthForSlider!!, initialImageIndex) { selectedMonthForSlider = null }
        }
    }
}

@Composable
private fun ProfileScreen(
    savedMemories: List<MemoryData>,
    profileName: String,
    onNameChange: (String) -> Unit,
    isDarkTheme: Boolean, // Parameter baru
    onThemeChange: (Boolean) -> Unit // Parameter baru
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }

    val totalMemories = savedMemories.size
    val allCategories = savedMemories.flatMap { it.categories }
    val totalCategoriesCount = allCategories.size
    val topCategories = allCategories
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(3)

    val categoryColors = listOf(MochaDark, Sand, Color(0xFFD0B092))

    // Warna Dinamis bereaksi terhadap Mode Gelap
    val textColor = if (isDarkTheme) WarmWhite else MochaDark
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else MaterialTheme.colorScheme.surface

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Profile Name", color = MochaDark, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Mocha,
                        focusedTextColor = MochaDark,
                        cursorColor = Mocha
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempName.isNotBlank()) onNameChange(tempName)
                    showEditNameDialog = false
                }) { Text("Save", color = Mocha) }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel", color = MochaDark.copy(alpha = 0.5f)) }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // SEKARANG KEDUANYA ADALAH TIPE "BRUSH"
                if (isDarkTheme) {
                    SolidColor(Color(0xFF121212))
                } else {
                    Brush.verticalGradient(
                        colors = listOf(MochaDark, Color(0xFF3C2417), MaterialTheme.colorScheme.background)
                    )
                }
            )
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    )  {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val initial = if (profileName.isNotBlank()) profileName.take(1).uppercase() else "U"

                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(colors = listOf(Color(0xFFE9C9A6), Color(0xFF82553A)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initial, color = WarmWhite, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))

                Text(text = profileName, color = WarmWhite, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit profile",
                    tint = WarmWhite,
                    modifier = Modifier.size(26.dp).clickable {
                        tempName = profileName
                        showEditNameDialog = true
                    }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text("My Stats", color = textColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).height(140.dp).clip(RoundedCornerShape(20.dp)).background(MochaDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalMemories", color = WarmWhite, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                                Text("Memories Saved", color = WarmWhite, fontSize = 14.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f).height(140.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                            Text("Top Categories", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            if (topCategories.isEmpty()) {
                                Text("No data yet.", color = textColor.copy(alpha = 0.6f), fontSize = 14.sp)
                            } else {
                                topCategories.forEachIndexed { index, categoryData ->
                                    val percentage = (categoryData.value.toFloat() / totalCategoriesCount * 100).toInt()
                                    // Memodifikasi warna teks legenda menyesuaikan tema
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.width(6.dp).height(38.dp).clip(RoundedCornerShape(999.dp)).background(categoryColors[index % categoryColors.size]))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(categoryData.key, color = textColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                            Text("$percentage%", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- TOGGLE DARK MODE BARU ---
        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                            contentDescription = "Theme Icon",
                            tint = if (isDarkTheme) WarmWhite else Mocha
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Dark Mode", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WarmWhite,
                            checkedTrackColor = Mocha,
                            uncheckedThumbColor = Mocha,
                            uncheckedTrackColor = Line
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp)) // Menghindari tertutup navbar melayang
        }
    }
}

private val CaramelWarm = Color(0xFF8D5B34)

@Composable
private fun MonthCard(
    month: MonthMemoryDynamic,
    isDarkTheme: Boolean,
    onMemoryClick: (MonthMemoryDynamic, Int) -> Unit
) {
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())

    // Warna Kartu Menyesuaikan Tema
    val cardBg = if (isDarkTheme) Color(0xFF2C2C2C) else WarmWhite
    val textColor = if (isDarkTheme) WarmWhite else MochaDark

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = month.month, color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().height(212.dp)
            ) {
                items((1..35).toList()) { day ->
                    val memoryForDay = month.memories.find { dayFormat.format(it.timestamp).toInt() == day }

                    if (memoryForDay != null) {
                        AsyncImage(
                            model = memoryForDay.uri,
                            contentDescription = "Preview Day $day",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Mocha.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable {
                                    val index = month.memories.indexOf(memoryForDay)
                                    onMemoryClick(month, index)
                                }
                        )
                    } else {
                        Box(modifier = Modifier.aspectRatio(1f).padding(10.dp), contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Mocha.copy(alpha = 0.95f)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorizeCard(
    selectedUri: Uri?,
    capturedBitmap: Bitmap?,
    onSave: () -> Unit // Callback untuk membersihkan layar setelah disave
) {
    val context = LocalContext.current

    // State untuk menyimpan teks input lokasi dan daftar kategori yang dipilih user
    var locationText by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    val allCategoriesRow1 = listOf("Adventure", "Food", "Nature")
    val allCategoriesRow2 = listOf("Friends", "Travel", "Self")

    Card(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Categorize Your Memory",
                color = Mocha,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))

            // 1. Tampilan Gambar yang Dipilih/Difoto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(2.dp, Mocha, RoundedCornerShape(26.dp))
                    .background(Color(0xFFE8E0D5)) // Warna background kosong
            ) {
                if (selectedUri != null) {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (capturedBitmap != null) {
                    Image(
                        bitmap = capturedBitmap.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Tampilan jika foto belum dipilih
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Photo Selected", color = MochaDark.copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text("Location", color = Mocha, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // 2. Input Lokasi Interaktif
            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = it },
                placeholder = { Text("Add Address/Location", color = MochaDark.copy(alpha = 0.7f)) },
                trailingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = "Location", tint = Mocha) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Mocha,
                    unfocusedIndicatorColor = Mocha,
                    focusedTextColor = MochaDark,
                    unfocusedTextColor = MochaDark,
                    cursorColor = Mocha
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))
            Text("Categories", color = Mocha, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // 3. Chip Kategori Interaktif
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    allCategoriesRow1.forEach { category ->
                        CategoryChip(
                            label = category,
                            selected = selectedCategories.contains(category),
                            onClick = {
                                // Logika nyala/mati chip
                                selectedCategories = if (selectedCategories.contains(category)) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                            }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    allCategoriesRow2.forEach { category ->
                        CategoryChip(
                            label = category,
                            selected = selectedCategories.contains(category),
                            onClick = {
                                selectedCategories = if (selectedCategories.contains(category)) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Tombol Save Simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Mocha)
                    .clickable {
                        // Cek apakah ada foto yang akan di-save
                        if (selectedUri == null && capturedBitmap == null) {
                            Toast.makeText(context, "Ambil atau pilih foto terlebih dahulu!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Memory Saved!", Toast.LENGTH_SHORT).show()
                            // Reset inputan setelah disave
                            locationText = ""
                            selectedCategories = setOf()
                            onSave()
                        }
                    }
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Save to Dump",
                    color = WarmWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Mocha else Color.Transparent)
            .border(2.dp, Mocha, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) WarmWhite else Mocha,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1 // Mencegah teks turun ke bawah jika layar sempit
        )
    }
}

@Composable
private fun SettingsGroup() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SettingRow("Memory Settings")
            HorizontalDivider(color = Line)
            SettingRow("Data & Privacy")
            HorizontalDivider(color = Line)
            SettingRow("Help & Support")
        }
    }
}

@Composable
private fun SettingRow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MochaDark,
            fontSize = 16.sp
        )
        Icon(Icons.Rounded.ChevronRight, contentDescription = title, tint = TextMuted)
    }
}

@Composable
private fun DistributionLegend(title: String, value: String, color: Color) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, color = MochaDark, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(value, color = MochaDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun VerticalStackBar(
    modifier: Modifier = Modifier,
    segments: List<Pair<Color, Float>>
) {
    Column(
        modifier = modifier
            .height(164.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Line)
    ) {
        segments.forEach { (color, weight) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight)
                    .background(color)
            )
        }
    }
}

@Composable
private fun CaptureButton() {
    Box(
        modifier = Modifier
            .size(118.dp)
            .border(5.dp, Mocha, CircleShape)
            .padding(10.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Mocha)
        )
    }
}

@Composable
private fun RoundOverlayIcon(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(Mocha.copy(alpha = 0.92f)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Sand, modifier = Modifier.size(30.dp))
    }
}

@Composable
private fun ScenicPlaceholder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFF4EEE6), Color(0xFFE2D2BF), Color(0xFFAF8F72))
            )
        )

        drawRoundRect(
            color = Color(0xFFF8F3EC),
            topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.22f, size.height * 0.66f),
            cornerRadius = CornerRadius(40f, 40f)
        )
        drawRoundRect(
            color = Color(0xFFF8F3EC),
            topLeft = Offset(size.width * 0.74f, size.height * 0.08f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.16f, size.height * 0.52f),
            cornerRadius = CornerRadius(34f, 34f)
        )
        drawRoundRect(
            color = Color(0xFF8C613E),
            topLeft = Offset(size.width * 0.30f, size.height * 0.54f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.48f, size.height * 0.10f),
            cornerRadius = CornerRadius(18f, 18f)
        )
        drawRoundRect(
            color = Color(0xFFE8D9C5),
            topLeft = Offset(size.width * 0.40f, size.height * 0.46f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.28f, size.height * 0.12f),
            cornerRadius = CornerRadius(24f, 24f)
        )
        drawRoundRect(
            color = Color(0xFFE8D9C5),
            topLeft = Offset(size.width * 0.48f, size.height * 0.66f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.32f, size.height * 0.18f),
            cornerRadius = CornerRadius(22f, 22f)
        )
    }
}

@Composable
private fun HikingPlaceholder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFD9EBF8), Color(0xFFB6CC9A), Color(0xFFA57D4C))
            )
        )
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, size.height * 0.75f)
                lineTo(size.width * 0.25f, size.height * 0.35f)
                lineTo(size.width * 0.45f, size.height * 0.72f)
                lineTo(size.width * 0.65f, size.height * 0.28f)
                lineTo(size.width, size.height * 0.68f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            },
            color = Color(0xFF5F7D47)
        )
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.58f, size.height)
                quadraticTo(
                    size.width * 0.66f,
                    size.height * 0.76f,
                    size.width * 0.78f,
                    size.height * 0.52f
                )
                lineTo(size.width * 0.86f, size.height)
                close()
            },
            color = Color(0xFFE0C28E)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DumpHereAppPreview() {
    DumpHereTheme {
        Surface {
            DumpHereApp()
        }
    }
}

@Composable
private fun MemorySliderOverlay(
    monthMemory: MonthMemoryDynamic,
    initialIndex: Int,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { monthMemory.memories.size })
    val coroutineScope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

    BackHandler(onBack = onClose)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF3A2D24))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Bagian Header untuk tombol Close
            Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = WarmWhite,
                    modifier = Modifier.align(Alignment.TopEnd).size(32.dp).clickable { onClose() }
                )
            }

            // Bagian Pager (Gambar akan otomatis mengambil sisa ruang tengah tanpa bertabrakan)
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    val memory = monthMemory.memories[page]
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = memory.uri,
                            contentDescription = "Memory Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .aspectRatio(0.75f)
                                .clip(RoundedCornerShape(24.dp))
                                .border(2.dp, WarmWhite, RoundedCornerShape(24.dp))
                        )
                    }
                }

                // Panah Kiri Kanan
                if (pagerState.currentPage > 0) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp).size(48.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)).clickable { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous", tint = Color.White) }
                }
                if (pagerState.currentPage < monthMemory.memories.size - 1) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp).size(48.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)).clickable { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Rounded.ChevronRight, contentDescription = "Next", tint = Color.White) }
                }
            }

            // Bagian Teks Footer (Pasti aman di bawah gambar)
            val currentMemory = monthMemory.memories[pagerState.currentPage]
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = currentMemory.location, color = WarmWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dateFormat.format(currentMemory.timestamp), color = WarmWhite.copy(alpha = 0.8f), fontSize = 14.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentMemory.categories.forEach { cat ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Mocha).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text(cat, color = WarmWhite, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp)) // Jarak ekstra menghindari navbar
            }
        }
    }
}
