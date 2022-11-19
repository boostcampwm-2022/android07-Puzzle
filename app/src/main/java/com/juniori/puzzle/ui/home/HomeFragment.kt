package com.juniori.puzzle.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juniori.puzzle.R
import com.juniori.puzzle.adapter.WeatherRecyclerViewAdapter
import com.juniori.puzzle.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val random = Random(System.currentTimeMillis())
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private lateinit var adapter: WeatherRecyclerViewAdapter

    @SuppressLint("MissingPermission")
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermitted ->
        if (isPermitted) {
            println("isPermitted")
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                println("isnetwork")
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 3000L, 30f
                ) { location ->
                    homeViewModel.getWeather(location.latitude, location.longitude)
                }
            } else {
                homeViewModel.setWeatherInfoText("네트워크를 연결해주세요")
            }
        } else {
            homeViewModel.setWeatherInfoText("위치 권한을 허용해주세요")
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
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val welcomeTextArray = resources.getStringArray(R.array.welcome_text)
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        adapter = WeatherRecyclerViewAdapter()

        binding.weatherNotPermittedLayout.setOnClickListener {
            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        binding.weatherDetailRecyclerView.adapter = adapter

        homeViewModel.run {
            setWelcomeText(welcomeTextArray.random(random))
            setDisplayName()
            weatherList.observe(viewLifecycleOwner) { list ->
                binding.weatherLayout.isVisible = list.isNotEmpty()
                binding.weatherNotPermittedLayout.isVisible = list.isEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
