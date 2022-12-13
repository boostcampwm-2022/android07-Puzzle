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
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.R
import com.juniori.puzzle.adapter.WeatherRecyclerViewAdapter
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.location.LocationInfo
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
            homeViewModel.cancelTimer()
            homeViewModel.getWeather(LocationInfo(loc.latitude, loc.longitude))
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            homeViewModel.setWeatherInfoText(getString(R.string.location_service_off))
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            homeViewModel.showWeather()
        }
    }


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermitted ->
        homeViewModel.setUiState(Resource.Loading)
        if (isPermitted.not()) {
            homeViewModel.setWeatherInfoText(getString(R.string.location_permission))
        } else {
            homeViewModel.registerListener(locationListener)
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
        adapter = WeatherRecyclerViewAdapter()
        checkPermission()

        binding.run {
            weatherRefreshBtn.setOnClickListener {
                checkPermission()
            }
            refreshBtn.setOnClickListener {
                checkPermission()
            }

            golfBtnBackground.setOnClickListener {
                val intent = Intent(requireActivity(), SensorActivity::class.java)
                startActivity(intent)
            }

            weatherDetailRecyclerView.adapter = adapter
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.uiState.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        stateManager.dismissLoadingDialog()
                        showWeather()
                    }
                    is Resource.Failure -> {
                        stateManager.dismissLoadingDialog()
                        hideWeather(resource.exception.message ?: getString(R.string.network_fail))
                    }
                    is Resource.Loading -> {
                        stateManager.showLoadingDialog()
                    }
                }
            }

        }

        homeViewModel.weatherFailTextId.observe(viewLifecycleOwner) { id ->
            homeViewModel.setWeatherInfoText(getString(id))
        }

        homeViewModel.run {
            setWelcomeText(welcomeTextArray.random(random))
            setDisplayName()
        }
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

    private fun checkPermission() =
        locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun showWeather() {
        binding.weatherLayout.isVisible = true
        binding.weatherFailLayout.isVisible = false
    }

    private fun hideWeather(text: String) {
        binding.weatherLayout.isVisible = false
        binding.weatherFailLayout.isVisible = true
        binding.infoText.text = text
    }
}

