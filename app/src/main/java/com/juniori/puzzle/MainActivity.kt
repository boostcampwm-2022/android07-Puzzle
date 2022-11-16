package com.juniori.puzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.juniori.puzzle.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationComponent()
    }

    private fun initNavigationComponent() {
        val navController = NavHostFragment.findNavController(
            supportFragmentManager.findFragmentById(R.id.fragmentcontainerview) as NavHostFragment
        )

        binding.bottomnavigationview.setupWithNavController(navController)
        // 추가하기 메뉴를 눌렀을 때 현재 프래그먼트를 유지하면서 다이얼로그를 보여준다.
        findViewById<BottomNavigationItemView>(R.id.bottomsheet_main_addvideo).setOnClickListener {
            navController.navigate(R.id.bottomsheet_main_addvideo)
        }
    }
}
