package com.example.androidapp.Data.ProductData;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM product_table")
    LiveData<List<Product>> getAllProduct();

    @Query("DELETE FROM product_table")
    void deleteAllProduct();

    @Query("UPDATE product_table SET imageDir=:imageDirs WHERE name =:productName")
    void updateImageProduct(String productName, String imageDirs);

    @Query("SELECT imageDir FROM product_table WHERE name =:name_s")
    String getProductImg(String name_s);

}