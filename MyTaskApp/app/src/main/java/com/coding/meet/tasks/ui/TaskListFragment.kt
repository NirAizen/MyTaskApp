package com.coding.meet.tasks.ui
import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.coding.meet.tasks.R
import com.coding.meet.tasks.adapters.TaskRVVBListAdapter
import com.coding.meet.tasks.databinding.FragmentTaskListBinding
import com.coding.meet.tasks.models.Task
import com.coding.meet.tasks.utils.Status
import com.coding.meet.tasks.utils.StatusResult
import com.coding.meet.tasks.utils.clearEditText
import com.coding.meet.tasks.utils.hideKeyBoard
import com.coding.meet.tasks.utils.longToastShow
import com.coding.meet.tasks.utils.setupDialog
import com.coding.meet.tasks.utils.validateEditText
import com.coding.meet.tasks.viewmodels.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import com.coding.meet.tasks.utils.hideKeyBoard
import com.coding.meet.tasks.utils.longToastShow
import com.coding.meet.tasks.ui.AboutFragment
import com.coding.meet.tasks.ui.ContactUsFragment

import longToastShow


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    val addTaskDialog: Dialog by lazy {
        Dialog(requireContext(), R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.add_task_dialog)
        }
    }

    private val updateTaskDialog: Dialog by lazy {
        Dialog(requireContext(), R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.update_task_dialog)
        }
    }

    private val loadingDialog: Dialog by lazy {
        Dialog(requireContext(), R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.loading_dialog)
        }
    }

    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(requireActivity())[TaskViewModel::class.java]
    }

    private val isListMutableLiveData = MutableLiveData<Boolean>().apply {
        postValue(true)
    }

    private var selectedImageUri: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        Log.d("test", "1")
        return binding.root
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("imageKey", viewLifecycleOwner) { _, bundle ->
            val imageUri = bundle.getString("imageUri")

            selectedImageUri = imageUri
            addTaskDialog.show()
        }

        val contactUsButton = view.findViewById<Button>(R.id.contact_us_button)
        contactUsButton.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_contactUsFragment)

        }

        val aboutButton = view.findViewById<Button>(R.id.about_button)
        aboutButton.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_aboutFragment)
        }

        val addCloseImg = addTaskDialog.findViewById<ImageView>(R.id.closeImg)
        addCloseImg.setOnClickListener { addTaskDialog.dismiss() }

        val addETTitle = addTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
        val addETTitleL = addTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)

        addETTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETTitle, addETTitleL)
            }

        })
        val addImageButton = addTaskDialog.findViewById<Button>(R.id.selectImageButton)
        addImageButton.setOnClickListener {
            addTaskDialog.dismiss()

            findNavController().navigate(R.id.action_taskListFragment_to_imageFragment)
        }


        val addETDesc = addTaskDialog.findViewById<TextInputEditText>(R.id.edTaskDesc)
        val addETDescL = addTaskDialog.findViewById<TextInputLayout>(R.id.edTaskDescL)

        addETDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETDesc, addETDescL)
            }
        })

        binding.addTaskFABtn.setOnClickListener {
            clearEditText(addETTitle, addETTitleL)
            clearEditText(addETDesc, addETDescL)
            selectedImageUri= null
            addTaskDialog.show()
        }

            val saveTaskBtn = addTaskDialog.findViewById<Button>(R.id.saveTaskBtn)
            saveTaskBtn.setOnClickListener {
                if (validateEditText(addETTitle, addETTitleL)
                    && validateEditText(addETDesc, addETDescL)
                ) {



                        Log.d("test", "2")
                    val newTask = Task(
                        UUID.randomUUID().toString(),
                        addETTitle.text.toString().trim(),
                        addETDesc.text.toString().trim(),
                        Date(),
                        selectedImageUri,
                    )
                    val addToCalendarDialog = MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.add_task_to_calendar))
                        .setMessage(getString(R.string.do_you_want_to_add_this_task_to_your_calendar))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            // Handle adding task to calendar
                            val args = Bundle().apply {
                                putString("title", newTask.title)
                                putString("description", newTask.description)
                                putLong("date", newTask.date.time)
                            }

                            findNavController().navigate(
                                R.id.action_taskListFragment_to_calendarFragment, // Use the action ID for navigation
                                args
                            )
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                            hideKeyboard(saveTaskBtn)
                            addTaskDialog.dismiss()
                            taskViewModel.insertTask(newTask)
                        }
                        .create()

                    addToCalendarDialog.show()
                    activity?.hideKeyBoard(it)
                    addTaskDialog.dismiss()
                    taskViewModel.insertTask(newTask)
                }
            }

            val updateETTitle = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
            val updateETTitleL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)

            updateETTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {
                    validateEditText(updateETTitle, updateETTitleL)
                }

            })

            val updateETDesc = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskDesc)
            val updateETDescL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskDescL)

            updateETDesc.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {
                    validateEditText(updateETDesc, updateETDescL)
                }
            })

            val updateCloseImg = updateTaskDialog.findViewById<ImageView>(R.id.closeImg)
            updateCloseImg.setOnClickListener { updateTaskDialog.dismiss() }

            val updateTaskBtn = updateTaskDialog.findViewById<Button>(R.id.updateTaskBtn)


        isListMutableLiveData.observe(viewLifecycleOwner) { isListView ->
            binding.taskRV.layoutManager = if (isListView) {
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            } else {
                StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            }
            binding.listOrGridImg.setImageResource(if (isListView) R.drawable.ic_view_module else R.drawable.ic_view_list)
        }


            binding.listOrGridImg.setOnClickListener {
                isListMutableLiveData.postValue(!isListMutableLiveData.value!!)
            }

        val taskRVVBListAdapter = TaskRVVBListAdapter(
            isListMutableLiveData,
            { type, position, task ->
                when (type) {
                    "delete" -> {
                        taskViewModel.deleteTaskUsingId(task.id)
                        restoreDeletedTask(task)
                    }
                    "update" -> {
                        updateETTitle.setText(task.title)
                        updateETDesc.setText(task.description)
                        updateTaskBtn.setOnClickListener {
                            if (validateEditText(updateETTitle, updateETTitleL) && validateEditText(updateETDesc, updateETDescL)) {
                                val updateTask = Task(
                                    task.id,
                                    updateETTitle.text.toString().trim(),
                                    updateETDesc.text.toString().trim(),
                                    task.date, // Preserve the original date if it was not intended to be changed
                                    task.imageUri // Preserve the existing image URI
                                )
                                activity?.hideKeyBoard(it)
                                updateTaskDialog.dismiss()
                                taskViewModel.updateTask(updateTask)
                            }
                        }
                        updateTaskDialog.show()
                    }
                }
            },

            { type, position, task ->
                if (type == "click") {
                    val args = Bundle().apply {
                        putString("title", task.title)
                        putString("description", task.description)
                        putString("imageUri", task.imageUri) // Add the image URI to the bundle
                    }
                    Log.d("Tasklist", "Task: ${task.toString()}")
                    Log.d("argslist", "Arguments: ${args.toString()}")

                    findNavController().navigate(
                        R.id.action_taskListFragment_to_taskDetailFragment, // Use the action ID for navigation
                        args
                    )
                }
            }
        )
            binding.taskRV.adapter = taskRVVBListAdapter
            ViewCompat.setNestedScrollingEnabled(binding.taskRV, true)
            taskRVVBListAdapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
