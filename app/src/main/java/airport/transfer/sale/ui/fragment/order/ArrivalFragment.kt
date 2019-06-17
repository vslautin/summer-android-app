package airport.transfer.sale.ui.fragment.order

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_address.*
import airport.transfer.sale.*
import airport.transfer.sale.adapter.AddressAdapter
import airport.transfer.sale.adapter.ViewHolderWrapper
import airport.transfer.sale.mvp.presenter.AddressPresenter
import airport.transfer.sale.mvp.view.AddressesView
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.ArrivalDetailsActivity_
import airport.transfer.sale.ui.activity.MapActivity_
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.EFragment
import java.util.*
import java.util.concurrent.TimeUnit

@EFragment(R.layout.fragment_address)
open class ArrivalFragment : BaseFragment(), AddressesView, GoogleApiClient.ConnectionCallbacks {

    @InjectPresenter
    lateinit var addressPresenter: AddressPresenter

    lateinit var addressAdapter: AddressAdapter

    var latLon : Pair<Float?, Float?>? = null

    var mGoogleApiClient: GoogleApiClient? = null
    var myLocation: Location? = null
    var shouldShowClosestAirports = true

    private var realm: Realm? = null

    private var firstObservable: Observable<CharSequence>? = null
    private var secondObservable: Observable<CharSequence>? = null

