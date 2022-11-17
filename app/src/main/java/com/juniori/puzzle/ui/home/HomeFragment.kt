package com.juniori.puzzle.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentHomeBinding
import com.juniori.puzzle.util.CurrentLocationManager
import dagger.hilt.android.AndroidEntryPoint
import java.security.Permission
import java.util.jar.Manifest
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val random = Random(System.currentTimeMillis())
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermitted ->
        if (isPermitted) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000L, 30f
                ) {
                    val longitude = it.longitude.toInt()
                    val latitude = it.latitude.toInt()
                    homeViewModel.getWeather(latitude, longitude)
                }
            } else {
                homeViewModel.setWeatherInfoText("네트워크를 연결해주세요")
            }
        } else {
            homeViewModel.setWeatherInfoText("위치 권한을 허용해주세요")
            println("isDenied")
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

        binding.weatherNotPermittedLayout.setOnClickListener {
            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        homeViewModel.setWelcomeText(welcomeTextArray.random(random))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
