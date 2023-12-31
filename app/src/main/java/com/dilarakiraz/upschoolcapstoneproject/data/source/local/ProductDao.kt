package com.dilarakiraz.upschoolcapstoneproject.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getProducts(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(product: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun clearFavorites()

    @Delete
    suspend fun deleteFromFavorites(product: ProductEntity)

    @Query("SELECT id FROM products")
    suspend fun getProductIds(): List<Int>
}