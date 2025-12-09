package com.company.employeetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.company.employeetracker.data.database.dao.MessageDao
import com.company.employeetracker.data.database.dao.ReviewDao
import com.company.employeetracker.data.database.dao.TaskDao
import com.company.employeetracker.data.database.dao.UserDao
import com.company.employeetracker.data.database.entities.Message
import com.company.employeetracker.data.database.entities.Review
import com.company.employeetracker.data.database.entities.Task
import com.company.employeetracker.data.database.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Task::class, Review::class, Message::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun reviewDao(): ReviewDao
    abstract fun messageDao(): MessageDao

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
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                // If DB empty, populate with only admin user
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (instance.userDao().getEmployeeCount() == 0) {
                            populateDatabase(
                                instance.userDao(),
                                instance.taskDao(),
                                instance.reviewDao(),
                                instance.messageDao()
                            )
                        }
                    } catch (e: Exception) {
                        // Log or handle as needed; avoid crashing on DB checks
                        android.util.Log.e("AppDatabase", "Error populating DB: ${e.message}")
                    }
                }

                instance
            }
        }

        suspend fun populateDatabase(
            userDao: UserDao,
            taskDao: TaskDao,
            reviewDao: ReviewDao,
            messageDao: MessageDao
        ) {
            // Insert only the admin user (authenticator)
            userDao.insertUser(
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

            // Note: No sample employees, tasks, reviews, or messages are added anymore.
            // If you need to add sample data later for testing, do so from a debug function
            // or guarded code path so production devices don't get test data.
        }
    }
}
