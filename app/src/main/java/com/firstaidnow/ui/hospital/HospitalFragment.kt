package com.firstaidnow.ui.hospital

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.firstaidnow.BuildConfig
import com.firstaidnow.R
import com.firstaidnow.data.remote.RetrofitClient
import com.firstaidnow.databinding.FragmentHospitalBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class HospitalFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHospitalBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val MAPS_API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.btnFindHospitals.setOnClickListener {
            checkLocationPermission()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        checkLocationPermission()

        // 设置标记点击监听
        map.setOnMarkerClickListener { marker ->
            val hospitalName = marker.title
            val position = marker.position
            binding.tvHospitalInfo.text = "Selected: $hospitalName\nTap 'Navigate' to get directions."

            // 显示导航按钮 (假设布局里有这个按钮)
            // 这里我们直接弹出一个对话框或者更新 UI
            marker.showInfoWindow()
            true
        }

        // 设置 InfoWindow 点击监听（点击气泡导航）
        map.setOnInfoWindowClickListener { marker ->
            launchNavigation(marker.position, marker.title ?: "Hospital")
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun enableMyLocation() {
        val map = googleMap ?: return
        try {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
                    searchNearbyHospitals(currentLatLng)
                }
            }
        } catch (e: SecurityException) {
            Log.e("HospitalFragment", "Permission error", e)
        }
    }

    private fun searchNearbyHospitals(location: LatLng) {
        val locationString = "${location.latitude},${location.longitude}"

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.placesApi.getNearbyHospitals(
                    location = locationString,
                    apiKey = MAPS_API_KEY
                )

                if (response.status == "OK") {
                    displayHospitals(response.results)
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.status}", Toast.LENGTH_SHORT).show()
                    Log.e("HospitalFragment", "Places API Error: ${response.error_message}")
                }
            } catch (e: Exception) {
                Log.e("HospitalFragment", "Network error", e)
                Toast.makeText(requireContext(), "Failed to fetch hospitals", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayHospitals(hospitals: List<com.firstaidnow.data.remote.PlaceResult>) {
        googleMap?.clear()

        if (hospitals.isEmpty()) {
            binding.tvHospitalInfo.text = "No hospitals found nearby."
            return
        }

        for (hospital in hospitals) {
            val pos = LatLng(hospital.geometry.location.lat, hospital.geometry.location.lng)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title(hospital.name)
                    .snippet(hospital.vicinity ?: "Tap to navigate")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }

        binding.tvHospitalInfo.text = "Found ${hospitals.size} hospitals near you. Tap a marker to navigate."
    }

    private fun launchNavigation(latLng: LatLng, name: String) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // 如果没装 Google Maps，尝试用浏览器打开
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${latLng.latitude},${latLng.longitude}"))
            startActivity(webIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
