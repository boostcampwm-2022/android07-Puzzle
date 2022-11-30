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
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.FragmentMypageBinding
import com.juniori.puzzle.ui.login.LoginActivity
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var updateActivityLauncher: ActivityResultLauncher<Intent>
    @Inject lateinit var stateManager: StateManager
    private val warningDialog: AlertDialog.Builder by lazy { AlertDialog.Builder(context) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        _binding!!.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        stateManager.createLoadingDialog(container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateUserNickname(result.data?.getStringExtra(NEW_NICKNAME) ?: "")
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.requestLogoutFlow.collect { result ->
                when(result) {
                    is Resource.Success<Unit> -> {
                        stateManager.dismissLoadingDialog()
                        val intent = Intent(context, LoginActivity::class.java)
                        activity?.finishAffinity()
                        startActivity(intent)
                    }
                    is Resource.Loading -> {
                        stateManager.showLoadingDialog()
                    }
                    is Resource.Failure -> {
                        stateManager.dismissLoadingDialog()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.requestWithdrawFlow.collect { result ->
                when(result) {
                    is Resource.Success<Unit> -> {
                        stateManager.dismissLoadingDialog()
                        activity?.finishAffinity()
                    }
                    is Resource.Loading -> {
                        stateManager.showLoadingDialog()
                    }
                    is Resource.Failure -> {
                        stateManager.dismissLoadingDialog()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.makeLogoutDialogFlow.collect {
                makeLogoutDialog()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.makeWithdrawDialogFlow.collect {
                makeWithdrawDialog()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.navigateToUpdateNicknameFlow.collect {
                val intent = Intent(context, UpdateNicknameActivity::class.java)
                updateActivityLauncher.launch(intent)
            }
        }
    }

    private fun makeLogoutDialog() {
        warningDialog
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_remind))
            .setPositiveButton(getString(R.string.all_yes)) { _, _ ->
                viewModel.requestLogout()
            }
            .setNegativeButton(getString(R.string.all_no)) { _, _ ->
            }
            .show()
    }

    private fun makeWithdrawDialog() {
        warningDialog
            .setTitle(getString(R.string.withdraw))
            .setMessage(getString(R.string.withdraw_remind))
            .setPositiveButton(getString(R.string.all_yes)) { _, _ ->
                viewModel.requestWithdraw()
            }
            .setNegativeButton(getString(R.string.all_no)) { _, _ ->
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val NEW_NICKNAME = "NEW_NICKNAME"
    }
}
