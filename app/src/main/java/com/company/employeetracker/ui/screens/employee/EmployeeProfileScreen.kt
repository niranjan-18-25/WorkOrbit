package com.company.employeetracker.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.AuthViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel

@Composable
fun EmployeeProfileScreen(
    currentUser: User,
    onLogout: () -> Unit = {},
    onNavigateToSelectEmployee: () -> Unit = {}, // ADDED PARAMETER
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    LaunchedEffect(currentUser.id) {
        taskViewModel.loadTasksForEmployee(currentUser.id)
        reviewViewModel.loadReviewsForEmployee(currentUser.id)
    }

    val tasks by taskViewModel.employeeTasks.collectAsState()
    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()

    // WRAP EVERYTHING IN SCAFFOLD
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSelectEmployee,
                containerColor = GreenPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "New Message",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        // PUT YOUR EXISTING LazyColumn HERE with padding
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues) // ADDED padding for scaffold content
        ) {
            // Header with Avatar
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.name.split(" ").mapNotNull { it.firstOrNull() }
                                    .take(2).joinToString(""),
                                color = GreenPrimary,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = currentUser.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = currentUser.designation,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentUser.email,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { /* TODO: Edit profile */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = GreenPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Stats Cards
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Tasks",
                                tint = AccentBlue,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tasks.size.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Tasks",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Reviews",
                                tint = AccentOrange,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = reviews.size.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Reviews",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = AccentYellow,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = String.format("%.1f", averageRating),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Avg Rating",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            // Settings Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Preferences",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Default.Notifications,
                            title = "Push Notifications",
                            subtitle = "Get notified about important updates",
                            iconColor = AccentRed,
                            hasSwitch = true
                        )
                        Divider()
                        SettingItem(
                            icon = Icons.Default.CloudUpload,
                            title = "Auto Backup",
                            subtitle = "Automatically backup your data",
                            iconColor = AccentBlue,
                            hasSwitch = true
                        )
                    }
                }
            }

            // Appearance Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Appearance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Default.DarkMode,
                            title = "Dark Mode",
                            subtitle = "Enable dark theme for better viewing",
                            iconColor = Color(0xFF673AB7),
                            hasSwitch = true
                        )
                        Divider()
                        SettingItem(
                            icon = Icons.Default.GridView,
                            title = "Compact View",
                            subtitle = "Show more items on screen",
                            iconColor = Color(0xFFFF9800),
                            hasSwitch = true
                        )
                    }
                }
            }

            // Reports Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Reports & Export",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Default.FileDownload,
                            title = "Export Summary Report",
                            subtitle = "Download comprehensive CSV report",
                            iconColor = GreenPrimary,
                            hasArrow = true
                        )
                        Divider()
                        SettingItem(
                            icon = Icons.Default.PictureAsPdf,
                            title = "Generate PDF Report",
                            subtitle = "Create PDF with charts and analytics",
                            iconColor = AccentRed,
                            hasArrow = true
                        )
                        Divider()
                        SettingItem(
                            icon = Icons.Default.Share,
                            title = "Share Analytics",
                            subtitle = "Share performance insights",
                            iconColor = AccentBlue,
                            hasArrow = true
                        )
                    }
                }
            }

            // Support Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Support & Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Default.Email,
                            title = "Contact Support",
                            subtitle = "Get help from our support team",
                            iconColor = AccentBlue,
                            hasArrow = true
                        )
                        Divider()
                        SettingItem(
                            icon = Icons.Default.Feedback,
                            title = "Send Feedback",
                            subtitle = "Help us improve the app",
                            iconColor = AccentYellow,
                            hasArrow = true
                        )
                        Divider()
                        SettingItem(
                            icon = Icons.Default.Info,
                            title = "About Application",
                            subtitle = "Version 1.0.0 • Employee Tracker",
                            iconColor = Color(0xFF757575),
                            hasArrow = true
                        )
                    }
                }
            }

            // Danger Zone
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Danger Zone",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentRed,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    SettingItem(
                        icon = Icons.Default.Delete,
                        title = "Reset All Data",
                        subtitle = "This will permanently delete all data",
                        iconColor = AccentRed,
                        hasArrow = true
                    )
                }
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Logo",
                        tint = Color(0xFFBDBDBD),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Employee Performance Tracker",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "Measure what matters — Empower performance through data-driven insights.",
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD),
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "● Developed by MindMatrix Interns",
                        fontSize = 11.sp,
                        color = Color(0xFFBDBDBD)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    } // END SCAFFOLD
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    hasSwitch: Boolean = false,
    hasArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    var checked by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }

            if (hasSwitch) {
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = iconColor
                    )
                )
            }

            if (hasArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Arrow",
                    tint = Color(0xFFBDBDBD)
                )
            }
        }
    }
}
