package com.example.notesapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

class NotesAdapter(private var notes: List<Note>, context: Context) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private val db : NotesDatabaseHelper = NotesDatabaseHelper(context)

    class NoteViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView=itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView=itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView=itemView.findViewById(R.id.updateButton)

        val imageView: ImageView = itemView.findViewById(R.id.recyclerImageView) // ImageView for displaying images


        val deleteButton: ImageView=itemView.findViewById(R.id.deleteButton)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noteitem,parent,false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content


        val imageUri = Uri.parse(note.imagePath)
        holder.imageView.setImageURI(imageUri)





        holder.updateButton.setOnClickListener {
            val intent=Intent(holder.itemView.context,UpdateNoteActivity::class.java).apply {
                putExtra("noteId",note.id)

            }
            holder.itemView.context.startActivity(intent)


        }

        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(holder.itemView.context,"Successfully Deleted",Toast.LENGTH_SHORT).show()
        }

//        holder.imageView.setOnClickListener {
//
//            val dialog = Dialog(holder.itemView.context)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//            // Create an ImageView and set its image from the imagePath
//            val dialogImageView = ImageView(holder.itemView.context)
//            dialogImageView.setImageURI(imageUri)
//
//            dialog.setContentView(dialogImageView)
//            dialog.show()
//
//        }


    }
    fun refreshData(newNotes: List<Note>){
        notes=newNotes
        notifyDataSetChanged()
    }
}