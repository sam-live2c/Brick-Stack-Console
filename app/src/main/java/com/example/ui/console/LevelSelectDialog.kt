package com.example.ui.console

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LevelSelectDialog(
    currentStartLevel: Int,
    maxUnlockedLevel: Int,
    skin: ConsoleSkin,
    onLevelSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val itemsPerPage = 20 // 4x5 grid
    val totalLevels = 1000
    val totalPages = (totalLevels + itemsPerPage - 1) / itemsPerPage // 50 pages

    val initialPage = ((currentStartLevel.coerceIn(1, totalLevels) - 1) / itemsPerPage)
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // MODERN HEADER: "Select Level" and level range subhead
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Level",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val startLvl = pagerState.currentPage * itemsPerPage + 1
                        val endLvl = ((pagerState.currentPage + 1) * itemsPerPage).coerceAtMost(totalLevels)
                        Text(
                            text = "Levels $startLvl - $endLvl (Swipe left/right)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SWIPABLE PAGES FOR 1000 LEVELS (20 PER PAGE)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) { pageIndex ->
                    val pageStart = pageIndex * itemsPerPage + 1
                    val pageEnd = (pageStart + itemsPerPage - 1).coerceAtMost(totalLevels)
                    val count = pageEnd - pageStart + 1

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        userScrollEnabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        items(count) { index ->
                            val levelNum = pageStart + index
                            val isUnlocked = levelNum <= maxUnlockedLevel.coerceAtLeast(1)
                            val isSelected = levelNum == currentStartLevel

                            val containerColor = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isUnlocked -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            }

                            val contentColor = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isUnlocked -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            }

                            val borderColor = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isUnlocked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else -> Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1.25f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(containerColor)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = isUnlocked) {
                                        onLevelSelected(levelNum)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (isUnlocked) {
                                        Text(
                                            text = "$levelNum",
                                            color = contentColor,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = contentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "$levelNum",
                                            color = contentColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FOOTER: Simple Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Close",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
