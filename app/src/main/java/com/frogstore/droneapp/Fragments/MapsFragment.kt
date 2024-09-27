package com.frogstore.droneapp.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.frogstore.droneapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// MapsFragment handles the display and interaction with a Google Map in the app
// It also manages location-related functionality such as showing the user's current location on the map.
class MapsFragment : Fragment(),
    GoogleMap.OnMyLocationButtonClickListener,  // Handle "My Location" button click events
    GoogleMap.OnMyLocationClickListener {       // Handle click events on the user's location

    private lateinit var map: GoogleMap // GoogleMap object that will hold the map reference once ready
    private var permissionDenied = false // Flag to track if location permissions were denied
    private val LOCATION_PERMISSION_REQUEST_CODE = 1 // Request code for location permission

    // Callback that is triggered once the map is ready to be used
    private val callback = OnMapReadyCallback { googleMap ->
        // Store the map reference in the 'map' variable
        map = googleMap

        // Set listeners for "My Location" button clicks and user location clicks
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)

        // Enable the user's location if permissions are granted
        enableMyLocation()
    }

    // Inflates the layout for this fragment which contains the map fragment view.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_maps layout and return it
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    // This method is called after the view has been created. It sets up the map fragment.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the child SupportMapFragment (the map container) and set up the map asynchronously
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        // When the map is ready, trigger the callback defined earlier
        mapFragment?.getMapAsync(callback)
    }

    /**
     * Enables the My Location layer if the fine or coarse location permission has been granted.
     * This method checks for permissions and requests them if necessary.
     */
    @SuppressLint("MissingPermission") // Suppresses lint warnings for missing location permissions (handled at runtime)
    private fun enableMyLocation() {
        // Check if the location permissions (fine or coarse) are granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // If permissions are granted, enable the location layer on the map
            map.isMyLocationEnabled = true
        } else {
            // If permissions are not granted, request the necessary permissions
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,    // Precise location
                    Manifest.permission.ACCESS_COARSE_LOCATION   // Approximate location
                ),
                LOCATION_PERMISSION_REQUEST_CODE // Request code to handle the permission result
            )
        }
    }

    // This method handles the result of the permission request (granted or denied)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Check if the request code matches the location permission request
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If the permission was granted, enable the location layer
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                // If the permission was denied, set the permissionDenied flag to true
                permissionDenied = true
            }
        }
    }

    // This method is called when the fragment is resumed and handles post-permission denial behavior.
    override fun onResume() {
        super.onResume()
        // If the permission was denied earlier, show a message to the user.
        if (permissionDenied) {
            Toast.makeText(context, "Location permission is required to show the current location.", Toast.LENGTH_SHORT).show()
            permissionDenied = false // Reset the flag after showing the message
        }
    }

    // This callback handles the event when the "My Location" button is clicked.
    // Returning false allows the default behavior (moving the camera to the user's location).
    override fun onMyLocationButtonClick(): Boolean {
        return false // Return false to allow the default behavior of focusing on user's location
    }

    // This callback is triggered when the user clicks on their current location on the map.
    // A toast message is displayed showing the coordinates of the current location.
    override fun onMyLocationClick(location: Location) {
        // Show a toast with the current location details (latitude and longitude)
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }
}
