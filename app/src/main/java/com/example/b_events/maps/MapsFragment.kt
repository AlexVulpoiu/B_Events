package com.example.b_events.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.b_events.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap

    private val REQUEST_LOCATION_PERMISSION = 11

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap

        arguments?.let { it ->
            val args = MapsFragmentArgs.fromBundle(it)
            val location = args.location
            val geocoder = Geocoder(activity)
            val addressList = geocoder.getFromLocationName(location, 1) as ArrayList<Address>
            val address: Address = addressList[0]
            val homeLatLng = LatLng(address.latitude, address.longitude)

            map.addMarker(MarkerOptions().position(homeLatLng).title(location))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15f))

            val overlaySize = 100f
            val androidOverlay = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
                .position(homeLatLng, overlaySize)
            map.addGroundOverlay(androidOverlay)
        }

        setMapLongClick(map)
        setPoiClick(map)
        enableMyLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->
            val snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude, latLng.longitude)
            map.addMarker(MarkerOptions().position(latLng).title(getString(R.string.dropped_pin))
                .snippet(snippet))
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            poiMarker?.showInfoWindow()
        }
    }

    private fun isPermissionGranted() : Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            this.activity?.let {
                ActivityCompat.requestPermissions(it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}