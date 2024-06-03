package com.example.storyapp.view.maps

import android.content.ContentValues
import android.content.Intent
import android.content.res.Resources
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.MapsModelFactory
import com.example.storyapp.R
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.databinding.CustomMapsBinding
import com.example.storyapp.databinding.FragmentMapsBinding
import com.example.storyapp.view.login.dataStore
import com.example.storyapp.view.story.StoryDetailActivity
import com.example.storyapp.view.utils.AuthViewModel
import com.example.storyapp.view.utils.ConvertLocation
import com.example.storyapp.view.utils.PreferenceManager
import com.example.storyapp.view.utils.convertBitmap
import com.example.storyapp.view.utils.convertDateTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var boundsBuilder = LatLngBounds.Builder()
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefen: PreferenceManager
    private lateinit var token: String
    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(this, MapsModelFactory(requireContext()))[MapsViewModel::class.java]
    }

    private fun setMarker(data: List<StoryEntity>) {
        lateinit var locationZoom: LatLng
        data.forEach { storyEntity ->
            if (storyEntity.lat != null && storyEntity.lon != null) {
                val latLng = LatLng(storyEntity.lat ?: 0.0, storyEntity.lon ?: 0.0)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(storyEntity.name)
                )?.tag = storyEntity
                boundsBuilder.include(latLng)
                locationZoom = latLng
            }
        }

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                locationZoom, 5f
            )
        )
    }

    private fun selectedStory(story: StoryEntity) {
        val intent = Intent(requireContext(), StoryDetailActivity::class.java)
        intent.putExtra(StoryDetailActivity.EXTRA_DETAIL_STORY, story)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        requireContext().startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setMapStyle() {
        val randomStyleIndex = (0..1).random()

        val selectedStyle = when (randomStyleIndex) {
            0 -> R.raw.map_style
            1 -> R.raw.map_style_2
            else -> throw IllegalArgumentException("Invalid randomStyleIndex")
        }

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    selectedStyle
                )
            )
            if (!success) {
                Timber.tag(ContentValues.TAG).e("Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Timber.tag(ContentValues.TAG).e(exception, "Can't find style, Error: ")
        }
    }


    override fun getInfoWindow(marker: Marker): View {
        val toolTips = CustomMapsBinding.inflate(LayoutInflater.from(requireContext()))
        if (marker.tag is StoryEntity) {
            val data: StoryEntity = marker.tag as StoryEntity
            toolTips.tvLocation.text = ConvertLocation.getStringAddress(
                ConvertLocation.convertLatLng(marker.position.latitude, marker.position.longitude),
                requireContext()
            )
            toolTips.name.text = data.name
            toolTips.ivStory.setImageBitmap(convertBitmap(requireContext(), data.photoUrl ?: ""))
            toolTips.tvStorycaptions.text = data.description
            toolTips.tvDateupload.text = convertDateTime(data.createdAt.toString())
        }

        return toolTips.root
    }


    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        prefen = PreferenceManager.getInstance(requireContext().dataStore)
        val authViewModel =
            ViewModelProvider(this, MapsViewModelFactory(prefen))[AuthViewModel::class.java]
        authViewModel.getUserLoginToken().observe(viewLifecycleOwner) {
            token = it
            viewModel.getListStoryLocation(token)
        }

        viewModel.storyStatus.observe(viewLifecycleOwner) { storyStatus ->
            val isError = viewModel.isErrorStory
            if (isError && !storyStatus.isNullOrEmpty()) {
                Snackbar.make(binding.root, storyStatus, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoadingStory.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.stories.observe(viewLifecycleOwner) {
            if (it != null) {
                setMarker(it)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            val data: StoryEntity = marker.tag as StoryEntity
            selectedStory(data)
        }
        setMapStyle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
