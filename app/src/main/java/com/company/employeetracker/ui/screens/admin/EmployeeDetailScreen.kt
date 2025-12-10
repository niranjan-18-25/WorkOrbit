package com.company.employeetracker.ui.screens.admin

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.Attendance
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.AttendanceViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.clickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    // The `employees` list lets the admin choose "All" or one employee. Default keeps backward compatibility.
    employee: User,
    employees: List<User> = listOf(employee),
    // Default times can be supplied by admin settings (inject these from ViewModel).
    defaultCheckIn: LocalTime = LocalTime.of(9, 0),
    defaultCheckOut: LocalTime = LocalTime.of(18, 0),
    onBackClick: () -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    LaunchedEffect(employee.id) {
        taskViewModel.loadTasksForEmployee(employee.id)
        reviewViewModel.loadReviewsForEmployee(employee.id)
        attendanceViewModel.loadAttendanceForEmployee(employee.id)

        // OPTIONAL: If admin defaults are stored in AttendanceViewModel, fetch them here and override defaults:
        // val adminDefaults = attendanceViewModel.getAdminAttendanceDefaults() // implement in VM
        // defaultCheckIn = adminDefaults.checkIn; defaultCheckOut = adminDefaults.checkOut
    }

    val tasks by taskViewModel.employeeTasks.collectAsState()
    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val attendance by attendanceViewModel.employeeAttendance.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()

    var showMarkAttendanceDialog by remember { mutableStateOf(false) }

    val avatarColor = when (employee.department) {
        "Design" -> Color(0xFF9C27B0)
        "Engineering" -> AccentBlue
        "Analytics" -> AccentOrange
        "Product" -> Color(0xFF00BCD4)
        else -> GreenPrimary
    }

    // --- New state: selected employee index for dropdown. index 0 = "All Employees", else employees[index-1]
    var expandedEmployeeMenu by remember { mutableStateOf(false) }
    var selectedEmployeeIndex by remember { mutableStateOf(1) } // default to the given `employee` (1)
    // Build display list where first item is "All Employees"
    val employeeMenuList = listOf("All Employees") + employees.map { it.name }

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
                            colors = listOf(avatarColor, avatarColor.copy(alpha = 0.8f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = employee.name.split(" ").mapNotNull { it.firstOrNull() }
                                .take(2).joinToString(""),
                            color = avatarColor,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // --- Updated: show dropdown for employee selection / all employees
                        ExposedDropdownMenuBox(
                            expanded = expandedEmployeeMenu,
                            onExpandedChange = { expandedEmployeeMenu = it }
                        ) {
                            TextField(
                                value = employeeMenuList.getOrNull(selectedEmployeeIndex) ?: employee.name,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (expandedEmployeeMenu) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Select employee"
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent
                                ) ,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedEmployeeMenu,
                                onDismissRequest = { expandedEmployeeMenu = false }
                            ) {
                                employeeMenuList.forEachIndexed { index, label ->
                                    DropdownMenuItem(
                                        text = { Text(text = label) },
                                        onClick = {
                                            selectedEmployeeIndex = index
                                            expandedEmployeeMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = employee.designation,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = employee.department,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Stats Cards (unchanged)
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
                            imageVector = Icons.Default.Assignment,
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
                            text = "Tasks",
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
                            text = "Rating",
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
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Present",
                            tint = GreenPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = attendance.count { it.status == "Present" }.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Present",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }
        }

        // Employee Info (unchanged)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Employee Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow("Email", employee.email)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Department", employee.department)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Designation", employee.designation)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Joining Date", employee.joiningDate)
                    if (employee.contact.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow("Contact", employee.contact)
                    }
                }
            }
        }

        // Mark Attendance Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showMarkAttendanceDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Mark Attendance"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mark Attendance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Attendance History (unchanged)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${attendance.size} records",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        }

        if (attendance.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No attendance records",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }
        } else {
            items(attendance.take(10)) { record ->
                Spacer(modifier = Modifier.height(12.dp))
                AttendanceRecordCard(record, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Mark Attendance Dialog
    if (showMarkAttendanceDialog) {
        // Determine which employee(s) the admin selected:
        val selectedEmployee: User? = if (selectedEmployeeIndex == 0) null else employees.getOrNull(selectedEmployeeIndex - 1)
        MarkAttendanceDialog(
            selectedEmployee = selectedEmployee ?: employee, // used for display when marking single employee
            isAllSelected = (selectedEmployee == null),
            employees = employees,
            defaultCheckIn = defaultCheckIn,
            defaultCheckOut = defaultCheckOut,
            onDismiss = { showMarkAttendanceDialog = false },
            onAttendanceMarked = { attendanceItem ->
                // If "All" was selected, mark for each employee using the admin defaults/values
                if (selectedEmployee == null) {
                    employees.forEach { emp ->
                        val copy = attendanceItem.copy(employeeId = emp.id)
                        attendanceViewModel.markAttendance(copy)
                    }
                } else {
                    attendanceViewModel.markAttendance(attendanceItem)
                }
                showMarkAttendanceDialog = false
            }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun AttendanceRecordCard(record: Attendance, modifier: Modifier = Modifier) {
    val statusColor = when (record.status) {
        "Present" -> GreenPrimary
        "Absent" -> AccentRed
        "Half Day" -> AccentOrange
        "Leave" -> AccentBlue
        else -> Color.Gray
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (record.status) {
                        "Present" -> Icons.Default.CheckCircle
                        "Absent" -> Icons.Default.Cancel
                        "Half Day" -> Icons.Default.Schedule
                        "Leave" -> Icons.Default.EventBusy
                        else -> Icons.Default.HelpOutline
                    },
                    contentDescription = record.status,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
                // Keep stored times (record.checkInTime/checkOutTime) — expected to be in hh:mm a format if you save that way
                Text(
                    text = "Check In: ${record.checkInTime}${if (record.checkOutTime != null) " • Check Out: ${record.checkOutTime}" else ""}",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
                if (record.remarks.isNotEmpty()) {
                    Text(
                        text = record.remarks,
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E),
                        maxLines = 1
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = record.status,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceDialog(
    selectedEmployee: User,
    isAllSelected: Boolean,
    employees: List<User> = listOf(selectedEmployee),
    defaultCheckIn: LocalTime = LocalTime.of(9, 0),
    defaultCheckOut: LocalTime = LocalTime.of(18, 0),
    onDismiss: () -> Unit,
    onAttendanceMarked: (Attendance) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("Present") }

    // store LocalTime internally and format as hh:mm a for display
    var checkInLocal by remember { mutableStateOf(defaultCheckIn) }
    var checkOutLocal by remember { mutableStateOf(defaultCheckOut) }
    var remarks by remember { mutableStateOf("") }

    val statuses = listOf("Present", "Absent", "Half Day", "Leave")

    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    // Time picker helper
    fun openTimePicker(initial: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
        val hour = initial.hour
        val minute = initial.minute
        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
        }, hour, minute, false).show() // false => 12-hour format displayed
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAllSelected) "Mark Attendance — All Employees" else "Mark Attendance",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAllSelected) "${employees.size} employees" else selectedEmployee.name,
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Selection: 2x2 grid
                Text(
                    text = "Status",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    statuses.chunked(2).forEach { rowStatuses ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowStatuses.forEach { status ->
                                FilterChip(
                                    selected = selectedStatus == status,
                                    onClick = { selectedStatus = status },
                                    label = { Text(status) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowStatuses.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Fields with native TimePicker and AM/PM support
                OutlinedTextField(
                    value = checkInLocal.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("Check In Time") },
                    placeholder = { Text("hh:mm AM") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            openTimePicker(checkInLocal) { newTime -> checkInLocal = newTime }
                        },
                    trailingIcon = {
                        IconButton(onClick = { openTimePicker(checkInLocal) { newTime -> checkInLocal = newTime } }) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Pick time")
                        }
                    },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = checkOutLocal.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("Check Out Time (Optional)") },
                    placeholder = { Text("hh:mm PM") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            openTimePicker(checkOutLocal) { newTime -> checkOutLocal = newTime }
                        },
                    trailingIcon = {
                        IconButton(onClick = { openTimePicker(checkOutLocal) { newTime -> checkOutLocal = newTime } }) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Pick time")
                        }
                    },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remarks
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (Optional)") },
                    placeholder = { Text("Any additional notes...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Format times to store as "hh:mm a" string so AM/PM are saved
                            val formattedCheckIn = checkInLocal.format(timeFormatter)
                            val formattedCheckOut = checkOutLocal.format(timeFormatter)

                            // When marking for all, `employeeId` will be overwritten in caller loop.
                            val attendance = Attendance(
                                employeeId = selectedEmployee.id,
                                date = LocalDate.now().toString(),
                                checkInTime = formattedCheckIn,
                                checkOutTime = formattedCheckOut,
                                status = selectedStatus,
                                remarks = remarks
                            )
                            onAttendanceMarked(attendance)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mark")
                    }
                }
            }
        }
    }
}
