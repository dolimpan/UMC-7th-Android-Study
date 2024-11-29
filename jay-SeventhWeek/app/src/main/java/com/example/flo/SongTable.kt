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
    @Insert
    suspend fun insertAlbum(album: AlbumTable)

    @Query("SELECT * FROM album_table")
    fun getAllAlbums(): List<AlbumTable>

    @Query("DELETE FROM album_table") // 테이블 비우기
    suspend fun deleteAllAlbums()

    @Query("SELECT * FROM album_table WHERE title = :title AND singer = :singer LIMIT 1")
    suspend fun Check(title: String, singer: String): AlbumTable?

}


@Database(entities = [SongTable::class, AlbumTable::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun SongTableDAO(): SongTableDAO
    abstract fun AlbumTableDAO(): AlbumTableDAO
}