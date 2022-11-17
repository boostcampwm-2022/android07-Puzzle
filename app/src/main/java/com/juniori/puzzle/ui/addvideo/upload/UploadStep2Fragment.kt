package com.juniori.puzzle.ui.addvideo.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.juniori.puzzle.databinding.FragmentUploadStep2Binding
import com.juniori.puzzle.ui.addvideo.AddVideoViewModel

class UploadStep2Fragment : Fragment() {

    private var _binding: FragmentUploadStep2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: AddVideoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
        }
        binding.buttonGoback.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.containerRadiogroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.radiobuttonSetPublic.id) {
            } else if (checkedId == binding.radiobuttonSetPrivate.id) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
