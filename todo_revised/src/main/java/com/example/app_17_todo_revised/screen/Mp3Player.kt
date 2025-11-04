package com.example.app_17_todo_revised.screen

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
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
import com.example.app_17_todo_revised.R
import com.example.app_17_todo_revised.ui.theme.ComposeLabTheme

// 파일 이름 가져오는 확장 함수
fun Uri.getFileName(context: Context): String? {
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}

// MP3 플레이어 화면을 구성하는 Composable 함수
@Composable
fun Mp3PlayerScreen() {
    val context = LocalContext.current

    // 상태 변수들
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("No file selected")}
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // 여기서 ActivityResult란, 별도의 화면(예: 파일 선택기, 카메라, 갤러리 등)을 의미한다.
    // MP3 파일 선택을 위한 ActivityResultLauncher
    val openFileLauncher = rememberLauncherForActivityResult(
        // 어떤 임무를 수행할 것인가? (contract): 어떤 종류의 액티비티를 실행할지 정의합니다.
        contract = ActivityResultContracts.OpenDocument(),
        // 어떤 결과를 받을 것인가? (onResult): 파일 선택 후 실행할 로직을 정의합니다.
        onResult = { uri ->
            uri?.let {
                // 기존 플레이어 정지 및 해제
                mediaPlayer?.stop()
                mediaPlayer?.release()

                selectedFileUri = it
                fileName = it.getFileName(context) ?: "Unknown file"
                mediaPlayer = MediaPlayer.create(context, it).apply {
                    setOnCompletionListener {
                        isPlaying = false // 재생이 끝나면 isPlaying 상태 변경
                    }
                }
            }
        }
    )
    
    // Composable이 화면에서 사라질 때 MediaPlayer 리소스 해제
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

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

            // 선택된 파일 이름 표시
            Text(
                text = fileName,
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
                // 재생/일시정지 버튼
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = selectedFileUri != null) {
                            mediaPlayer?.let {
                                if (it.isPlaying) {
                                    it.pause()
                                } else {
                                    it.start()
                                }
                                isPlaying = it.isPlaying
                            }
                        }
                )

                // 정지 버튼
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = selectedFileUri != null) {
                            mediaPlayer?.let {
                                if (it.isPlaying) {
                                    it.stop()
                                    it.prepare() // stop() 후에 다시 재생하려면 prepare() 필요
                                }
                                isPlaying = false
                            }
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 파일 선택 버튼
                Button(onClick = {
                    // openFileLauncher.launch(arrayOf("audio/mpeg"))

                    // 2. res/raw 리소스에 대한 Uri를 생성합니다.
                    //    이렇게 하면 selectedFileUri 상태가 null이 아니게 되어 컨트롤 버튼이 활성화됩니다.
                    val resourceUri = Uri.parse("android.resource://${context.packageName}/${R.raw.music}")

                    // 3. 리소스 ID(또는 Uri)를 사용해 MediaPlayer를 생성합니다. (아직 재생은 안 함)
                    mediaPlayer = MediaPlayer.create(context, resourceUri).apply {
                        // 재생이 끝나면 '재생 중' 상태를 false로 변경하도록 리스너를 설정합니다.
                        setOnCompletionListener {
                            isPlaying = false
                        }
                    }

                    // 4. UI 상태를 업데이트하여 로드된 파일 정보를 표시합니다.
                    selectedFileUri = resourceUri
                    fileName = "music.mp3 (default)"
                    isPlaying = false // '재생 중' 상태는 false로 유지하여 사용자가 직접 재생하도록 합니다.
                }) {
                    Text("Select MP3 File")
                }
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
    ComposeLabTheme {
        Mp3PlayerUIScreen()
    }
}