package com.example.basiclocationsamplecompose

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapsInitializer
import com.swapnil.basiclocationsamplecompose.ui.theme.BasicLocationComposeTheme


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequired: Boolean = false
    private val permissions=arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    //private var mCurrentLastLocationLatitude = mutableStateOf("0.0")
    //private var mCurrentLastLocationLongitude = mutableStateOf("0.0")

    private var mLocation: MutableState<Location?> = mutableStateOf(null)

 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(this,MapsInitializer.Renderer.LATEST){

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            BasicLocationComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp), //MaterialTheme.shapes.medium,
                    tonalElevation = 5.dp,
                    contentColor = Color.Blue,
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationScreen(this@MainActivity, mLocation)
                }
            }
        }
    }

 fun getLastLocation()
 {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) return

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Got last known location. In some rare situations this can be null
                //mCurrentLastLocationLatitude.value = (location?.latitude?: 0.0).toString()
                //mCurrentLastLocationLongitude.value = (location?.longitude?: 0.0).toString()
                mLocation.value = location?: null
            }
                .addOnFailureListener {
                    Toast.makeText(this,getString(R.string.failed), Toast.LENGTH_SHORT).show()
                }
}


 @Composable
 private fun LocationScreen(
        context: Context,
        loc: MutableState<Location?>
        ) {
        val launchMultiplePermissions= rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions() )
        {
                permissionMaps->
            val areGranted = permissionMaps.values.reduce{acc,next->acc && next}
            if (areGranted)
            {
                locationRequired = true
                getLastLocation()
                Toast.makeText(context,"Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit){
            if (permissions.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                getLastLocation()
            } else {
                launchMultiplePermissions.launch(permissions)
            }
        }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Your location is ${(loc.value)?.latitude} and ${(loc.value)?.longitude}",
                    color = Color.Black)
                Button(onClick = {
                    if (permissions.all {
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }) getLastLocation()
                    else {
                        launchMultiplePermissions.launch(permissions)
                    }
                }) {
                    Text(text = "Check permission and try again")
                }
            }
    }
}

