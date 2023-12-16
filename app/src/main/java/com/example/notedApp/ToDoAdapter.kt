package com.example.notedApp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notedApp.databinding.ToDoItemBinding

class ToDoAdapter(private val list:MutableList<ToDoInfo>):
RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>(){

    private var listener:ToDoAdapterClicksInterface? = null
    fun setListener(listener:ToDoAdapterClicksInterface){
        this.listener = listener
    }

    inner class ToDoViewHolder(val binding:ToDoItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = ToDoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder){
            with(list[position]){

                binding.toDoTask.text = this.task
                binding.deleteTask.setOnClickListener {

                    listener?.onDeleteTaskBtnClicked(this)

                }

            }
        }
    }

    interface ToDoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(toDoInfo: ToDoInfo)
    }
}