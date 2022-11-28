package com.juniori.puzzle.ui.mypage

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juniori.puzzle.R
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

        viewModel.makeLogoutDialogEvent.observe(viewLifecycleOwner) {
            navigateToIntroPageEvent()
        }

        viewModel.makeWithdrawDialogEvent.observe(viewLifecycleOwner) {
            finishApplication()
        }

        viewModel.navigateToUpdateNicknamePageEvent.observe(viewLifecycleOwner) {
            val intent = Intent(context, UpdateNicknameActivity::class.java)
            updateActivityLauncher.launch(intent)
        }
    }

    private fun navigateToIntroPageEvent() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_remind))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val result = viewModel.requestLogout()
                if (result is Resource.Success<Unit>) {
                    val intent = Intent(context, LoginActivity::class.java)
                    activity?.finishAffinity()
                    startActivity(intent)
                }
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
            }
            .show()
    }

    private fun finishApplication() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.withdraw))
            .setMessage(getString(R.string.withdraw_remind))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val result = viewModel.requestWithdraw()
                if (result is Resource.Success<Unit>) {
                    activity?.finishAffinity()
                }
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val NEW_NICKNAME = "new_nickname"
    }
}