    companion object {
        const val KEY = "key"

        fun newInstance(isAirport: Boolean, previousText: String): ArrivalFragment {
            val args = Bundle()
            args.putBoolean(KEY, isAirport)
            args.putString(Constants.EXTRA_TEXT, previousText)
            val fragment = ArrivalFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        realm = Realm.getDefaultInstance()
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        forcedAirportDescription.visibility = View.GONE
        val isAirport = arguments.getBoolean(KEY)
        if (isAirport) {
            title.setText(R.string.enter_airport_name)
            showMapButton.visibility = View.GONE
            val forced = realm?.where(Address::class.java)?.equalTo("isForcedAirport", true)?.findFirst()
            forcedAirportDescription.visibility = if (forced != null) View.VISIBLE else View.GONE
        } else {
            addressEditText.setHint(R.string.choose_place)
        }
        val location = Preferences.getLocation(context)
        latLon = Pair<Float?, Float?>(location?.first, location?.second)
        //if (isLocationPermissionGranted(false)) requestLocationUpdates()
        /*if (latLon.first == null && Preferences.shouldAskLocationPermission(context)) {
            AlertDialog.Builder(context, R.style.AlertDialogStyle)
                    .setMessage(R.string.location_relevance)
                    .setPositiveButton(R.string.ok, { dialog, which ->
                        Preferences.setLocationPermissionAsked(context)
                        if (isLocationPermissionGranted(true)) requestLocationUpdates()
                    })
                    .show()
        }*/
        val useLocation = this !is DeliveryFragmentNew
        val lang = Locale.getDefault().language
        firstObservable = RxTextView.textChanges(addressEditText)
                .skip(1)
                .debounce(600, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        firstObservable?.subscribe {
                    shouldShowClosestAirports = false
                    if (it.isNotEmpty()) {
                        progressBar?.visibility = View.VISIBLE
                        if (isAirport) {
                            /*addressPresenter.getAirports(getString(R.string.google_places_key), it.toString(),
                                    if (useLocation) latLon.first else null, if (useLocation) latLon.second else null, lang)*/
                            addressPresenter.searchAirport(it.toString())//addressPresenter.search(it.toString(), context, true)
                        } else {
                            //addressPresenter.getAddresses(it.toString(), latLon.first, latLon.second, getString(R.string.google_places_key), lang)
                            addressPresenter.autocomplete(it.toString(), if (!isAirport) activity.intent.getStringExtra(Constants.EXTRA_AIRPORT) else null)
                        }
                    } else {
                        addressRecyclerView?.visibility = View.GONE
                        if (shouldShowMap()) showMapButton?.visibility = View.VISIBLE
                    }
                }
        showMapButton.setOnClickListener {
            val intent = Intent(context, MapActivity_::class.java)
            intent.putExtra(Constants.EXTRA_FLAG, this@ArrivalFragment is DeliveryFragmentNew)
            startActivityForResult(intent, Constants.REQUEST_ARRIVAL)
        }
        secondObservable = RxTextView.textChanges(addressEditText)
                .observeOn(AndroidSchedulers.mainThread())
        secondObservable?.subscribe {
                    if (it.isNotEmpty()) addressEditText?.setTypeface(null, Typeface.BOLD)
                    else addressEditText?.setTypeface(null, Typeface.NORMAL)
                }
        if (shouldShowClosestAirports && arguments.getBoolean(KEY)) {
            shouldShowClosestAirports = false
            //addressPresenter.getClosestAirports(lang)
        }
        arguments.getString(Constants.EXTRA_TEXT)?.let {
            addressEditText.setText(it)
            addressEditText.setSelection(it.length)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            requestLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ showKeyboard(context, addressEditText) }, 300)
    }

    open fun setupRecyclerView() {
        addressAdapter = AddressAdapter()
        val isAirport = arguments.getBoolean(KEY)
        addressAdapter.setClickListener(object : ViewHolderWrapper.ClickListener {

            override fun onItemClicked(position: Int, wrapper: ViewHolderWrapper<*>) {
                val address = addressAdapter.getItem(position)
                /*if (address.isAirport() && address.name == null && address.addressComponents?.find {
                    it.types.contains(AddressComponentType.STREET_NUMBER) } == null && address.addressComponents?.find {
                    it.types.contains(AddressComponentType.AIRPORT) } == null) {
                    addressEditText.setTextChars(address.getTitle())
                    return
                }*/
                if (!address.isTransfer) {
                    context.makeToast(R.string.available_soon)
                    return
                }
                val order = getCurrentOrder(realm)
                if (this@ArrivalFragment is DeliveryFragmentNew) order.destination = address
                else order.delivery = address
                saveCurrentOrder(realm, order)
                if (!isAirport) addressPresenter.getCoordinatesByAddress(address)
                else {
                    val intent = Intent(context, ArrivalDetailsActivity_::class.java)
                    intent.putExtra(Constants.EXTRA_TITLE, addressAdapter.getItem(position).title)
                    intent.putExtra(Constants.EXTRA_SUBTITLE, addressAdapter.getItem(position).subtitle)
                    val requestCode = if (this@ArrivalFragment is DeliveryFragmentNew) Constants.REQUEST_DESTINATION_FLIGHT
                    else Constants.REQUEST_ARRIVAL_FLIGHT
                    intent.putExtra(Constants.EXTRA_KEY, requestCode)
                    startActivityForResult(intent, requestCode)
                }
            }
        })
        addressRecyclerView.layoutManager = LinearLayoutManager(context)
        if (isAirport) {
            addressRecyclerView.addItemDecoration(AsymDividerItemDecoration(context, context.dip(38), context.dip(10), false))
        } else {
            addressRecyclerView.addItemDecoration(DividerItemDecoration(context, context.dip(10), false))
        }
        addressRecyclerView.adapter = addressAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_ARRIVAL) {
                activity.setResult(Activity.RESULT_OK, data)
                activity.finish()
            } else {
                val flightNumber = data?.getStringExtra(Constants.EXTRA_FLIGHT)
                val arrivalPlace = data?.getStringExtra(Constants.EXTRA_TITLE)
                val arrivalSubtitle = data?.getStringExtra(Constants.EXTRA_SUBTITLE)
                acceptArrival(arrivalPlace!!, arrivalSubtitle!!, flightNumber)
            }
        }
    }

    private fun acceptArrival(title: String, subtitle: String, flightNumber: String?) {
        val intent = Intent()
        intent.putExtra(Constants.EXTRA_TITLE, title)
        intent.putExtra(Constants.EXTRA_SUBTITLE, subtitle)
        flightNumber?.let { intent.putExtra(Constants.EXTRA_FLIGHT, it) }
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }

    override fun onAddressListReceived(addresses: List<Address>, isClosest: Boolean) {
        shouldShowClosestAirports = false
        if (isClosest && addresses.isNotEmpty()) closestLabel.visibility = View.VISIBLE
        else closestLabel.visibility = View.GONE
        if (addresses.isNotEmpty()) addressRecyclerView?.visibility = View.VISIBLE
        addressAdapter.replaceAll(addresses)
        progressBar.visibility = View.GONE
        if (shouldShowMap()) {
            showMapButton.visibility = if (addresses.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    fun shouldShowMap(): Boolean{
        return !arguments.getBoolean(KEY) && this !is DeliveryFragmentNew
    }

    fun requestLocationUpdates() {
        mGoogleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build()
        mGoogleApiClient?.connect()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener)
    }

    val locationListener = { location: Location ->
        myLocation = location
        latLon = Pair(myLocation?.latitude?.toFloat(), myLocation?.longitude?.toFloat())
    }

    override fun onPause() {
        super.onPause()
        myLocation?.let{ Preferences.saveLocation(it.latitude, it.longitude, context) }
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
        val order = getCurrentOrder(realm)
        if (this is DeliveryFragmentNew) {
            order.destination = address
        } else order.delivery = address
        saveCurrentOrder(realm, order)
        acceptArrival(address.title, address.subtitle, null)
    }

    override fun onCoordinatesAbsent() {
    }

    override fun onDestroy() {
        realm?.close()
        firstObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        secondObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        super.onDestroy()
    }
}