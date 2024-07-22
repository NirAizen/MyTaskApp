package com.coding.meet.tasks.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.coding.meet.tasks.R
import android.content.pm.PackageManager
import android.util.Log
import androidx.navigation.fragment.findNavController

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactUsButton: Button = view.findViewById(R.id.contact_us_button)
        contactUsButton.setOnClickListener {
            sendEmail()
        }

        val backButton: Button = view.findViewById(R.id.back_button_contact)
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_contactUsFragment_to_taskListFragment)
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            Log.d("Email","1")
            data = Uri.parse("mailto:") // This ensures only email apps handle the intent
            putExtra(Intent.EXTRA_EMAIL, arrayOf("someone@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
            putExtra(Intent.EXTRA_TEXT, "Message Body Here")
        }

        try {
            if (emailIntent.resolveActivity(requireActivity().packageManager) == null) {
                startActivity(emailIntent)
            } else {
                throw android.content.ActivityNotFoundException()
            }
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(requireContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }
}