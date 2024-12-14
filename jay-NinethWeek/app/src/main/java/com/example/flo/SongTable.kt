package com.example.flo

import androidx.room.*

// 1. Entity 정의
@Entity(tableName = "song_table")
data class SongTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val singer: String,
    val second: Int,
    val isPlaying: Boolean,
    val coverImg: Int,
    val liked: Boolean,
    val albumIdx: String,
    val mp3:Int
)

// 2. DAO 정의
@Dao
interface SongTableDAO {
    @Insert
    suspend fun insertSong(song: SongTable)

    @Query("SELECT * FROM song_table")
    fun getAllSongs(): List<SongTable>

    @Query("SELECT * FROM song_table WHERE title = :title AND singer = :singer LIMIT 1")
    suspend fun Check(title: String, singer: String): AlbumTable?

    @Query("SELECT * FROM song_table WHERE albumIdx = :albumIdx")
    suspend fun getSongsByAlbumIdx(albumIdx: String): List<SongTable>

    @Query("SELECT * FROM song_table WHERE title = :indexId LIMIT 1")
    suspend fun getSongByIndexId(indexId: String): SongTable?

    @Query("UPDATE song_table SET liked = :isLiked WHERE title = :songId")
    suspend fun updateLikedStatus(songId: String, isLiked: Boolean)

    @Query("SELECT * FROM song_table WHERE liked = 1")
    suspend fun getLikedSongs(): List<SongTable>

    @Query("UPDATE song_table SET liked = 0 WHERE liked = 1")
    suspend fun resetLikedSongs()

}



@Entity(tableName = "album_table")
data class AlbumTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val singer: String,
    val coverImg: Int
)

// 2. DAO 정의
@Dao
interface AlbumTableDAO {

    @Query("SELECT title, coverImg, singer FROM album_table WHERE id = :id LIMIT 1")
    suspend fun getAlbumDataById(id: Int): AlbumData

    @Query("SELECT id FROM album_table WHERE title = :title LIMIT 1")
    suspend fun getIdByTitle(title: String): Int?

    @Insert
    suspend fun insertAlbum(album: AlbumTable)

    @Query("SELECT * FROM album_table")
    fun getAllAlbums(): List<AlbumTable>

    @Query("DELETE FROM album_table") // 테이블 비우기
    suspend fun deleteAllAlbums()

    @Query("SELECT * FROM album_table WHERE title = :title AND singer = :singer LIMIT 1")
    suspend fun Check(title: String, singer: String): AlbumTable?

}

@Entity(tableName = "liked_album")
data class LikedAlbum(
    @PrimaryKey(autoGenerate = false) val cnt: Int = 0,
    val id: Int = 0,
    val userId: String
)

@Dao
interface LikedAlbumDAO
{
    @Query("SELECT EXISTS(SELECT 1 FROM liked_album WHERE id = :id AND userId = :user)")
    suspend fun isAlbumLiked(id: Int, user : String): Boolean

    @Query("DELETE FROM liked_album WHERE id = :id AND userId = :user")
    suspend fun deleteLikedAlbum(id: Int, user: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedAlbum(likedAlbum: LikedAlbum)

    @Query("SELECT * FROM liked_album")
    suspend fun getAllLikedAlbums(): List<LikedAlbum>

    @Query("SELECT id FROM liked_album WHERE userId = :userId")
    suspend fun getIdsByUserId(userId: String): List<Int>

}



@Database(entities = [SongTable::class, AlbumTable::class, UserTable::class, LikedAlbum::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun SongTableDAO(): SongTableDAO
    abstract fun AlbumTableDAO(): AlbumTableDAO
    abstract fun UserDao(): UserDao
    abstract fun LikedAlbumDAO(): LikedAlbumDAO

}