package com.example.todo_triple

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.todo_triple.ui.theme.TaewonyTheme

/**
 * 1. 배터리 관련 데이터 상태를 관리하는 ViewModel을 정의합니다.
 * ViewModel은 UI와 관련된 데이터를 저장하고 관리하여, 화면 회전과 같은 구성 변경에도 데이터를 유지합니다.
 */
class BatteryViewModel : ViewModel() {
    // _batteryLevel: 내부에서만 수정 가능한 MutableStateFlow. 배터리 잔량을 저장합니다.
    private val _batteryLevel = MutableStateFlow(0)
    // batteryLevel: 외부에는 읽기 전용 StateFlow로 노출하여 데이터의 일관성을 유지합니다.
    val batteryLevel: StateFlow<Int> = _batteryLevel

    // 충전 상태를 저장하는 StateFlow
    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging

    // 충전 방식을 저장하는 StateFlow
    private val _chargeType = MutableStateFlow("Unknown")
    val chargeType: StateFlow<String> = _chargeType

    /**
     * BroadcastReceiver로부터 받은 새로운 배터리 상태 정보로 StateFlow 값을 업데이트합니다.
     * 이 함수가 호출되면 StateFlow를 구독하는 UI는 자동으로 다시 그려집니다(recomposition).
     */
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

/**
 * 2. NavHost에서 호출될 진입점(Entry-point) 역할을 하는 Composable입니다.
 * 이 Composable은 데이터 로직(BroadcastReceiver 등록 및 ViewModel 연동)을 처리하고,
 * 상태가 없는(stateless) UI Composable(`BatteryStatusScreen`)에 데이터를 전달하는 역할을 합니다.
 */
@Composable
fun BatteryStatusRoute(
    modifier: Modifier = Modifier,
    // viewModel() 헬퍼 함수를 사용하여 ViewModel의 인스턴스를 가져옵니다.
    // 이 방식은 Composable의 생명주기와 관계없이 ViewModel 인스턴스를 유지시켜줍니다.
    viewModel: BatteryViewModel = viewModel()
) {
    // LocalContext.current를 통해 현재 Composable의 Context에 접근합니다.
    // BroadcastReceiver를 등록할 때 필요합니다.
    val context = LocalContext.current

    /**
     * DisposableEffect는 Composable이 화면에 표시될 때(composition) 코드를 실행하고,
     * 화면에서 사라질 때(disposal) 정리(clean-up) 코드를 실행할 수 있게 해줍니다.
     * 여기서는 BroadcastReceiver를 안전하게 등록하고 해제하기 위해 사용됩니다.
     */
    DisposableEffect(Unit) {
        // BroadcastReceiver: 시스템 전체에서 발생하는 방송(broadcast) 메시지를 수신하는 컴포넌트입니다.
        // 여기서는 배터리 상태 변경 이벤트를 수신합니다.
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // 인텐트에서 배터리 관련 정보를 추출합니다.
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                // 배터리 잔량을 백분율로 계산합니다.
                val batteryPct = if (level != -1 && scale != -1) (level * 100 / scale.toFloat()).toInt() else 0

                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

                val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

                // 추출한 정보로 ViewModel의 상태를 업데이트합니다.
                viewModel.updateBatteryStatus(batteryPct, isCharging, chargePlug)
            }
        }

        // IntentFilter: 수신할 인텐트(여기서는 배터리 상태 변경)를 지정합니다.
        // ACTION_BATTERY_CHANGED는 "Sticky Intent"이므로, 리시버를 등록하는 즉시 현재 배터리 상태를 담은 인텐트를 받을 수 있습니다.
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, intentFilter)

        // onDispose: Composable이 화면에서 사라질 때 호출되는 블록입니다.
        // 메모리 누수(memory leak)를 방지하기 위해 등록했던 리시버를 반드시 해제해야 합니다.
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // ViewModel의 StateFlow를 `collectAsState`를 통해 구독합니다.
    // StateFlow의 값이 변경될 때마다 Composable이 자동으로 다시 그려지게(recomposition) 됩니다.
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val chargeType by viewModel.chargeType.collectAsState()

    // 상태를 UI Composable에 전달하여 화면을 그립니다.
    BatteryStatusScreen(
        batteryLevel = batteryLevel,
        isCharging = isCharging,
        chargeType = chargeType,
        modifier = modifier
    )
}

/**
 * 3. 배터리 상태를 화면에 표시하는 역할을 하는 Stateless Composable입니다.
 * 이 Composable은 외부로부터 데이터를 전달받아 UI만 그리는 역할에 집중합니다.
 * 자체적으로 상태를 가지지 않기 때문에 재사용 및 테스트가 용이합니다.
 */
@Composable
fun BatteryStatusScreen(batteryLevel: Int, isCharging: Boolean, chargeType: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // 테마의 배경색을 명시적으로 설정합니다.
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

/**
 * `@Preview` 어노테이션은 안드로이드 스튜디오의 디자인 탭에서 Composable의 UI를 미리 볼 수 있게 해줍니다.
 * 이를 통해 앱을 실제 기기나 에뮬레이터에서 실행하지 않고도 UI를 빠르게 확인하고 수정할 수 있습니다.
 * 여기서는 가상의 데이터(batteryLevel=75, isCharging=true)를 전달하여 UI가 어떻게 보일지 확인합니다.
 */
@Preview(showBackground = true)
@Composable
fun BatteryStatusScreenPreview() {
    TaewonyTheme {
        BatteryStatusScreen(batteryLevel = 75, isCharging = true, chargeType = "USB")
    }
}
