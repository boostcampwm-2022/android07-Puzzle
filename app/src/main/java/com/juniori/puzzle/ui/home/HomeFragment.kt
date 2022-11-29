package com.juniori.puzzle.ui.home

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.location.LocationListenerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juniori.puzzle.R
import com.juniori.puzzle.adapter.WeatherRecyclerViewAdapter
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.FragmentHomeBinding
import com.juniori.puzzle.ui.sensor.SensorActivity
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val random = Random(System.currentTimeMillis())
    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var stateManager: StateManager

    private lateinit var adapter: WeatherRecyclerViewAdapter

    private val locationListener = object : LocationListenerCompat {
        override fun onLocationChanged(loc: Location) {
            homeViewModel.getWeather()
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            homeViewModel.setWeatherInfoText(getString(R.string.location_service_off))
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            homeViewModel.getWeather()
        }
    }

    val checkPermission = {
        locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermitted ->
        homeViewModel.setUiState(Resource.Loading)
        if (isPermitted) {
            homeViewModel.getWeather()
        } else {
            homeViewModel.setWeatherInfoText(getString(R.string.location_permission))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = homeViewModel
        }
        stateManager.createLoadingDialog(container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val welcomeTextArray = resources.getStringArray(R.array.welcome_text)
        checkPermission()
        adapter = WeatherRecyclerViewAdapter()

        binding.run {
            weatherRefreshBtn.setOnClickListener {
                checkPermission()
            }

            golfBtnBackground.setOnClickListener {
                val intent = Intent(requireActivity(), SensorActivity::class.java)
                startActivity(intent)
            }

            weatherDetailRecyclerView.adapter = adapter
        }

        homeViewModel.run {
            setWelcomeText(welcomeTextArray.random(random))
            setDisplayName()

            uiState.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        stateManager.dismissLoadingDialog()
                        stateManager.removeNetworkDialog()
                        binding.weatherLayout.isVisible = true
                    }
                    is Resource.Failure -> {
                        stateManager.dismissLoadingDialog()
                        stateManager.removeNetworkDialog()
                        binding.weatherLayout.isVisible = false
                        stateManager.showNetworkDialog(
                            binding.homeBottomCardView,
                            resource.exception.message ?: "네트워크 통신에 실패했습니다",
                            checkPermission
                        )
                    }
                    is Resource.Loading -> {
                        stateManager.showLoadingDialog()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.registerListener(locationListener)
    }

    override fun onPause() {
        super.onPause()
        stateManager.dismissLoadingDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.unregisterListener()
        _binding = null
    }

}
