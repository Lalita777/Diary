package com.example.project

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.edit_delete_layout.view.*
import kotlinx.android.synthetic.main.insert_layout.view.*
import kotlinx.android.synthetic.main.insert_layout.view.edt_age
import kotlinx.android.synthetic.main.insert_layout.view.edt_id
import kotlinx.android.synthetic.main.insert_layout.view.edt_name

class MainActivity : AppCompatActivity() {
    var dbHandler: DatabaseHelper? = null
    var student = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = DatabaseHelper(this)
        dbHandler?.getWritableDatabase()
        callStudentData()
        recycler_view.adapter = UsersAdapter(student, applicationContext)
        recycler_view.layoutManager =
            LinearLayoutManager(applicationContext) as RecyclerView.LayoutManager?
        recycler_view.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        recycler_view.addItemDecoration(
            DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        )

        recycler_view.addOnItemTouchListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                editDeleteDialog(position)
            }
        })
    }
    fun callStudentData() {
        student.clear();
        student.addAll(dbHandler!!.getAllStudents())
        recycler_view.adapter?.notifyDataSetChanged()
    }

    fun addStudent(v: View) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.insert_layout, null)
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.btnAdd.setOnClickListener {
            var id = mDialogView.edt_id.text.toString()
            var name = mDialogView.edt_name.text.toString()
            var age = mDialogView.edt_age.text.toString()
            var result = dbHandler?.insertStudent(User(id = id, name = name, age = age))

            if (result!! > -1) {
                Toast.makeText(
                    applicationContext,
                    "The Student is added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                callStudentData()
                mAlertDialog.dismiss()
            } else {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
            }
        }
        mDialogView.btnReset.setOnClickListener {
            mDialogView.edt_id.setText("")
            mDialogView.edt_name.setText("")
            mDialogView.edt_age.setText("")
        }


    }

    fun editDeleteDialog(position: Int) {
        val std = student[position]
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_delete_layout, null)

        mDialogView.edt_id.setText(std.id)
        mDialogView.edt_id.isEnabled = false
        mDialogView.edt_name.setText(std.name)
        mDialogView.edt_age.setText(std.age)

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.btnUpdate.setOnClickListener {
            var id = mDialogView.edt_id.text.toString()
            var name = mDialogView.edt_name.text.toString()
            var age = mDialogView.edt_age.text.toString()
            var result = dbHandler?.updateStudent(User(id = id, name = name, age = age))
            if (result!! > -1) {
                Toast.makeText(
                    applicationContext,
                    "The student is update sucessfully",
                    Toast.LENGTH_SHORT
                ).show()
                callStudentData()

            } else {
                Toast.makeText(applicationContext, "Eror", Toast.LENGTH_LONG).show()
            }
            mAlertDialog.dismiss()
        }
        mDialogView.btnDelete.setOnClickListener() {
            val builder = AlertDialog.Builder(this)
            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                val result = dbHandler?.deleteStudent(std.id)
                if (result!! > -1) {
                    Toast.makeText(applicationContext, "Successful", Toast.LENGTH_LONG).show()
                    callStudentData()
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                }
                mAlertDialog.dismiss()
            }
            val negativeButtonClick = { dialog: DialogInterface, which :Int->
                mAlertDialog.dismiss()
            }
            builder.setTitle("Warning")
            builder.setMessage("Do you want to delete the diary?")
            builder.setPositiveButton("No", negativeButtonClick)
            builder.setNegativeButton("Yes" , positiveButtonClick )
            builder.show()
        }


    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int, view: View) }
fun RecyclerView.addOnItemTouchListener(onClickListener: OnItemClickListener) {
    this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
            view?.setOnClickListener(null) }

        override fun onChildViewAttachedToWindow(view: View) {
            view?.setOnClickListener {
                val holder = getChildViewHolder(view)
                onClickListener.onItemClicked(holder.adapterPosition, view)
            }
        }
    })
}