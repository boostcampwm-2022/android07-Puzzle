package com.juniori.puzzle.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.MainActivity
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.ActivityUpdateNicknameBinding
import com.juniori.puzzle.domain.usecase.UpdateNicknameUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateNicknameActivity : AppCompatActivity() {
    private val binding: ActivityUpdateNicknameBinding by lazy { ActivityUpdateNicknameBinding.inflate(layoutInflater) }
    @Inject lateinit var updateNicknameUseCase: UpdateNicknameUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.completeButton.setOnClickListener {
            val newNickname = binding.nicknameContainer.text.toString()

            if (newNickname.isBlank()) {
                Toast.makeText(this, "닉네임을 다시 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                lifecycleScope.launch(Dispatchers.Main.immediate) {
                    val result = updateNicknameUseCase(newNickname)

                    if (result is Resource.Success) {
                        val intent = Intent(this@UpdateNicknameActivity, MainActivity::class.java).apply {
                            putExtra(MyPageFragment.NEW_NICKNAME, newNickname)
                        }

                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this@UpdateNicknameActivity, "닉네임을 변경할 수 없습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}