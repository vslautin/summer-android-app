package airport.transfer.sale.rest

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.request.TokenRequest
import airport.transfer.sale.rest.models.request.UpdateProfileRequest
import airport.transfer.sale.rest.models.response.CreateOrderResponse
import airport.transfer.sale.rest.models.response.PriceResponse
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.rest.models.response.model.v2.Order
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import rx.Observable
import rx.schedulers.Schedulers
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import javax.net.ssl.*


class RestService {

    private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .socketFactory(SocketFactory.getDefault())
            //.sslSocketFactory(getSocketFactory(), trustManagerForCertificates())
            .build()!!

    private fun getSocketFactory(): SSLSocketFactory{
        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManagerForCertificates()), null)
        return sslContext.socketFactory
    }

    private fun trustManagerForCertificates(): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(Buffer().writeUtf8(Constants.CERTIFICATE).inputStream())
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        for ((index, certificate) in certificates.withIndex()) {
            val certificateAlias = Integer.toString(index)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }
        val keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val `in`: InputStream? = null // By convention, 'null' creates an empty key store.
        keyStore.load(`in`, password)
        return keyStore
    }

    private val retrofit = Retrofit.Builder()
            .baseUrl(if (Constants.isTestServer) Api.TEST_BASE_API_URL else Api.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .build()!!

    private val placesRetrofit = Retrofit.Builder()
            .baseUrl(Api.GOOGLE_PLACES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .build()!!

    private val mapsRetrofit = Retrofit.Builder()
            .baseUrl(Api.GOOGLE_MAPS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .build()!!

    private val api = retrofit.create(Api::class.java)!!

    private val placesApi = placesRetrofit.create(Api::class.java)!!

    private val mapsApi = mapsRetrofit.create(Api::class.java)!!

    fun getCode(phone: String) = api.getCode(phone)

    fun getToken(request: TokenRequest) = api.getToken(request.phone, request.code)

    fun updateUser(authToken: String, request: UpdateProfileRequest) = api.updateProfile(authToken,
            request.email, request.firstName, request.lastName, request.receiveEmails)

    fun search(query: String, lat: Float?, lon: Float?) = api.search(query, lat, lon)

    fun searchAirport(query: String, lat: Float?, lon: Float?) = api.searchAirport(query,lat, lon)

    fun getUser(authToken: String) = api.getUser(authToken)

    fun getLocationName(lat: Double, lon: Double) = api.getLocationName(lat, lon)

    fun getPlans() = api.getPlans()

    fun getAirlines(query: String) = api.getAirlines(query)

    fun getAirlines() = api.getAirlines()

    fun createOrder(authToken: String, order: Order, crypto: String?): Observable<CreateOrderResponse> {
        val builder = order.getRequestBody(false, authToken)
        if (crypto != null)
            builder.add("cryptogram", crypto)
        return api.createOrder(builder.build())
    }

    fun getCurrentOrders(authToken: String) = api.getCurrentOrders(authToken)

    fun getCompletedOrders(authToken: String, limit: Int, offset: Int) = api.getCompletedOrders(authToken, limit, offset)

    fun getFavoriteOrders(authToken: String, limit: Int, offset: Int) = api.getFavoriteOrders(authToken, limit, offset)

    fun addFavorite(authToken: String, id: Long) = api.addFavorite(id, authToken)

    fun editOrder(authToken: String, id: Long, order: Order): Observable<CreateOrderResponse> {
        return api.editOrder(id,  order.getRequestBody(true, authToken).build())
    }

    fun cancelOrder(authToken: String, id: Long) = api.cancelOrder(id, authToken)

    fun getPrice(order: Order): Observable<PriceResponse> {
        return api.getPrice( order.getRequestBody(false, null).build())
    }

//    fun getPlans(order: Order, coupon: String) = api.getPlans(RequestBody.create(MediaType.parse("text/plain"), order.getPlansRequestString(coupon)))

    fun sendMessage(message: String, token: String) = api.sendMessage(token, message)

    fun sendFeedback(message: String, token: String) = api.sendFeedback(token, message)

    fun getMessages(token: String, limit: Int, offset: Int) = api.getMessages(token, limit, offset)

    fun addFeedback(token: String, id: Long, comment: String?, rate: Int) = api.addFeedback(id, token, comment, rate)

    fun getPlaces(key: String, query: String, lat: Float?, lon: Float?, lang: String) = placesApi.getPlaces(key, query, if (lat == null) null else lat.toString() + "," + lon, lang)

    fun getAddress(query: String, key: String, lang: String, bounds: String?) = mapsApi.geocode(query, key, lang, bounds)

    fun getAddress(latlng: String, key: String, lang: String) = mapsApi.geocode(latlng, key, lang)

    fun getNearestAirports(key: String, lang: String, location: String) = placesApi.getNearPlaces(key, location, "airport",  lang)

    fun getAirportPlaces(key: String, query: String, location: String?, lang: String) = placesApi.getAirportPlaces(key, query, location, lang)

    fun autocomplete(query: String, location: String? = null) = api.autocomplete(query)

    fun geodecode(lat: Double, lng: Double) = api.geodecode(lat.toString() + "," + lng)

    fun getCoordinates(address: Address) = api.getCoordinates(address.title + " " + address.subtitle)

    fun getAirports(query: String) = api.getAirports(query)

    fun logout(token: String) = api.logout(token)

    fun checkForcedAirport() = api.checkForcedAirport()

    fun addPushToken(token: String, pushToken: String) = api.addPushToken(token, pushToken)

    fun getPublicId(token: String) = api.getPublicId(token)

    fun addCard(token: String, holder: String, crypto: String, number: String) = api.addCard(token, holder, crypto, number)

    fun getCards(token: String) = api.getCards(token)

    fun deleteCard(token: String, id: Long) = api.deleteCard(id, token)

    fun defaultCard(token: String, id: Long) = api.defaultCard(id, token)
}