//                mainBinding.taskRV.smoothScrollToPosition(positionStart)
                    binding.nestedScrollView.smoothScrollTo(0, positionStart)
                }
            })
            callGetTaskList(taskRVVBListAdapter)
            callSortByLiveData()
            statusCallback()


            callSearch()
        }


    private fun restoreDeletedTask(deletedTask: Task) {
            val snackBar = Snackbar.make(
                binding.root, "Deleted '${deletedTask.title}'",
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction("Undo") {
                taskViewModel.insertTask(deletedTask)
            }
            snackBar.show()
        }

        private fun callSearch() {
            binding.edSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(query: Editable) {
                    if (query.toString().isNotEmpty()) {

                        taskViewModel.searchTaskList(query.toString())
                    } else {
                        callSortByLiveData()
                    }
                }
            })

            binding.edSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    activity?.hideKeyBoard(v)
                    return@setOnEditorActionListener true
                }
                false
            }

            callSortByDialog()
        }

        private fun callSortByLiveData() {
            taskViewModel.sortByLiveData.observe(viewLifecycleOwner) {
                taskViewModel.getTaskList(it.second, it.first)
            }
        }

        private fun callSortByDialog() {
            var checkedItem = 0   // 2 is default item set
            val items =
                arrayOf(getString(R.string.title_ascending),
                    getString(R.string.title_descending),
                    getString(R.string.date_ascending), getString(R.string.date_descending))

            binding.sortImg.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.sort_by))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        when (checkedItem) {
                            0 -> {
                                taskViewModel.setSortBy(Pair("title", true))
                            }

                            1 -> {
                                taskViewModel.setSortBy(Pair("title", false))
                            }

                            2 -> {
                                taskViewModel.setSortBy(Pair("date", true))
                            }

                            else -> {
                                taskViewModel.setSortBy(Pair("date", false))
                            }
                        }
                    }
                    .setSingleChoiceItems(items, checkedItem) { _, selectedItemIndex ->
                        checkedItem = selectedItemIndex
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        private fun statusCallback() {
            taskViewModel
                .statusLiveData
                .observe(viewLifecycleOwner) { statusResult ->
                    when (statusResult.status) {
                        Status.LOADING -> {
                            loadingDialog.show()
                        }

                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            when (statusResult.data) {
                                StatusResult.Added -> {
                                    Log.d("StatusResult", "Added")
                                }

                                StatusResult.Deleted -> {
                                    Log.d("StatusResult", "Deleted")
                                }

                                StatusResult.Updated -> {
                                    Log.d("StatusResult", "Updated")
                                }

                                null -> TODO()
                            }
                            statusResult.message?.let { message ->
                                context?.let { longToastShow(it, message) }
                            }
                        }

                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            statusResult.message?.let { message ->
                                context?.let { longToastShow(it, message) }
                            }
                        }
                    }
                }
        }


        private fun callGetTaskList(taskRecyclerViewAdapter: TaskRVVBListAdapter) {
            CoroutineScope(Dispatchers.Main).launch {
                taskViewModel
                    .taskStateFlow
                    .collectLatest { state ->
                        Log.d("status", state.status.toString())

                        when (state.status) {
                            Status.LOADING -> {
                                loadingDialog.show()
                            }

                            Status.SUCCESS -> {
                                loadingDialog.dismiss()
                                state.data?.collect { taskList ->
                                    taskRecyclerViewAdapter.submitList(taskList)
                                }
                            }

                            Status.ERROR -> {
                                loadingDialog.dismiss()
                                state.message?.let { message ->
                                    context?.let { longToastShow(it, message) }
                                }
                            }
                        }
                    }
            }
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

