package com.juniori.puzzle.ui.addvideo.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.FragmentUploadStep2Binding
import com.juniori.puzzle.ui.addvideo.AddVideoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UploadStep2Fragment : Fragment() {

    private var _binding: FragmentUploadStep2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: AddVideoViewModel by activityViewModels()

    private val uploadDialog: AlertDialog by lazy {
        createSaveDialog()
    }
    private val publicModeDialog: AlertDialog by lazy {
        createPublicModeDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStep2Binding.inflate(inflater, container, false).apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOnClickListener()

        binding.containerRadiogroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.radiobuttonSetPublic.id && viewModel.isPublicUpload.not()) {
                viewModel.isPublicUpload = true
                publicModeDialog.show()
            } else if (checkedId == binding.radiobuttonSetPrivate.id && viewModel.isPublicUpload) {
                viewModel.isPublicUpload = false
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadFlow.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            findNavController().popBackStack(R.id.fragment_upload_step1, true)
                        }
                        is Resource.Failure -> {
                            /** upload video가 실패했을때의 ui 처리 */
                        }
                        is Resource.Loading -> {
                            /** video upload 중일때의 ui 처리 */
                        }
                    }
                }
            }
        }

        binding.datespicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            binding.datesButton.text = getString(R.string.upload2_dates, year, month + 1, dayOfMonth)
        }

        binding.timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initOnClickListener() {
        binding.buttonSave.setOnClickListener {
            uploadDialog.show()
        }

        binding.buttonGoback.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.datesButton.setOnClickListener {
            binding.datespicker.visibility = View.VISIBLE
            binding.timepicker.visibility = View.GONE
        }

        binding.timeButton.setOnClickListener {
            binding.datespicker.visibility = View.GONE
            binding.timepicker.visibility = View.VISIBLE
        }
    }

    private fun createSaveDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Puzzle_Dialog)
            .setTitle(R.string.upload2_savedialog_title)
            .setMessage(
                if (binding.radiobuttonSetPrivate.isChecked) {
                    R.string.upload2_savedialog_supporting_text_private
                } else {
                    R.string.upload2_savedialog_supporting_text_public
                }
            )
            .setPositiveButton(R.string.all_yes) { _, _ ->
                viewModel.uploadVideo()
            }
            .setNegativeButton(R.string.all_no) { _, _ ->
                uploadDialog.dismiss()
            }
            .create()
    }

    private fun createPublicModeDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Puzzle_Dialog)
            .setTitle(R.string.upload2_publicuploaddialog_title)
            .setMessage(R.string.upload2_publicuploaddialog_supporting_text)
            .setPositiveButton(R.string.all_yes) { _, _ ->
                publicModeDialog.dismiss()
            }
            .setNegativeButton(R.string.all_no) { _, _ ->
                binding.radiobuttonSetPrivate.isChecked = true
            }
            .create()
    }
}
