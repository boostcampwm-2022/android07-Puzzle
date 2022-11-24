package com.juniori.puzzle.ui.mypage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.FragmentMypageBinding
import com.juniori.puzzle.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var updateActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        _binding!!.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.userNickname.value = result.data?.getStringExtra(NEW_NICKNAME) ?: ""
            }
        }

        viewModel.navigateToIntroPageEvent.observe(viewLifecycleOwner) { result ->
            navigateToIntroPageEvent(result)
        }

        viewModel.finishApplicationEvent.observe(viewLifecycleOwner) { result ->
            finishApplication(result)
        }

        viewModel.navigateToUpdateNicknamePageEvent.observe(viewLifecycleOwner) {
            val intent = Intent(context, UpdateNicknameActivity::class.java)
            updateActivityLauncher.launch(intent)
        }
    }

    private fun navigateToIntroPageEvent(result: Resource<Unit>) {
        if (result is Resource.Success) {
            val intent = Intent(context, LoginActivity::class.java)
            activity?.finishAffinity()
            startActivity(intent)
        }
    }

    private fun finishApplication(result: Resource<Unit>) {
        if (result is Resource.Success) {
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val NEW_NICKNAME = "new_nickname"
    }
}
