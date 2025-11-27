package com.example.todo_triple

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_triple.ui.theme.TaewonyTheme

@Composable
fun Mp3PlayerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var musicPlayerService by remember { mutableStateOf<MusicPlayerService?>(null) }

    // Collect state from the service
    val isPlaying by musicPlayerService?.isPlaying?.collectAsState(initial = false) ?: remember { mutableStateOf(false) }
    val currentTrack by musicPlayerService?.currentTrack?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicPlayerService.MusicBinder
                musicPlayerService = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                musicPlayerService = null
            }
        }
    }

    // Bind to the service on composition and unbind on disposal
    DisposableEffect(Unit) {
        val intent = Intent(context, MusicPlayerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    // MP3 file selection launcher
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                musicPlayerService?.play(context, it)
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MP3 Player (Service)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display file name from service state
            Text(
                text = currentTrack?.fileName ?: "No file selected",
                fontSize = 16.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause button
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = currentTrack != null) {
                            musicPlayerService?.let {
                                if (it.isPlaying.value) {
                                    it.pause()
                                } else {
                                    it.resume()
                                }
                            }
                        }
                )

                // Stop button
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = currentTrack != null) {
                            musicPlayerService?.stopPlayback()
                        }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // File select button
            Button(onClick = { openFileLauncher.launch(arrayOf("audio/*")) }) {
                Text("Select MP3 File")
            }
             Spacer(modifier = Modifier.height(8.dp))
            // Button to play default music from res/raw
            Button(onClick = {
                val resourceUri = Uri.parse("android.resource://${context.packageName}/${R.raw.music}")
                musicPlayerService?.play(context, resourceUri)
            }) {
                Text("Play Default Music")
            }
        }
    }
}


@Composable
fun Mp3PlayerUIScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 전체 화면 배경
            .padding(16.dp),
        contentAlignment = Alignment.Center // Box 내용을 중앙에 배치
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black) // MP3 플레이어 영역 배경을 검은색으로 설정
                .padding(24.dp), // 내부 여백 추가
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "     MP3 Player    ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // 텍스트 색상을 흰색으로 변경
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Play",
                    modifier = Modifier.size(48.dp))
                Image(painter = painterResource(id = R.drawable.ic_stop),
                    contentDescription = "Stop",
                    modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Mp3PlayerUIScreenPreview() {
    TaewonyTheme {
        Mp3PlayerScreen()
    }
}