package com.kotlin.maps

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kotlin.maps.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {
                mMap.clear()
                val guncelKonum = LatLng(p0.latitude, p0.longitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("şu an buradasın"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum, 15f))

                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                var adres = ""

                try {
                    val adresListesi = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if(adresListesi.size > 0){
                        if (adresListesi[0].thoroughfare != null){
                            adres += adresListesi.get(0).thoroughfare
                        }
                        if(adresListesi[0].subThoroughfare != null){
                            adres += adresListesi.get(0).subThoroughfare
                        }
                    }
                }catch(e : Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(guncelKonum).title(adres))
            }
        }
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null){
                val sonBilinenLetLng = LatLng(sonBilinenKonum.latitude, sonBilinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenLetLng).title("en son buradaydın"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLetLng, 15f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}