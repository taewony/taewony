package com.example.fake_store

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
Hilt를 활성화하려면 @HiltAndroidApp으로 표시된 Application 서브클래스를 하나 만들어야 합니다.
그리고 AndroidManifest.xml에 등록:
<application
    android:name=".FakeStoreApplication"
    android:label="@string/app_name"
    android:theme="@style/Theme.HiltTest">
</application>
*/

@HiltAndroidApp
class FakeStoreApplication : Application() {
}