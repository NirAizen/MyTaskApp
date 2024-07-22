package com.coding.meet.tasks.ui

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.coding.meet.tasks.R
import com.coding.meet.tasks.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        arguments?.let {
            val title = it.getString("title", "")
            val description = it.getString("description", "")
            val dateMillis = it.getLong("date", 0L)

            binding.taskTitle.text = title
            binding.taskDescription.text = description
            binding.taskDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(dateMillis))
        }

        binding.addToCalendarBtn.setOnClickListener {
            addToCalendar()
        }

        binding.backButtonC.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_taskListFragment)
        }

        return binding.root
    }

    private fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, binding.taskTitle.text.toString())
            .putExtra(CalendarContract.Events.DESCRIPTION, binding.taskDescription.text.toString())
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
        startActivity(intent)

        requireActivity().onBackPressed()
    }
}
