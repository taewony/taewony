# MusicPlayerService를 이용한 MP3 플레이어 리팩토링

이 문서는 기존 `Mp3Player.kt`의 문제점을 분석하고, `Service`를 사용하여 어떻게 문제를 해결하고 올바른 아키텍처로 개선하는지 설명합니다.

## 1. 기존 방식의 문제점: Activity에서 MediaPlayer 직접 제어

기존 코드는 `Mp3PlayerScreen` Composable 함수 내에서 `MediaPlayer`를 직접 생성하고 상태를 관리했습니다.

```kotlin
// Activity 또는 Fragment 내부에 있는 코드
var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
// ...
mediaPlayer = MediaPlayer.create(context, it)
mediaPlayer.start()
```

이 방식은 다음과 같은 심각한 문제들을 가집니다.

-   **생명주기 종속성**: `MediaPlayer` 인스턴스가 `Activity`의 생명주기에 완전히 종속됩니다. 사용자가 홈 버튼을 누르거나 다른 앱으로 전환하면 `Activity`가 파괴되면서 음악이 즉시 중단됩니다.
-   **백그라운드 재생 불가**: 앱이 백그라운드 상태가 되면 음악 재생을 유지할 수 없습니다. 이는 음악 플레이어의 핵심 기능을 수행할 수 없다는 의미입니다.
-   **UI 스레드 차단 가능성**: `MediaPlayer.create()`는 내부적으로 파일을 읽고 디코딩할 준비를 동기적으로 수행합니다. 이로 인해 UI 스레드가 잠시 멈춰 앱의 반응성이 떨어질 수 있습니다.
-   **제어의 분산**: `MediaPlayer` 인스턴스와 재생 상태(`isPlaying`)가 UI와 강하게 결합되어 있어, 여러 화면에서 일관된 상태를 유지하고 제어하기가 매우 어렵습니다.

> 이 방식은 짧은 효과음을 화면 내에서 재생할 때만 적합하며, 지속적인 배경 재생이 필요한 음악 플레이어에는 절대로 사용해서는 안 됩니다.

## 2. 해결책: `MusicPlayerService` 도입

이러한 문제들을 해결하기 위해 안드로이드의 `Service` 컴포넌트를 사용합니다. `Service`는 UI와 독립적으로 백그라운드에서 오래 실행되는 작업을 수행하기 위해 설계되었습니다.

### `MusicPlayerService` 아키텍처의 장점

| 특징 | `MediaPlayer` 직접 사용 | `MusicPlayerService` 사용 |
| :--- | :--- | :--- |
| **생명주기** | UI 컴포넌트(Activity)에 종속적 | **독립적**. UI가 사라져도 유지됨 |
| **백그라운드 재생**| **불가능** | **가능 (핵심 목적)** |
| **UI 스레드** | 리소스 준비 시 차단 위험 있음 | 안전함. 백그라운드에서 작업 처리 |
| **제어/상태 관리** | 복잡하고 분산됨 | **쉽고 중앙 집중적** |
| **시스템 통합** | 알림, 잠금 화면 컨트롤 등 연동이 어려움 | `Foreground Service`와 `MediaSession`으로 시스템과 완벽히 통합 |

**결론적으로, `MusicPlayerService`는 단순히 더 나은 방법이 아니라, 제대로 동작하는 음악 플레이어를 만들기 위한 '유일하고 올바른' 아키텍처입니다.**

---

## 3. 리팩토링된 코드

아래는 `MusicPlayerService` 아키텍처를 적용한 전체 코드입니다.

### 1) `MusicPlayerService.kt` (신규 생성)

`MediaPlayer`의 생명주기를 관리하고, 재생/일시정지/정지 로직을 수행하며, UI에 현재 상태를 `StateFlow`로 전달하는 중앙 허브입니다. `Foreground Service`로 동작하여 앱이 백그라운드에 있어도 시스템에 의해 종료되지 않도록 합니다.

```kotlin
package com.example.todo_triple

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.OpenableColumns
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class to hold track information
data class TrackInfo(val fileName: String)

class MusicPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()

    // StateFlow to communicate state to the UI
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<TrackInfo?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "MusicPlayerServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    // Binder class for clients to access the service
    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun play(context: Context, uri: Uri) {
        // Release previous player
        mediaPlayer?.release()

        // Create and prepare new player
        mediaPlayer = MediaPlayer.create(context, uri).apply {
            setOnCompletionListener {
                stopPlayback()
            }
        }
        mediaPlayer?.start()

        // Update state
        _isPlaying.value = true
        _currentTrack.value = TrackInfo(fileName = uri.getFileName(context) ?: "Unknown File")

        // Start as a foreground service
        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentTrack.value = null
        stopForeground(true)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Music Player Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing: ${_currentTrack.value?.fileName ?: "No track"}")
            .setSmallIcon(R.drawable.ic_play) // Replace with your app's icon
            .build()
    }
}

// Extension function to get file name from Uri
fun Uri.getFileName(context: Context): String? {
    // This is a common way to get file name from a content Uri
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}
```

### 2) `AndroidManifest.xml` (수정)

생성한 `MusicPlayerService`를 Android 시스템에 등록하고, `Foreground Service` 실행에 필요한 권한을 요청합니다.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Taewony">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Taewony">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".MusicPlayerService"
            android:exported="false" />

    </application>

</manifest>
```

### 3) `Mp3Player.kt` (수정)

UI는 이제 `MediaPlayer`를 직접 제어하지 않습니다. 대신 `ServiceConnection`을 통해 `MusicPlayerService`에 연결하고, 서비스가 제공하는 `StateFlow`를 구독하여 `isPlaying`, `currentTrack` 같은 상태를 받아옵니다. 버튼 클릭 이벤트는 서비스의 `play`, `pause`, `stopPlayback` 같은 함수를 호출하여 서비스에 명령을 전달합니다.

```kotlin
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
```