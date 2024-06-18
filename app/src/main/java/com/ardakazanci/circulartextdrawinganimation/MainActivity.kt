package com.ardakazanci.circulartextdrawinganimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardakazanci.circulartextdrawinganimation.ui.theme.CircularTextDrawingAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CircularTextDrawingAnimationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularTextAnimation()
                }
            }
        }
    }
}


@Composable
fun CircularTextAnimation() {
    var text by remember { mutableStateOf("NO WORRY • NO STRESS •") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "rotationAnimation"
        )

        Spacer(modifier = Modifier.height(120.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text") },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        Spacer(modifier = Modifier.height(240.dp))

        CircularText(
            modifier = Modifier.wrapContentHeight(),
            text = text,
            textSize = 48.sp,
            color = Color.White,
            shadowColor = Color.Black,
            shadowOffset = Offset(8f, 8f),
            rotation = rotation
        )

    }
}

@Composable
fun CircularText(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    color: Color,
    shadowColor: Color,
    shadowOffset: Offset,
    rotation: Float
) {
    val radius = with(LocalDensity.current) { 100.dp.toPx() }
    val textPaint = android.graphics.Paint().apply {
        this.color = color.toArgb()
        this.textSize = with(LocalDensity.current) { textSize.toPx() }
        this.isAntiAlias = true
        this.style = android.graphics.Paint.Style.FILL
    }
    val shadowPaint = android.graphics.Paint().apply {
        this.color = shadowColor.toArgb()
        this.textSize = with(LocalDensity.current) { textSize.toPx() }
        this.isAntiAlias = true
        this.style = android.graphics.Paint.Style.FILL
    }

    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val charAngle = 360f / text.length

        rotate(rotation, Offset(centerX, centerY)) {
            for (i in text.indices) {
                val angle = i * charAngle - 90
                val x =
                    centerX + radius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val y =
                    centerY + radius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()

                rotate(
                    degrees = angle + 90,
                    pivot = Offset(x + shadowOffset.x, y + shadowOffset.y)
                ) {
                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            text[i].toString(),
                            x + shadowOffset.x,
                            y + shadowOffset.y,
                            shadowPaint
                        )
                    }
                }

                rotate(degrees = angle + 90, pivot = Offset(x, y)) {
                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            text[i].toString(),
                            x,
                            y,
                            textPaint
                        )
                    }
                }
            }
        }
    }
}

