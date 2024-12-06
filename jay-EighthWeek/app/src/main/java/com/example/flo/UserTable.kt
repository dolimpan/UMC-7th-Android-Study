package com.example.flo

import androidx.room.*

@Entity(tableName = "user_table")
data class UserTable(
    @PrimaryKey val id: String,
    val password: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table WHERE id = :id AND password = :password")
    suspend fun login(id: String, password: String): UserTable?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserTable)
    @Query("SELECT * FROM user_table")
    suspend fun getAllUsers(): List<UserTable>
}