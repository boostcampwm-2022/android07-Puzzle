package com.juniori.puzzle.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.MainActivity
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.ActivityUpdateNicknameBinding
import com.juniori.puzzle.domain.usecase.UpdateNicknameUseCase
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UpdateNicknameActivity : AppCompatActivity() {
    private val binding: ActivityUpdateNicknameBinding by lazy { ActivityUpdateNicknameBinding.inflate(layoutInflater) }
    @Inject lateinit var stateManager: StateManager
    @Inject lateinit var updateNicknameUseCase: UpdateNicknameUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        stateManager.createLoadingDialog(binding.viewContainer)

        binding.completeButton.setOnClickListener {
            val newNickname = binding.nicknameContainer.text.toString()

            if (newNickname.isBlank()) {
                Toast.makeText(this, getString(R.string.input_nickname_again), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.Main.immediate) {
                stateManager.showLoadingDialog()
                val result = withContext(Dispatchers.IO) {
                    updateNicknameUseCase(newNickname)
                }
                stateManager.dismissLoadingDialog()

                if (result is Resource.Success) {
                    val intent = Intent(this@UpdateNicknameActivity, MainActivity::class.java).apply {
                        putExtra(MyPageFragment.NEW_NICKNAME, newNickname)
                    }

                    setResult(RESULT_OK, intent)
                    finish()
                }
                else {
                    Toast.makeText(this@UpdateNicknameActivity, getString(R.string.nickname_change_impossible), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}