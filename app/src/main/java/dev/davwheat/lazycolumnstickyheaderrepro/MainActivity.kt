package dev.davwheat.lazycolumnstickyheaderrepro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.davwheat.lazycolumnstickyheaderrepro.ui.theme.LazyColumnStickyHeaderReproTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val resultsGroupedByDay =
            mapOf(
                "Group 1" to (1..10).toList(),
                "Group 2" to (1..10).toList(),
                "Group 3" to (1..10).toList(),
                "Group 4" to (1..10).toList(),
                "Group 5" to (1..10).toList(),
                "Group 6" to (1..10).toList(),
            )

        setContent {
            val lazyListState = rememberLazyListState()
            val scope = rememberCoroutineScope()

            val dividerColor = Color(0xFFEBEFF4)

            fun scrollTo(itemIndex: Int) {
                scope.launch {
                    lazyListState.stopScroll()
                    lazyListState.animateScrollToItem(index = itemIndex)
                }
            }

            LazyColumnStickyHeaderReproTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(8.dp),
                        ) {
                            Button(onClick = { scrollTo(0) }) { Text("Scroll to idx 0") }
                            Button(onClick = { scrollTo(1) }) { Text("Scroll to idx 1") }
                            Button(onClick = { scrollTo(11) }) { Text("Scroll to idx 10") }
                            Button(onClick = { scrollTo(25) }) { Text("Scroll to idx 25") }
                        }

                        LazyColumn(
                            state = lazyListState,
                        ) {
                            resultsGroupedByDay.forEach { (group, items) ->
                                stickyHeader(key = group) { ListHeader(group) }

                                val maxIndex = items.size - 1
                                itemsIndexed(items, key = { _, item -> "$group-$item" }) {
                                    index,
                                    num ->
                                    Column(
                                        Modifier.padding(
                                            top = if (index == 0) 0.dp else 5.dp,
                                            bottom = if (index == maxIndex) 0.dp else 5.dp,
                                        ),
                                    ) {
                                        HorizontalDivider(color = dividerColor)
                                        ListItem(num.toString())
                                        HorizontalDivider(color = dividerColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListHeader(text: String) {
    Surface(color = Color.DarkGray, modifier = Modifier.fillMaxWidth()) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
        )
    }
}

@Composable
fun ListItem(text: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
        Text(text, modifier = Modifier.padding(48.dp))
    }
}
