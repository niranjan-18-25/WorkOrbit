package com.company.employeetracker.ui.screens.employee

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.ReviewViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EmployeeReviewsScreen(
    currentUser: User,
    onBackClick: () -> Unit = {},
    reviewViewModel: ReviewViewModel = viewModel()
) {
    LaunchedEffect(currentUser.id) {
        reviewViewModel.loadReviewsForEmployee(currentUser.id)
    }

    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val latestReview = reviews.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(GreenPrimary, GreenDark)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = { /* More options */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Reviews",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Performance Reviews",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${reviews.size} review${if (reviews.size != 1) "s" else ""} received",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Overall Rating Card
        if (latestReview != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Overall Rating",
                                fontSize = 16.sp,
                                color = Color(0xFF757575)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format("%.1f", latestReview.overallRating),
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = " / 5.0",
                                    fontSize = 24.sp,
                                    color = Color(0xFF757575),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    latestReview.overallRating >= 4.5f -> GreenLight.copy(alpha = 0.2f)
                                    latestReview.overallRating >= 3.5f -> AccentOrange.copy(alpha = 0.2f)
                                    else -> AccentRed.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = when {
                                        latestReview.overallRating >= 4.5f -> "Excellent"
                                        latestReview.overallRating >= 3.5f -> "Good"
                                        else -> "Needs Improvement"
                                    },
                                    color = when {
                                        latestReview.overallRating >= 4.5f -> GreenPrimary
                                        latestReview.overallRating >= 3.5f -> AccentOrange
                                        else -> AccentRed
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(AccentYellow.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = AccentYellow,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
            }

            // Skills Overview
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Skills Overview",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Chart",
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SkillBar("Quality", latestReview.quality, GreenPrimary)
                            SkillBar("Communication", latestReview.communication, AccentBlue)
                            SkillBar("Innovation", latestReview.innovation, PurplePrimary)
                            SkillBar("Timeliness", latestReview.timeliness, AccentOrange)
                            SkillBar("Attendance", latestReview.attendance, AccentGreen)
                        }
                    }
                }
            }

            // Performance Radar
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Performance Radar",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Radar",
                                    tint = AccentRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        RadarChart(
                            values = listOf(
                                latestReview.quality,
                                latestReview.communication,
                                latestReview.innovation,
                                latestReview.timeliness,
                                latestReview.attendance
                            ),
                            labels = listOf("Quality", "Communication", "Innovation", "Timeliness", "Attendance"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                }
            }

            // Skill Breakdown
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Skill Breakdown",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Breakdown",
                                tint = AccentOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SkillProgressBar("Quality", latestReview.quality, GreenPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressBar("Communication", latestReview.communication, AccentBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressBar("Innovation", latestReview.innovation, PurplePrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressBar("Timeliness", latestReview.timeliness, AccentOrange)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressBar("Attendance", latestReview.attendance, AccentGreen)
                    }
                }
            }
        }

        // Review History Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Review History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Review History Items
        items(reviews) { review ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Reviewer",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = String.format("%.1f/5.0", review.overallRating),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = review.date,
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                review.overallRating >= 4.5f -> GreenLight.copy(alpha = 0.1f)
                                review.overallRating >= 3.5f -> AccentOrange.copy(alpha = 0.1f)
                                else -> AccentRed.copy(alpha = 0.1f)
                            }
                        ) {
                            Text(
                                text = when {
                                    review.overallRating >= 4.5f -> "Excellent"
                                    review.overallRating >= 3.5f -> "Good"
                                    else -> "Fair"
                                },
                                color = when {
                                    review.overallRating >= 4.5f -> GreenPrimary
                                    review.overallRating >= 3.5f -> AccentOrange
                                    else -> AccentRed
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = review.remarks,
                        fontSize = 14.sp,
                        color = Color(0xFF424242),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Skill Ratings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SkillRatingItem("Quality", review.quality)
                        SkillRatingItem("Communication", review.communication)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SkillRatingItem("Innovation", review.innovation)
                        SkillRatingItem("Timeliness", review.timeliness)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SkillRatingItem("Attendance", review.attendance, modifier = Modifier.fillMaxWidth(0.5f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// Helper Composables
@Composable
fun SkillBar(label: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format("%.1f", value),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(80.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight((value / 5f))
                    .background(color, RoundedCornerShape(2.dp))
                    .align(Alignment.BottomCenter)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF757575),
            maxLines = 1
        )
    }
}

@Composable
fun SkillProgressBar(label: String, value: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = String.format("%.1f/5", value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = value / 5f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
fun SkillRatingItem(label: String, rating: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = label,
                tint = AccentYellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun RadarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    maxValue: Float = 5f
) {
    Canvas(modifier = modifier.padding(16.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f * 0.7f
        val angleStep = 360f / values.size

        // Draw background polygons
        for (level in 5 downTo 1) {
            val levelRadius = radius * (level / 5f)
            val path = Path()
            for (i in values.indices) {
                val angle = Math.toRadians((angleStep * i - 90).toDouble())
                val x = center.x + (levelRadius * cos(angle)).toFloat()
                val y = center.y + (levelRadius * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(
                path = path,
                color = Color(0xFFE0E0E0),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw axes
        for (i in values.indices) {
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val endX = center.x + (radius * cos(angle)).toFloat()
            val endY = center.y + (radius * sin(angle)).toFloat()
            drawLine(
                color = Color(0xFFE0E0E0),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw data polygon
        val dataPath = Path()
        for (i in values.indices) {
            val normalizedValue = values[i] / maxValue
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val x = center.x + (radius * normalizedValue * cos(angle)).toFloat()
            val y = center.y + (radius * normalizedValue * sin(angle)).toFloat()
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()

        drawPath(
            path = dataPath,
            color = GreenPrimary.copy(alpha = 0.3f)
        )
        drawPath(
            path = dataPath,
            color = GreenPrimary,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}