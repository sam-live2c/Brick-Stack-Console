package com.example.ui.console

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun TouchGestureOverlay(
    enabled: Boolean,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onRotateClockwise: () -> Unit,
    onHoldPiece: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 35f // Pixels needed per cell shift

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onRotateClockwise()
                    },
                    onDoubleTap = {
                        onHardDrop()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        // Horizontal Move
                        if (abs(totalDragX) >= dragThreshold && abs(totalDragX) > abs(totalDragY)) {
                            if (totalDragX > 0) {
                                onMoveRight()
                            } else {
                                onMoveLeft()
                            }
                            totalDragX = 0f
                        }

                        // Vertical Drag
                        if (abs(totalDragY) >= dragThreshold && abs(totalDragY) > abs(totalDragX)) {
                            if (totalDragY > 0) {
                                onSoftDrop()
                            } else {
                                onHoldPiece()
                            }
                            totalDragY = 0f
                        }
                    }
                )
            }
    )
}
