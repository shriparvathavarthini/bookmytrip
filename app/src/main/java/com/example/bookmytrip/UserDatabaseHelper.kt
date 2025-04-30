package com.example.bookmytrip

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "UserDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE User(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT," +
                    "password TEXT," +
                    "location TEXT," +
                    "profileImagePath TEXT" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS User")
        onCreate(db)
    }

    fun insertUser(username: String, password: String, location: String, profileImagePath: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("location", location)
            put("profileImagePath", profileImagePath)
        }
        return db.insert("User", null, values)
    }

    fun getLastUser(): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM User ORDER BY id DESC LIMIT 1", null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("location")),
                cursor.getString(cursor.getColumnIndexOrThrow("profileImagePath"))
            )
        }
        cursor.close()
        return user
    }

    fun deleteAllUsers() {
        writableDatabase.execSQL("DELETE FROM User")
    }
}
