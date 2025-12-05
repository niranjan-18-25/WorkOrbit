package com.company.employeetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.company.employeetracker.data.database.dao.ReviewDao
import com.company.employeetracker.data.database.dao.TaskDao
import com.company.employeetracker.data.database.dao.UserDao
import com.company.employeetracker.data.database.entities.Review
import com.company.employeetracker.data.database.entities.Task
import com.company.employeetracker.data.database.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Task::class, Review::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "employee_tracker_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.userDao(), database.taskDao(), database.reviewDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao, taskDao: TaskDao, reviewDao: ReviewDao) {
            // Add default admin
            val adminId = userDao.insertUser(
                User(
                    email = "admin@company.com",
                    password = "admin123",
                    name = "Admin User",
                    role = "admin",
                    designation = "System Administrator",
                    department = "Management",
                    joiningDate = "2024-01-01"
                )
            )

            // Add sample employees
            val employees = listOf(
                User(
                    email = "niranjan@company.com",
                    password = "pass123",
                    name = "Niranjan",
                    role = "employee",
                    designation = "UX Designer",
                    department = "Design",
                    joiningDate = "2023-03-15"
                ),
                User(
                    email = "manoj@company.com",
                    password = "pass123",
                    name = "Manoj",
                    role = "employee",
                    designation = "Senior Developer",
                    department = "Engineering",
                    joiningDate = "2023-01-10"
                ),
                User(
                    email = "krish@company.com",
                    password = "pass123",
                    name = "Krish",
                    role = "employee",
                    designation = "Data Analyst",
                    department = "Analytics",
                    joiningDate = "2023-06-20"
                ),
                User(
                    email = "ram@company.com",
                    password = "pass123",
                    name = "Ram",
                    role = "employee",
                    designation = "Product Manager",
                    department = "Product",
                    joiningDate = "2023-02-14"
                )
            )

            val employeeIds = employees.map { userDao.insertUser(it).toInt() }

            // Add sample tasks for each employee
            employeeIds.forEachIndexed { index, empId ->
                taskDao.insertTask(
                    Task(
                        employeeId = empId,
                        title = "Design Login Screen",
                        description = "Create UI mockups for the login page",
                        priority = "High",
                        status = "Pending",
                        deadline = "2024-03-20",
                        assignedDate = "2024-03-01"
                    )
                )
                taskDao.insertTask(
                    Task(
                        employeeId = empId,
                        title = "Database Schema",
                        description = "Design and implement database structure",
                        priority = "Critical",
                        status = "Done",
                        deadline = "2024-03-22",
                        assignedDate = "2024-03-01"
                    )
                )
                taskDao.insertTask(
                    Task(
                        employeeId = empId,
                        title = "API Documentation",
                        description = "Write comprehensive API docs",
                        priority = "Medium",
                        status = "Active",
                        deadline = "2024-03-25",
                        assignedDate = "2024-03-05"
                    )
                )
            }

            // Add sample reviews
            employeeIds.forEachIndexed { index, empId ->
                reviewDao.insertReview(
                    Review(
                        employeeId = empId,
                        date = "2024-10-15",
                        quality = 4.5f + (index * 0.1f),
                        communication = 4.0f + (index * 0.2f),
                        innovation = 4.8f,
                        timeliness = 4.2f,
                        attendance = 5.0f,
                        overallRating = 4.5f + (index * 0.1f),
                        remarks = "Excellent performance and great team collaboration.",
                        reviewedBy = "Admin User"
                    )
                )
            }
        }
    }
}