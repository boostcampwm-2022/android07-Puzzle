package com.juniori.puzzle.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        _binding!!.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToIntroPageEvent.observe(viewLifecycleOwner) { result ->
            navigateToIntroPageEvent(result)
        }
    }

    private fun navigateToIntroPageEvent(result: Resource<Unit>) {
        if (result is Resource.Success) {
            val intent = Intent(context, LoginActivity::class.java)
            activity?.finishAffinity()
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
