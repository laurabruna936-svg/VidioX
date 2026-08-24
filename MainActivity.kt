package com.vidiox.beta

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VidioXApp() }
    }
}

@Composable
fun VidioXApp() {
    var screen by remember { mutableStateOf("home") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var exportStarted by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            videoUri = uri
            screen = "editor"
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFB65CFF),
            secondary = Color(0xFFFF4FD8),
            background = Color(0xFF0B0712),
            surface = Color(0xFF17111F)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (screen) {
                "home" -> HomeScreen(
                    onNewProject = { picker.launch("video/*") },
                    onTemplates = { }
                )
                "editor" -> EditorScreen(
                    videoUri = videoUri,
                    onBack = { screen = "home" },
                    onExport = { exportStarted = true }
                )
            }

            if (exportStarted) {
                AlertDialog(
                    onDismissRequest = { exportStarted = false },
                    title = { Text("Exportação") },
                    text = {
                        Text(
                            "A tela de exportação do Beta está pronta. " +
                            "A próxima etapa liga o botão ao Media3 Transformer para gerar o MP4."
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { exportStarted = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(onNewProject: () -> Unit, onTemplates: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("VidioX", fontSize = 34.sp, color = Color.White)
        Text("Editor de vídeo • Beta", color = Color.LightGray)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onNewProject,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("＋  Novo projeto", fontSize = 18.sp)
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = onTemplates,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Modelos")
        }

        Spacer(Modifier.height(30.dp))
        Text("Projetos recentes", color = Color.White, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF17111F), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum projeto ainda", color = Color.Gray)
        }
    }
}

@Composable
fun EditorScreen(videoUri: Uri?, onBack: () -> Unit, onExport: () -> Unit) {
    var selectedTool by remember { mutableStateOf("Cortar") }
    val tools = listOf("Cortar", "Texto", "Áudio", "Efeitos", "Velocidade")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("← Voltar") }
            Text("VidioX", color = Color.White, fontSize = 20.sp)
            Button(onClick = onExport) { Text("Exportar") }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
                .background(Color.Black, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (videoUri != null) "PRÉ-VISUALIZAÇÃO\n\nVídeo carregado"
                else "Nenhum vídeo",
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Spacer(Modifier.height(12.dp))
        Text("Timeline", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("🎬 0:00", "🎬 0:05", "🎬 0:10", "🎬 0:15")) { clip ->
                Box(
                    modifier = Modifier
                        .width(105.dp)
                        .height(64.dp)
                        .background(Color(0xFF30213A), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(clip, color = Color.White)
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(tools) { tool ->
                Text(
                    tool,
                    color = if (selectedTool == tool) Color(0xFFCF7BFF) else Color.LightGray,
                    modifier = Modifier
                        .clickable { selectedTool = tool }
                        .padding(12.dp)
                )
            }
        }
    }
}
