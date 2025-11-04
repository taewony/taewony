package com.example.app_17_todo_revised.screen

// LocalContext`는 Composable 함수 내에서 현재 안드로이드 `Context`에 접근할 수 있도록 해주는 역할을 합니다.
// `BroadcastReceiver`를 등록하는 등 `Context`가 필요한 작업을 할 때 사용됩니다.
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_17_todo_revised.ui.theme.ComposeLabTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 1. 배터리 관련 데이터 상태를 관리하는 ViewModel 정의
class BatteryViewModel : ViewModel() {
    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel

    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging

    private val _chargeType = MutableStateFlow("Unknown")
    val chargeType: StateFlow<String> = _chargeType

    fun updateBatteryStatus(level: Int, charging: Boolean, chargePlug: Int) {
        _batteryLevel.value = level
        _isCharging.value = charging
        _chargeType.value = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Unknown"
        }
    }
}

// 2. NavHost에서 호출될 진입점(Entry-point) Composable
@Composable
fun BatteryStatusScreen(
    // ViewModel을 composable 내에서 직접 생성 및 관리
    viewModel: BatteryViewModel = viewModel()
) {
    val context = LocalContext.current

    // Composable의 생명주기에 맞춰 BroadcastReceiver를 등록/해제
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = if (level != -1 && scale != -1) (level * 100 / scale.toFloat()).toInt() else 0

                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

                val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

                viewModel.updateBatteryStatus(batteryPct, isCharging, chargePlug)
            }
        }

        // 초기 데이터 로드를 위해 Sticky Intent를 수신
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, intentFilter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // ViewModel의 StateFlow를 구독하여 UI에 상태 전달
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val chargeType by viewModel.chargeType.collectAsState()

    // 실제 UI Composable 호출
    BatteryStatusUIScreen(
        batteryLevel = batteryLevel,
        isCharging = isCharging,
        chargeType = chargeType
    )
}

@Composable
fun BatteryStatusUIScreen(batteryLevel: Int, isCharging: Boolean, chargeType: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 배경색 명시적 설정
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Battery Level: $batteryLevel%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status: ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        
            Text(
                text = if (isCharging) "Charging" else "Not Charging",
                fontSize = 20.sp,
                color = if (isCharging) Color.Blue else Color.Red
            )
            if (isCharging) {
                Spacer(modifier = Modifier.width(8.dp))            
                Text(
                    text = "Type: $chargeType",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BatteryStatusScreenPreview() {
    ComposeLabTheme {
        BatteryStatusUIScreen(
            batteryLevel = 75,
            isCharging = true,
            chargeType = "USB"
        )
    }
}
