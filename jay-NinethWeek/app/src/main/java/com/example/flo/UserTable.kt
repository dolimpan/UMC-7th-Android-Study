package com.example.flo

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_table")
data class UserTable(
    @SerializedName(value = "name") val name: String,
    @SerializedName(value = "email")  @PrimaryKey val id: String,
    @SerializedName(value = "password") val password: String
)

data class UserLogin(
    @SerializedName(value = "email") val id: String,
    @SerializedName(value = "password") val pw: String
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