package com.register.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.register.app.ui.theme.RegisterTheme
import com.register.app.util.DataStoreManager
import com.register.app.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { Utils.createDeviceId(dataStoreManager) }
        startMyFirebaseMessagingService()
        setContent {
            RegisterTheme {
                RegisterAppNavHost(this, dataStoreManager)
            }
        }
    }

    private fun startMyFirebaseMessagingService() {
        val intent = Intent(this@MainActivity, NotificationService::class.java)
        startService(intent)
    }
}
