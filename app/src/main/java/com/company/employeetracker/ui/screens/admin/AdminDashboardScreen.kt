package com.company.employeetracker.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.EmployeeViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel
import com.company.employeetracker.ui.components.StatCard

@Composable
fun AdminDashboardScreen(
    employeeViewModel: EmployeeViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val employees by employeeViewModel.employees.collectAsState()
    val employeeCount by employeeViewModel.employeeCount.collectAsState()
    val allTasks by taskViewModel.allTasks.collectAsState()
    val allReviews by reviewViewModel.allReviews.collectAsState()
    val reviewCount by reviewViewModel.reviewCount.collectAsState()

    val completedTasks = allTasks.count { it.status == "Done" }
    val averageRating = if (allReviews.isNotEmpty()) {
        allReviews.map { it.overallRating }.average().toFloat()
    } else 0f

    // Get top performers
    val topPerformers = allReviews
        .groupBy { it.employeeId }
        .mapValues { entry -> entry.value.map { it.overallRating }.average().toFloat() }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

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
                            colors = listOf(IndigoPrimary, IndigoDark)
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
                        Column {
                            Text(
                                text = "Welcome Back! 👋",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "Admin Dashboard",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Empower your team through insights",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        IconButton(onClick = { /* Notifications */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.People,
                        value = employeeCount.toString(),
                        label = "Total Employees",
                        color = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        value = completedTasks.toString(),
                        label = "Completed",
                        color = GreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Schedule,
                        value = (allTasks.size - completedTasks).toString(),
                        label = "Pending Tasks",
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.Star,
                        value = String.format("%.1f", averageRating),
                        label = "Avg Rating",
                        color = AccentYellow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Top Performers
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Top Performers",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = AccentYellow,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}