package com.juniori.puzzle.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationListener
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
    private val locationManager: LocationManager by lazy {
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private lateinit var adapter: WeatherRecyclerViewAdapter
    private val geoCoder: Geocoder by lazy {
        Geocoder(requireContext())
    }
    private val locationListener = LocationListener { loc ->
        getWeatherInfo(loc.latitude, loc.longitude)
    }

    @SuppressLint("MissingPermission")
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermitted ->
        if (isPermitted) {
            println("permitted")
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                var location = locationManager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                )
                if (location == null) {
                    location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 3000L, 30f, locationListener
                )
                println("location available $location")
                val latitude = location?.latitude ?: 37.0
                val longitude = location?.longitude ?: 127.0
                getWeatherInfo(latitude, longitude)
            } else {
                homeViewModel.setWeatherInfoText("네트워크 및 위치 서비스를 연결해주세요")
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
        locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        adapter = WeatherRecyclerViewAdapter()

        binding.weatherNotPermittedLayout.setOnClickListener {
            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        binding.weatherRefreshBtn.setOnClickListener {
            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        binding.weatherDetailRecyclerView.adapter = adapter

        homeViewModel.run {
            setWelcomeText(welcomeTextArray.random(random))
            setDisplayName()
            weatherInfoText.observe(viewLifecycleOwner) { text ->
                println("observe $text")
                binding.weatherLayout.isVisible = text.isEmpty()
                binding.weatherNotPermittedLayout.isVisible = text.isNotEmpty()
            }
        }
    }

    private fun getWeatherInfo(latitude: Double, longitude: Double) {
        val address = geoCoder.getFromLocation(latitude, longitude, 1)
        homeViewModel.setCurrentAddress(address)
        homeViewModel.getWeather(latitude, longitude)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager.removeUpdates(locationListener)
        _binding = null
    }
}
