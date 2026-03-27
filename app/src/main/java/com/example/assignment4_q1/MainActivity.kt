package com.example.assignment4_q1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GyroMazeGame()
            }
        }
    }
}

data class Wall(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    fun toRect(): Rect = Rect(left, top, right, bottom)
}

@Composable
fun GyroMazeGame() {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val gyroscope = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    // Canvas size
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    // Ball
    val ballRadius = 22f
    val startX = 70f
    val startY = 70f

    var ballX by remember { mutableStateOf(startX) }
    var ballY by remember { mutableStateOf(startY) }

    // Velocity
    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }

    // Game state
    var gameWon by remember { mutableStateOf(false) }

    // Maze walls
    val walls = remember {
        listOf(
            // Outer border
            Wall(0f, 0f, 1080f, 30f),
            Wall(0f, 0f, 30f, 1800f),
            Wall(1050f, 0f, 1080f, 1800f),
            Wall(0f, 1770f, 1080f, 1800f),

            // Inner maze
            Wall(120f, 150f, 900f, 180f),
            Wall(120f, 150f, 150f, 600f),

            Wall(250f, 300f, 1000f, 330f),
            Wall(250f, 300f, 280f, 900f),

            Wall(400f, 450f, 850f, 480f),
            Wall(820f, 450f, 850f, 1100f),

            Wall(120f, 700f, 700f, 730f),
            Wall(670f, 700f, 700f, 1400f),

            Wall(250f, 980f, 950f, 1010f),
            Wall(250f, 980f, 280f, 1500f),

            Wall(100f, 1250f, 800f, 1280f),
            Wall(800f, 1250f, 830f, 1650f),

            Wall(400f, 1500f, 1000f, 1530f),

            // Obstacles
            Wall(500f, 180f, 560f, 260f),
            Wall(600f, 560f, 690f, 640f),
            Wall(350f, 1120f, 440f, 1200f),
            Wall(900f, 1380f, 980f, 1460f)
        )
    }

    // Goal
    val goalRect = remember {
        Rect(920f, 1600f, 1020f, 1700f)
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                // During win/reset, ignore sensor input
                if (gameWon) return


                val gx = event.values[0]
                val gy = event.values[1]

                // Control movement
                velocityX += (-gy * 2.8f)
                velocityY += (gx * 2.8f)

                velocityX = velocityX.coerceIn(-18f, 18f)
                velocityY = velocityY.coerceIn(-18f, 18f)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (gyroscope != null) {
            sensorManager.registerListener(
                listener,
                gyroscope,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)

            if (gameWon) {
                velocityX = 0f
                velocityY = 0f
                continue
            }

            // Friction
            velocityX *= 0.96f
            velocityY *= 0.96f

            val nextX = ballX + velocityX
            val nextY = ballY + velocityY

            // Move on X axis
            val tryRectX = Rect(
                nextX - ballRadius,
                ballY - ballRadius,
                nextX + ballRadius,
                ballY + ballRadius
            )

            val hitX = walls.any { it.toRect().overlaps(tryRectX) }
            if (!hitX) {
                ballX = nextX.coerceIn(
                    ballRadius,
                    max(ballRadius, canvasWidth - ballRadius)
                )
            } else {
                velocityX = 0f
            }

            // Move on Y axis
            val tryRectY = Rect(
                ballX - ballRadius,
                nextY - ballRadius,
                ballX + ballRadius,
                nextY + ballRadius
            )

            val hitY = walls.any { it.toRect().overlaps(tryRectY) }
            if (!hitY) {
                ballY = nextY.coerceIn(
                    ballRadius,
                    max(ballRadius, canvasHeight - ballRadius)
                )
            } else {
                velocityY = 0f
            }

            // Win check
            val ballRect = Rect(
                ballX - ballRadius,
                ballY - ballRadius,
                ballX + ballRadius,
                ballY + ballRadius
            )

            if (goalRect.overlaps(ballRect)) {
                gameWon = true
                velocityX = 0f
                velocityY = 0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gyroscope Maze Game",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Text(
            text = if (gyroscope == null) {
                "This device does not have a gyroscope."
            } else {
                "Tilt your phone to move the ball from START to GOAL."
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasWidth = size.width
                canvasHeight = size.height

                // Background
                drawRect(Color.White)

                // Goal
                drawRect(
                    color = Color(0xFF39FF14),
                    topLeft = goalRect.topLeft,
                    size = goalRect.size
                )

                // Walls
                walls.forEach { wall ->
                    drawRect(
                        color = Color.DarkGray,
                        topLeft = Offset(wall.left, wall.top),
                        size = Size(
                            width = wall.right - wall.left,
                            height = wall.bottom - wall.top
                        )
                    )
                }

                // Ball
                drawCircle(
                    color = if (gameWon) Color(0xFF2E7D32) else Color.Red,
                    radius = ballRadius,
                    center = Offset(ballX, ballY)
                )
            }

            Text(
                text = "START",
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 18.dp, top = 10.dp)
            )

            Text(
                text = "GOAL",
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 30.dp, bottom = 110.dp)
            )

            if (gameWon) {
                Text(
                    text = "🎉 You Win!",
                    fontSize = 26.sp,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}