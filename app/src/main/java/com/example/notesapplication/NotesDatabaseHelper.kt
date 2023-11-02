package com.example.notesapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.ByteArrayOutputStream


class NotesDatabaseHelper(context: Context):SQLiteOpenHelper(context, DataBaseName,null,
    DataBaseVersion) {

    companion object{
        private const val DataBaseName="notesapp.db"
        private const val DataBaseVersion=1
        private const val TableName="allnotes"
        private const val ColumnId="id"
        private const val ColumnTitle="title"
        private const val ColumnContent="content"
        private const val ColumnImage = "image"


    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQuery  = "CREATE TABLE $TableName( $ColumnId INTEGER PRIMARY KEY,  $ColumnTitle TEXT, $ColumnContent TEXT, $ColumnImage TEXT)"
        db?.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val dropTableQuery = "DROP TABLE IF EXISTS $TableName"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertNote(note: Note){

        val db= writableDatabase
        val values=ContentValues().apply {
            put(ColumnTitle,note.title)
            put(ColumnContent,note.content)
            put(ColumnImage, note.imagePath)

        }
        db.insert(TableName,null,values)
        db.close()

    }


    fun getAllNotes() : List<Note>{
        val notesList= mutableListOf<Note>()
        val db= readableDatabase
        val query = "SELECT * FROM $TableName"
        val cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val id= cursor.getInt(cursor.getColumnIndexOrThrow(ColumnId))
            val title= cursor.getString(cursor.getColumnIndexOrThrow(ColumnTitle))
            val content= cursor.getString(cursor.getColumnIndexOrThrow(ColumnContent))

            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ColumnImage))

            val note = Note(id,title, content,imagePath)
            notesList.add(note)

        }
        cursor.close()
        db.close()
        return notesList
    }


    fun updateNote(note: Note){
        val db= writableDatabase
        val values= ContentValues().apply {
            put(ColumnTitle,note.title)
            put(ColumnContent,note.content)
            put(ColumnImage,note.imagePath)
        }
        val whereClause= "$ColumnId = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TableName,values, whereClause, whereArgs)
        db.close()
    }
    fun getNoteById(noteId: Int): Note{
        val db = readableDatabase


        val query = "SELECT * FROM $TableName WHERE $ColumnId = $noteId"
        val cursor=db.rawQuery(query, null)
        cursor.moveToFirst()

        val id= cursor.getInt(cursor.getColumnIndexOrThrow(ColumnId))
        val title= cursor.getString(cursor.getColumnIndexOrThrow(ColumnTitle))
        val content= cursor.getString(cursor.getColumnIndexOrThrow(ColumnContent))

        val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ColumnImage))

        cursor.close()
        db.close()
        return Note(id,title,content, imagePath)


    }
    fun deleteNote(noteId: Int){
        val db=writableDatabase
        val whereClause = "$ColumnId = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TableName,whereClause,whereArgs)
        db.close()
    }


}