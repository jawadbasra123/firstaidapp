package com.firstaidnow.ui.hospital

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.firstaidnow.R
import com.firstaidnow.databinding.FragmentHospitalBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HospitalFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHospitalBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                enableMyLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                enableMyLocation()
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Location permission needed to find nearby hospitals",
                    Toast.LENGTH_LONG
                ).show()
                showDefaultLocation()
            }
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment)
            as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapContainer, it)
                    .commit()
            }

        mapFragment.getMapAsync(this)

        binding.btnFindHospitals.setOnClickListener {
            checkLocationPermission()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun enableMyLocation() {
        val map = googleMap ?: return
        try {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                    addSampleHospitalMarkers(userLatLng)
                    binding.tvHospitalInfo.text = "Showing nearby hospitals. Tap markers for details."
                } else {
                    showDefaultLocation()
                }
            }
        } catch (e: SecurityException) {
            showDefaultLocation()
        }
    }

    private fun showDefaultLocation() {
        val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        addSampleHospitalMarkers(defaultLocation)
        binding.tvHospitalInfo.text = "Enable location services to find hospitals near you."
    }

    private fun addSampleHospitalMarkers(center: LatLng) {
        val map = googleMap ?: return
        map.clear()

        // Add sample hospital markers around the user's location
        val hospitals = listOf(
            Pair("General Hospital", LatLng(center.latitude + 0.01, center.longitude + 0.008)),
            Pair("Community Medical Center", LatLng(center.latitude - 0.008, center.longitude + 0.012)),
            Pair("Children's Hospital", LatLng(center.latitude + 0.005, center.longitude - 0.01)),
            Pair("Emergency Care Center", LatLng(center.latitude - 0.012, center.longitude - 0.005)),
            Pair("University Hospital", LatLng(center.latitude + 0.015, center.longitude - 0.003)),
        )

        for ((name, position) in hospitals) {
            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(name)
                    .snippet("Tap for directions")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
