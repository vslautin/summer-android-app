package airport.transfer.sale.mvp.presenter

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.Constants
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.AddressesView
import airport.transfer.sale.networkError
import airport.transfer.sale.rest.models.response.model.Airport
import airport.transfer.sale.rest.models.response.model.v2.Address
import retrofit2.adapter.rxjava.HttpException
import ru.aviasales.core.AviasalesSDK
import ru.aviasales.core.places.OnNearestPlacesListener
import ru.aviasales.core.search_airports.`object`.PlaceData
import ru.aviasales.core.search_airports.interfaces.OnSearchPlacesListener
import ru.aviasales.core.search_airports.params.SearchByNameParams
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class AddressPresenter : BasePresenter<AddressesView>() {

    fun getAddresses(name: String, lat: Float?, lon: Float?, placesKey: String, lang: String) {
        /*var addresses: List<Airport>? = null
        var places: List<Airport>? = null
        var bounds: String? = null
        if (lat != null && lon != null) bounds = (lat - 1).toString() + "," + (lon - 1) + "|" + (lat + 1) + "," + (lon + 1)
        restService.getAddress(name, placesKey, lang, bounds*//*, lat, lon*//*).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    *//*if (response.status == Constants.Status.SUCCESS) {*//*
                    addresses = response.results
                    if (places != null) viewState.onAddressListReceived(combineAddressesWithPlaces(addresses!!, places!!), false)
                } *//*else networkError(Exception(response.message!!))}*//*,
                { error -> networkError(error) }
        )
        restService.getPlaces(placesKey, name, lat, lon, lang).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    places = response.results
                    if (addresses != null) viewState.onAddressListReceived(combineAddressesWithPlaces(addresses!!, places!!), false)
                },
                { error -> networkError(error) }
        )*/
    }

    fun autocomplete(address: String, location: String? = null) {
        restService.autocomplete(address, location).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    viewState.onAddressListReceived(response.results, false)
                },
                { error -> if (error is HttpException) {
                    val message = error.getErrorString()
                    if (message != null) viewState.onError(error.code(), message)
                } else networkError(error) }
        )
    }

    fun search(name: String, context: Context, isAirport: Boolean) {
        val params = SearchByNameParams()
        params.name = name
        params.locale = "ru"
        params.context = context
        AviasalesSDK.getInstance().startPlacesSearch(params, object : OnSearchPlacesListener {

            override fun onSuccess(result: MutableList<PlaceData>?) {
                if (result != null) {
                    if (isAirport) {
                        viewState.onAddressListReceived(result
                                .filter { it.airportName != null && !isRailway(it.airportName) }
                                .map { data ->
                                    val title = data.airportName
                                    val subtitle = data.cityName + ", " + data.country
                                    Address(title, subtitle, data.coordinates[0].toFloatOrNull(), data.coordinates[1].toFloatOrNull(), true)
                                }, false)
                    } else {
                        viewState.onAddressListReceived(result.map { data ->
                            val title = data.airportName ?: data.name
                            val subtitle = data.cityName + ", " + data.country
                            Address(title, subtitle, data.coordinates[0].toFloatOrNull(),
                                    data.coordinates[1].toFloatOrNull(), data.airportName != null)
                        }, false)
                    }
                }
            }

            override fun onCanceled() {
            }

            override fun onError(p0: Int, p1: Int, p2: String?) {
            }

        })
    }

    fun searchAirport(name: String){
        restService.getAirports(name).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    viewState.onAddressListReceived(response.results.map { data ->
                        Address(data.title, data.subtitle, data.lat, data.lng, true, data.isTransfer)
                    }, false)
                },
                { error -> if (error is HttpException) {
                    val message = error.getErrorString()
                    if (message != null) viewState.onError(error.code(), message)
                } else networkError(error) }
        )
    }

    fun isRailway(airport: String): Boolean {
        val words = listOf<String>("Railway", "Railstation", "Rail way", "Rail", "вокзал")
        words.forEach { if (airport.contains(it, true)) return true }
        return false
    }

    fun combineAddressesWithPlaces(addresses: List<Airport>, places: List<Airport>): List<Airport> {
        val removingIds = ArrayList<String>()
        addresses.forEach { address ->
            places.forEach { place ->
                if ((place.placeId == address.id || place.placeId == address.placeId) && (place.name != null && !place.name!!.contains("Push"))) {
                    address.placeId?.let { removingIds.add(it) }
                }
            }
        }
        val mutableAddresses = addresses.toMutableList()
        removingIds.forEach { remodedId ->
            val removed = mutableAddresses.find { it.placeId == remodedId }
            removed?.let { mutableAddresses.remove(it) }
        }
        /*places.forEach { place ->
            addresses.find { address ->
                (place.placeId == address.id || place.placeId == address.placeId) && (place.name != null && !place.name!!.contains("Push"))
            }?.name = place.name
        }*/
        return mutableAddresses.plus(places).sortedByDescending { it.name }
    }

    fun getAirports(key: String, name: String, lat: Float?, lon: Float?, lang: String) {
        /*restService.getAirportPlaces(key, name, null, lang).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onAddressListReceived(response.results, false) },
                { error -> networkError(error) }
        )*/
    }

    fun getClosestAirports(lang: String) {
        AviasalesSDK.getInstance().getNearestPlaces(lang, object : OnNearestPlacesListener {
            override fun onError(p0: Int) {}
            override fun onSuccess(result: MutableList<PlaceData>?) {
                if (result != null) {
                    viewState.onAddressListReceived(result
                            .filter { it.airportName != null && !isRailway(it.airportName) }
                            .map { data ->
                                val title = data.airportName
                                val subtitle = data.cityName + ", " + data.country
                                Address(title, subtitle, data.coordinates[0].toFloatOrNull(), data.coordinates[1].toFloatOrNull(), true)
                            }, true)
                }
            }
        })
    }

    fun getAddressByCoordinates(lat: Double, lon: Double) {
        restService.geodecode(lat, lon).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onAddressListReceived(listOf(Address(response.title, response.subtitle, lat.toFloat(), lon.toFloat())), false)
                    } else networkError(Exception(response.message!!))
                },
                { error -> if (error is HttpException && error.code() == 404) viewState.onCoordinatesAbsent()
                    else if (error is HttpException) {
                    val message = error.getErrorString()
                    if (message != null) viewState.onError(error.code(), message)
                } else networkError(error) }
        )
    }

    fun getCoordinatesByAddress(address: Address){
        restService.getCoordinates(address).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        address.lat = response.lat
                        address.lng = response.lng
                        viewState.onCoordinatesReceived(address)
                    } else networkError(Exception(response.message!!))
                },
                { error -> if (error is HttpException) {
                    val message = error.getErrorString()
                    if (message != null) viewState.onError(error.code(), message)
                } else networkError(error) }
        )
    }
}