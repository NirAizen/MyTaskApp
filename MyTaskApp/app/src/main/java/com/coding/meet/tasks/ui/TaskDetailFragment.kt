package com.coding.meet.tasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.coding.meet.tasks.R

class TaskDetailFragment : Fragment() {

    private var title: String? = null
    private var description: String? = null
    private var imageUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the task details from arguments
        arguments?.let {
            title = it.getString("title")
            description = it.getString("description")
            imageUri = it.getString("imageUri") // Get the image URI from arguments
        }

        // Set up views
        val titleTextView: TextView = view.findViewById(R.id.task_title)
        val descriptionTextView: TextView = view.findViewById(R.id.task_description)
        val imageView: ImageView = view.findViewById(R.id.task_image)

        val backButton: Button = view.findViewById(R.id.back_button_details)
        backButton.setOnClickListener {
            findNavController().popBackStack() // Pop the current fragment off the stack
        }

        titleTextView.text = title ?: "No Title"
        descriptionTextView.text = description ?: "No Description"

        // Use Glide to load the image
        imageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(imageView)
        } ?: null // Set a default image if none provided
    }

    companion object {
        fun newInstance(title: String, description: String, imageUri: String?): TaskDetailFragment {
            val fragment = TaskDetailFragment()
            val args = Bundle().apply {
                putString("title", title)
                putString("description", description)
                putString("imageUri", imageUri)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
