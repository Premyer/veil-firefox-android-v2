package com.premyer.veilfirefoxandroidv2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeilFirefoxAndroidV2Theme {
                BrowserApp()
            }
        }
    }
}

@Composable
fun BrowserApp() {
    var urlText by rememberSaveable { mutableStateOf("https://www.google.com") }
    var canGoBack by rememberSaveable { mutableStateOf(false) }
    var canGoForward by rememberSaveable { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val loadUrl: (String) -> Unit = { input ->
        val normalized = normalizeUrl(input)
        urlText = normalized
        webViewRef?.loadUrl(normalized)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (webViewRef?.canGoBack() == true) {
                            webViewRef?.goBack()
                        }
                    },
                    enabled = canGoBack
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                }

                IconButton(
                    onClick = {
                        if (webViewRef?.canGoForward() == true) {
                            webViewRef?.goForward()
                        }
                    },
                    enabled = canGoForward
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Vorwärts")
                }

                IconButton(
                    onClick = {
                        webViewRef?.reload()
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren")
                }

                IconButton(
                    onClick = {
                        loadUrl("https://www.google.com")
                    }
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Startseite")
                }

                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        TextButton(onClick = { loadUrl(urlText) }) {
                            Text("Go")
                        }
                    }
                )
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            if (url != null) {
                                urlText = url
                            }
                            canGoBack = view?.canGoBack() == true
                            canGoForward = view?.canGoForward() == true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            if (url != null) {
                                urlText = url
                            }
                            canGoBack = view?.canGoBack() == true
                            canGoForward = view?.canGoForward() == true
                        }
                    }
                    webViewRef = this
                    loadUrl(normalizeUrl(urlText))
                }
            }
        )
    }
}

private fun normalizeUrl(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) {
        return "https://www.google.com"
    }

    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        trimmed.contains(".") -> "https://$trimmed"
        else -> "https://www.google.com/search?q=${trimmed.replace(" ", "+") }"
    }
}
