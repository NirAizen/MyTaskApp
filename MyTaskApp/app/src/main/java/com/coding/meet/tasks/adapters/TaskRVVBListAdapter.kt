package com.coding.meet.tasks.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coding.meet.tasks.databinding.ViewTaskGridLayoutBinding
import com.coding.meet.tasks.databinding.ViewTaskListLayoutBinding
import com.coding.meet.tasks.models.Task
import java.text.SimpleDateFormat
import java.util.Locale
import com.coding.meet.tasks.R

class TaskRVVBListAdapter(
    private val isList: MutableLiveData<Boolean>,
    private val deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
    private val onClick: (String, Int, Task) -> Unit
) : ListAdapter<Task, RecyclerView.ViewHolder>(DiffCallback()) {

    class ListTaskViewHolder(private val viewTaskListLayoutBinding: ViewTaskListLayoutBinding) :
        RecyclerView.ViewHolder(viewTaskListLayoutBinding.root) {

        fun bind(
            task: Task,
            deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
            onClick: (type: String, position: Int, task: Task) -> Unit
        ) {
            viewTaskListLayoutBinding.titleTxt.text = task.title
            viewTaskListLayoutBinding.descrTxt.text = task.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())
            viewTaskListLayoutBinding.dateTxt.text = dateFormat.format(task.date)

            if (task.imageUri != null) {
                Glide.with(itemView.context)
                    .load(task.imageUri)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(viewTaskListLayoutBinding.taskImage)
            } else {
                viewTaskListLayoutBinding.taskImage.setImageResource(R.drawable.ic_placeholder_image)
            }

            viewTaskListLayoutBinding.deleteImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("delete", adapterPosition, task)
                }
            }
            viewTaskListLayoutBinding.editImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("update", adapterPosition, task)
                }
            }

            viewTaskListLayoutBinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    onClick("click", adapterPosition, task)
                }
            }
        }
    }

    class GridTaskViewHolder(private val viewTaskGridLayoutBinding: ViewTaskGridLayoutBinding) :
        RecyclerView.ViewHolder(viewTaskGridLayoutBinding.root) {

        fun bind(
            task: Task,
            deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
            onClick: (type: String, position: Int, task: Task) -> Unit
        ) {
            viewTaskGridLayoutBinding.titleTxt.text = task.title
            viewTaskGridLayoutBinding.descrTxt.text = task.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())
            viewTaskGridLayoutBinding.dateTxt.text = dateFormat.format(task.date)

            if (task.imageUri != null) {
                Glide.with(itemView.context)
                    .load(task.imageUri)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .into(viewTaskGridLayoutBinding.taskImage)
            } else {
                viewTaskGridLayoutBinding.taskImage.setImageResource(R.drawable.ic_placeholder_image)
            }

            viewTaskGridLayoutBinding.deleteImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("delete", adapterPosition, task)
                }
            }
            viewTaskGridLayoutBinding.editImg.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("update", adapterPosition, task)
                }
            }

            viewTaskGridLayoutBinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    onClick("click", adapterPosition, task)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == 1) {  // Grid_Item
            GridTaskViewHolder(
                ViewTaskGridLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {  // List_Item
            ListTaskViewHolder(
                ViewTaskListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = getItem(position)

        if (isList.value == true) {
            (holder as ListTaskViewHolder).bind(task, deleteUpdateCallback, onClick)
        } else {
            (holder as GridTaskViewHolder).bind(task, deleteUpdateCallback, onClick)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isList.value == true) {
            0
        } else {
            1
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
