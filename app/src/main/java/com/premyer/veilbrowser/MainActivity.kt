package com.premyer.veilbrowser

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeilBrowserTheme(darkTheme = false) {
                BrowserApp()
            }
        }
    }
}

data class BrowserTab(
    val id: Int,
    val title: String,
    val url: String
)

@Composable
fun BrowserApp() {
    var darkMode by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var homePage by rememberSaveable { mutableStateOf("https://www.google.com") }
    var urlText by rememberSaveable { mutableStateOf("https://www.google.com") }

    val tabs = remember {
        mutableStateOf(listOf(BrowserTab(1, "Google", "https://www.google.com")))
    }
    var activeTabId by rememberSaveable { mutableStateOf(1) }
    var currentPageTitle by rememberSaveable { mutableStateOf("Google") }
    var canGoBack by rememberSaveable { mutableStateOf(false) }
    var canGoForward by rememberSaveable { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val activeTab = tabs.value.firstOrNull { it.id == activeTabId } ?: tabs.value.first()

    fun updateTabState(title: String, url: String) {
        tabs.value = tabs.value.map {
            if (it.id == activeTabId) it.copy(title = title, url = url) else it
        }
        currentPageTitle = title
        urlText = url
    }

    val loadUrl: (String) -> Unit = { input ->
        val normalized = normalizeUrl(input)
        urlText = normalized
        val updatedTabs = tabs.value.map {
            if (it.id == activeTabId) it.copy(url = normalized) else it
        }
        tabs.value = updatedTabs
        webViewRef?.loadUrl(normalized)
    }

    VeilBrowserTheme(darkTheme = darkMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Veil Browser",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row {
                            IconButton(onClick = {
                                val nextId = (tabs.value.maxOfOrNull { it.id } ?: 0) + 1
                                val newTab = BrowserTab(nextId, "Neu", homePage)
                                tabs.value = tabs.value + newTab
                                activeTabId = nextId
                                urlText = homePage
                                webViewRef?.loadUrl(normalizeUrl(homePage))
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Neuer Tab")
                            }
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menü")
                            }
                            IconButton(onClick = { showSettings = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Einstellungen")
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { if (webViewRef?.canGoBack() == true) webViewRef?.goBack() }, enabled = canGoBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                        }
                        IconButton(onClick = { if (webViewRef?.canGoForward() == true) webViewRef?.goForward() }, enabled = canGoForward) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Vorwärts")
                        }
                        IconButton(onClick = { webViewRef?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren")
                        }
                        IconButton(onClick = { loadUrl(homePage) }) {
                            Icon(Icons.Default.Home, contentDescription = "Startseite")
                        }
                        IconButton(onClick = { webViewRef?.stopLoading() }) {
                            Icon(Icons.Default.Close, contentDescription = "Stop")
                        }
                    }

                    OutlinedTextField(
                        value = urlText,
                        onValueChange = { urlText = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            Button(
                                onClick = { loadUrl(urlText) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Go")
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.value.forEach { tab ->
                    val selected = tab.id == activeTabId
                    Button(
                        onClick = {
                            activeTabId = tab.id
                            urlText = tab.url
                            webViewRef?.loadUrl(normalizeUrl(tab.url))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(tab.title.take(10))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
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
                            settings.allowFileAccess = true
                            settings.databaseEnabled = true

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    if (url != null) {
                                        urlText = url
                                        updateTabState(view?.title ?: "Seite", url)
                                    }
                                    canGoBack = view?.canGoBack() == true
                                    canGoForward = view?.canGoForward() == true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    if (url != null) {
                                        urlText = url
                                        updateTabState(view?.title ?: "Seite", url)
                                    }
                                    canGoBack = view?.canGoBack() == true
                                    canGoForward = view?.canGoForward() == true
                                }
                            }

                            webViewRef = this
                            loadUrl(normalizeUrl(homePage))
                        }
                    },
                    update = { webView ->
                        webViewRef = webView
                        if (webView.url == null || webView.url != normalizeUrl(urlText)) {
                            webView.loadUrl(normalizeUrl(urlText))
                        }
                    }
                )
            }
        }

        if (showSettings) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Einstellungen", style = MaterialTheme.typography.titleLarge)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dunkelmodus")
                            Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                        }
                        OutlinedTextField(
                            value = homePage,
                            onValueChange = { homePage = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("Startseite") }
                        )
                        Button(
                            onClick = { showSettings = false },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Schließen")
                        }
                    }
                }
            }
        }

        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.30f)),
                contentAlignment = Alignment.TopEnd
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .padding(top = 80.dp, end = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MenuActionRow(icon = Icons.Default.Home, title = "Startseite", subtitle = "Google als Standard") { homePage = "https://www.google.com"; showMenu = false }
                        MenuActionRow(icon = Icons.Default.Star, title = "Favoriten", subtitle = "GitHub, Wikipedia, Google") { loadUrl("https://github.com"); showMenu = false }
                        MenuActionRow(icon = Icons.Default.Info, title = "Info", subtitle = "Veil Browser v1.0") { currentPageTitle = "Veil Browser v1.0"; showMenu = false }
                        MenuActionRow(icon = Icons.Default.Settings, title = "Einstellungen", subtitle = "Theme & Startseite") { showSettings = true; showMenu = false }
                        MenuActionRow(icon = Icons.Default.MoreVert, title = "Mehr", subtitle = "Browser-Extras") { showMenu = false }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = title)
            Column(horizontalAlignment = Alignment.Start) {
                Text(title, fontSize = 16.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun normalizeUrl(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return "https://www.google.com"
    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        trimmed.contains(".") -> "https://$trimmed"
        else -> "https://www.google.com/search?q=${trimmed.replace(" ", "+") }"
    }
}
