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
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.FragmentUploadStep2Binding
import com.juniori.puzzle.util.ProgressDialog
import com.juniori.puzzle.util.PuzzleDialog
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UploadStep2Fragment : Fragment() {

    private var _binding: FragmentUploadStep2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: UploadViewModel by activityViewModels()

    private val uploadDialog: PuzzleDialog by lazy {
        createSaveDialog()
    }
    private val publicModeDialog: PuzzleDialog by lazy {
        createPublicModeDialog()
    }
    private val confirmDialog: PuzzleDialog by lazy {
        PuzzleDialog(requireContext()).buildConfirmationDialog({}, {})
    }
    private lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var stateManager: StateManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStep2Binding.inflate(inflater, container, false).apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        stateManager.createLoadingDialog(container)
        progressDialog = ProgressDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOnClickListener()

        binding.containerRadiogroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.radiobuttonSetPublic.id && viewModel.isPublicUpload.not()) {
                viewModel.isPublicUpload = true
                publicModeDialog.showDialog()
            } else if (checkedId == binding.radiobuttonSetPrivate.id && viewModel.isPublicUpload) {
                viewModel.isPublicUpload = false
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.compressFlow.collectLatest { compressPercentile ->
                    if (compressPercentile == 0) {
                        progressDialog.show()
                    } else if (compressPercentile == -1) {
                        return@collectLatest
                    }
                    progressDialog.setProgress(compressPercentile)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadFlow.collectLatest { resource ->
                    if (resource == null) return@collectLatest
                    when (resource) {
                        is Resource.Success -> {
                            stateManager.dismissLoadingDialog()
                            showUploadStateFeedback(getString(R.string.upload_complete))
                            findNavController().popBackStack(R.id.fragment_upload_step1, true)
                        }
                        is Resource.Failure -> {
                            stateManager.dismissLoadingDialog()
                            showUploadStateFeedback(getString(R.string.upload_fail))
                        }
                        is Resource.Loading -> {
                            progressDialog.dismiss()
                            stateManager.showLoadingDialog()
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

    private fun initOnClickListener() {
        binding.buttonSave.setOnClickListener {
            if (checkInfo()) uploadDialog.showDialog()
        }

        binding.buttonGoback.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun createSaveDialog(): PuzzleDialog {
        return PuzzleDialog(requireContext()).buildAlertDialog({
            viewModel.compressVideo(
                requireContext()
            )
        }, {})
            .setTitle(getString(R.string.upload2_savedialog_title))
            .setMessage(
                getString(
                    if (binding.radiobuttonSetPrivate.isChecked) {
                        R.string.upload2_savedialog_supporting_text_private
                    } else {
                        R.string.upload2_savedialog_supporting_text_public
                    }
                )
            )
    }

    private fun createPublicModeDialog(): PuzzleDialog {
        return PuzzleDialog(requireContext()).buildAlertDialog({ },
            { binding.radiobuttonSetPrivate.isChecked = true })
            .setTitle(getString(R.string.upload2_publicuploaddialog_title))
            .setMessage(getString(R.string.upload2_publicuploaddialog_supporting_text))
    }

    private fun setConfirmMessage(message: String): PuzzleDialog {
        return confirmDialog.setMessage(message)
    }

    private fun checkInfo(): Boolean {
        val msg = if (binding.memo.text.isNullOrEmpty()) {
            getString(R.string.upload_memo_empty)
        } else if (binding.memo.text.length > 140) {
            getString(R.string.upload_memo_overflow)
        } else if (binding.golfCourseName.text.isNullOrEmpty()) {
            getString(R.string.upload_location_empty)
        } else if (binding.golfCourseLabel.text.length > 50) {
            getString(R.string.upload_location_overflow)
        } else {
            ""
        }
        if (msg == "") return true
        setConfirmMessage(msg).showDialog()
        return false
    }

    private fun showUploadStateFeedback(feedbackText: String) {
        Snackbar.make(
            binding.root,
            feedbackText,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
