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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.davwheat.lazycolumnstickyheaderrepro.ui.theme.LazyColumnStickyHeaderReproTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val resultsGroupedByDay =
            mapOf(
                LocalDate.parse("2025-01-01") to (1..10).toList(),
                LocalDate.parse("2025-01-02") to (1..10).toList(),
                LocalDate.parse("2025-01-03") to (1..10).toList(),
                LocalDate.parse("2025-01-04") to (1..10).toList(),
                LocalDate.parse("2025-01-05") to (1..10).toList(),
                LocalDate.parse("2025-01-06") to (1..10).toList(),
            )

        val notices =
            listOf(
                "Example notice 1",
                "Example notice 2",
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
                            Button(onClick = { scrollTo(3) }) { Text("Scroll to idx 3") }
                            Button(onClick = { scrollTo(4) }) { Text("Scroll to idx 4") }
                            Button(onClick = { scrollTo(25) }) { Text("Scroll to idx 25") }
                        }

                        LazyColumn(
                            state = lazyListState,
                        ) {
                            itemsIndexed(notices, key = { _, it -> it }) { i, it ->
                                Column(modifier = Modifier.animateItem()) {
                                    ListItem(it)

                                    if (i < notices.size - 1) {
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }

                            item(key = "less") {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    TextButton(
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            "Example button",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }

                            resultsGroupedByDay.forEach { (date, results) ->
                                stickyHeader(key = date) { ListHeader(date.formatted()) }

                                val maxIndex = results.size - 1
                                itemsIndexed(results, key = { _, result -> "$date-$result" }) {
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

                            item(key = "more") {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    TextButton(
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            "Example button",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
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
        Text(text, modifier = Modifier.padding(16.dp))
    }
}

internal fun LocalDate.formatted(): String = DateTimeFormatter.ofPattern("EEE dd MMM").format(this)
