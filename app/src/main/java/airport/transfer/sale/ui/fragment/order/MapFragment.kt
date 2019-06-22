package airport.transfer.sale.ui.fragment.order

import airport.transfer.sale.*
import airport.transfer.sale.mvp.presenter.AddressPresenter
import airport.transfer.sale.mvp.view.AddressesView
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.BaseFragment
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_map.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_map)
open class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        AddressesView {

    var mGoogleApiClient: GoogleApiClient? = null
    var myLocation: Location? = null
    var chosenAddress: Address? = null

    @InjectPresenter
    lateinit var presenter: AddressPresenter
    
    private var realm: Realm? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        chooseButton.setOnClickListener {
            if (addressTitle.text.isNotEmpty() && chosenAddress != null) {
                val order = getCurrentOrder(realm)
                if (activity.intent.getBooleanExtra(Constants.EXTRA_FLAG, false)) {
                    chosenAddress?.let { order.destination = it }
                } else {
                    chosenAddress?.let { order.delivery = it }
                }
                saveCurrentOrder(realm, order)
                val intent = Intent()
                intent.putExtra(Constants.EXTRA_TITLE, addressTitle.text)
                intent.putExtra(Constants.EXTRA_SUBTITLE, addressSubtitle.text)
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
            } else context.makeToast(R.string.try_choose_another_point)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLocationPermissionGranted(true)) requestLocationUpdates()
    }

    override fun onMapReady(map: GoogleMap?) {
        val order = getCurrentOrder(realm)
        val location = if (activity.intent.getBooleanExtra(Constants.EXTRA_FLAG, false)) {
            order.destination
        } else order.delivery
        val lastLocation = Preferences.getLocation(context)
        val latLng = when {
            location != null && location.lat != 200F && location.lng != 200F -> LatLng(location.lat!!.toDouble(), location.lng!!.toDouble())
            lastLocation != null -> LatLng(lastLocation.first.toDouble(), lastLocation.second.toDouble())
            myLocation != null -> myLocation?.let { LatLng(it.latitude, it.longitude) }
            else -> LatLng(36.898096, 30.802386)
        }
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        minusButton.setOnClickListener { map?.animateCamera(CameraUpdateFactory.zoomOut()) }
        plusButton.setOnClickListener { map?.animateCamera(CameraUpdateFactory.zoomIn()) }
        locationButton.setOnClickListener {
            myLocation?.let {
                map?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
            }
        }
        map?.setOnCameraIdleListener {
            presenter.getAddressByCoordinates(
                    map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        }
    }

    override fun onAddressListReceived(addresses: List<Address>, isClosest: Boolean) {
        chosenAddress = addresses.firstOrNull()
        addressTitle.text = chosenAddress?.title
        addressSubtitle.text = chosenAddress?.subtitle
        //TODO: other fields to this VIEW
    }

    fun requestLocationUpdates() {
        mGoogleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build()
        mGoogleApiClient?.connect()
    }

    override fun onConnected(p0: Bundle?) {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener)
    }

    val locationListener = { location: Location -> myLocation = location }

    override fun onPause() {
        super.onPause()
        myLocation?.let { Preferences.saveLocation(it.latitude, it.longitude, context) }
        mGoogleApiClient?.let {
            if (it.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(it, locationListener)
                it.disconnect()
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onCoordinatesReceived(address: Address) {
    }

    override fun onCoordinatesAbsent() {
        addressTitle.setText(R.string.address_not_found)
        addressSubtitle.setText(R.string.try_choose_another_point)
        chosenAddress = null
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }
}