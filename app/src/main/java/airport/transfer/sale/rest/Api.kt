package airport.transfer.sale.rest

import airport.transfer.sale.rest.models.response.*
import airport.transfer.sale.rest.models.response.model.StartResponse
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
import java.util.*


interface Api {

    companion object {
        const val BASE_URL = "transfer.sale"
        const val BASE_API_URL = "https://$BASE_URL/cl/v1/"
        const val TEST_BASE_URL = "185.235.130.31"
        const val TEST_BASE_API_URL = "http://$TEST_BASE_URL/cl/v1/"

        const val GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/"
        const val GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/"
        const val AUTHORIZATION = "token"
        const val LANGUAGE = "Language"

        const val GET_CODE = "auth/phone"
        const val GET_TOKEN = "auth/code"
        const val UPDATE = "profile/update"
        const val SEARCH = "geo/search?limit=20&offset=0"
        const val SEARCH_AIRPORT = "geo/airports?limit=20&offset=0"
        const val GEO_BACK = "geo/back"
        const val GET_USER = "profile"
        const val GET_PLANS = "plans"
        const val AIRLINES = "airlines/search"
        const val ALL_AIRLINES = "airlines?limit=20&offset=0"
        const val CREATE_ORDER = "orders"
        const val CURRENT_ORDERS = "orders?limit=20&offset=0"
        const val COMPLETED_ORDERS = "orders/completed"
        const val FAVORITE_ORDERS = "orders/favorites"
        const val ADD_FAVORITE = "orders/{id}/favorite"
        const val EDIT_ORDER = "orders/{id}/update"
        const val CANCEL_ORDER = "orders/{id}/cancel"
        const val PRICE = "orders/calculator"
        const val MESSAGE = "support/messages"
        const val FEEDBACK = "feedback"
        const val ORDER_FEEDBACK = "orders/{id}/review"

        const val SEARCH_PLACES = "textsearch/json"
        const val SEARCH_CLOSE_PLACES = "nearbysearch/json?rankby=distance"
        const val SEARCH_GEOCODE = "geocode/json"

        const val AUTOCOMPLETE = "geo/autocomplete"
        const val GEODECODE = "geo/geodecode"
        const val GET_COORDINATES = "geo/geocode"
        const val GET_AIRPORTS = "geo/airport"

        const val LOGOUT = "auth/logout"
        const val FORCED_AIRPORT = "geo/start"

        const val NOTIFICATION = "notification"
        const val PUBLIC_ID = "cloudPayments/publicID"
        const val ADD_CARD = "cloudPayments"
        const val CARDS = "cloudPayments"
        const val DEFAULT_CARD = "cloudPayments/{id}/default"
        const val DELETE_CARD = "cloudPayments/{id}"
    }

    @FormUrlEncoded
    @POST(GET_CODE)
    fun getCode(@Field("phone") phone: String,
                @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<BaseResponse>

    @FormUrlEncoded
    @POST(GET_TOKEN)
    fun getToken(@Field("phone") phone: String,
                 @Field("code") code: String,
                 @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<TokenResponse>

    @FormUrlEncoded
    @POST(UPDATE)
    fun updateProfile(@Field(AUTHORIZATION) authToken: String,
                      @Field("email") email: String,
                      @Field("first_name") name: String,
                      @Field("last_name") lastName: String?,
                      @Field("receive_emails") receiveEmails: Int?,
                      @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<ProfileResponse>

    @GET(SEARCH)
    fun search(@Query("query") query: String,
               @Query("lat") lat: Float?,
               @Query("lon") lon: Float?,
               @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<SearchAirportResponse>

    @GET(SEARCH_AIRPORT)
    fun searchAirport(@Query("query") query: String,
                      @Query("lat") lat: Float?,
                      @Query("lon") lon: Float?,
                      @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<SearchAirportResponse>

    @GET(GEO_BACK)
    fun getLocationName(@Query("lat") lat: Double,
                        @Query("lon") lon: Double,
                        @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<SearchAirportResponse>

    @GET(GET_USER)
    fun getUser(@Query(AUTHORIZATION) authToken: String,
                @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<ProfileResponse>

    @GET(GET_PLANS)
    fun getPlans(@Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<PlansResponse>

    @POST(GET_PLANS)
    fun getPlans(@Body body: RequestBody): Observable<PlansResponse>

    @GET(AIRLINES)
    fun getAirlines(@Query("query") query: String,
                    @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<AirlineResponse>

    @GET(ALL_AIRLINES)
    fun getAirlines(): Observable<AirlineResponse>

    @POST(CREATE_ORDER)
    fun createOrder(@Body body: RequestBody): Observable<CreateOrderResponse>

    @GET(CURRENT_ORDERS)
    fun getCurrentOrders(@Query(AUTHORIZATION) authToken: String,
                         @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<TripsResponse>

    @GET(COMPLETED_ORDERS)
    fun getCompletedOrders(@Query(AUTHORIZATION) authToken: String,
                           @Query("limit") limit: Int,
                           @Query("offset") offset: Int,
                           @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<TripsResponse>

    @GET(FAVORITE_ORDERS)
    fun getFavoriteOrders(@Query(AUTHORIZATION) authToken: String,
                          @Query("limit") limit: Int,
                          @Query("offset") offset: Int,
                          @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<TripsResponse>

    @FormUrlEncoded
    @POST(ADD_FAVORITE)
    fun addFavorite(@Path("id") id: Long,
                    @Field(AUTHORIZATION) authToken: String,
                    @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<CreateOrderResponse>

    @POST(EDIT_ORDER)
    fun editOrder(@Path("id") id: Long,
                  @Body order: RequestBody): Observable<CreateOrderResponse>

    @DELETE(CANCEL_ORDER)
    fun cancelOrder(@Path("id") id: Long,
                    @Query(AUTHORIZATION) authToken: String,
                    @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<BaseResponse>

    @POST(PRICE)
    fun getPrice(@Body body: RequestBody): Observable<PriceResponse>

    @POST(MESSAGE)
    fun sendMessage(@Query(AUTHORIZATION) authToken: String,
                    @Query("body") message: String,
                    @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<MessageResponse>

    @POST(FEEDBACK)
    fun sendFeedback(@Query(AUTHORIZATION) authToken: String,
                     @Query("text") text: String,
                     @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<BaseResponse>

    @GET(MESSAGE)
    fun getMessages(@Query(AUTHORIZATION) authToken: String,
                    @Query("limit") limit: Int,
                    @Query("offset") offset: Int,
                    @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<MessagesResponse>

    @POST(ORDER_FEEDBACK)
    fun addFeedback(@Path("id") id: Long,
                    @Query(AUTHORIZATION) authToken: String,
                    @Query("comment") comment: String?,
                    @Query("rate") rate: Int): Observable<CreateOrderResponse>

    @GET(SEARCH_PLACES)
    fun getPlaces(@Query("key") key: String,
                  @Query("query") query: String,
                  @Query("location") location: String?,
                  @Query("language") lang: String): Observable<PlacesResponse>

    @GET(SEARCH_GEOCODE)
    fun geocode(@Query("address") address: String,
                @Query("key") key: String,
                @Query("language") lang: String,
                @Query("bounds") bounds: String?): Observable<PlacesResponse>

    @GET(SEARCH_GEOCODE)
    fun geocode(@Query("latlng") latlng: String,
                @Query("key") key: String,
                @Query("language") lang: String): Observable<PlacesResponse>

    @GET(SEARCH_CLOSE_PLACES)
    fun getNearPlaces(@Query("key") key: String,
                      @Query("location") location: String,
                      @Query("type") type: String,
                      @Query("language") lang: String): Observable<PlacesResponse>

    @GET(SEARCH_PLACES + "?type=airport")
    fun getAirportPlaces(@Query("key") key: String,
                         @Query("query") query: String,
                         @Query("location") location: String?,
                         @Query("language") lang: String): Observable<PlacesResponse>

    @FormUrlEncoded
    @POST(AUTOCOMPLETE)
    fun autocomplete(@Field("search") search: String,
                     @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<AutocompleteResponse>

    @FormUrlEncoded
    @POST(GEODECODE)
    fun geodecode(@Field("latlng") lat: String,
                  @Field("language") lang: String = Locale.getDefault().language,
                  @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<AddressResponse>

    @FormUrlEncoded
    @POST(GET_COORDINATES)
    fun getCoordinates(@Field("address") address: String,
                       @Field("language") lang: String = Locale.getDefault().language,
                       @Field(LANGUAGE) language: String = Locale.getDefault().language): Observable<AddressResponse>

    @POST(GET_AIRPORTS)
    fun getAirports(@Query("search") search: String,
                    @Query(LANGUAGE) language: String = Locale.getDefault().language): Observable<AutocompleteResponse>

    @GET(LOGOUT)
    fun logout(@Query(AUTHORIZATION) token: String): Observable<BaseResponse>

    @GET(FORCED_AIRPORT)
    fun checkForcedAirport(): Observable<StartResponse>

    @FormUrlEncoded
    @POST(NOTIFICATION)
    fun addPushToken(@Field(AUTHORIZATION) token: String,
                     @Field("notificationId") pushToken: String,
                     @Field("type") type: Int = 2): Observable<BaseResponse>

    @GET(PUBLIC_ID)
    fun getPublicId(@Query(AUTHORIZATION) token: String): Observable<PublicIdResponse>

    @FormUrlEncoded
    @POST(ADD_CARD)
    fun addCard(@Field(AUTHORIZATION) token: String,
                @Field("card_holder") cardHolder: String,
                @Field("cryptogram") cryptogram: String,
                @Field("card_number") number: String): Observable<CreditCardResponse>

    @GET(CARDS)
    fun getCards(@Query(AUTHORIZATION) token: String): Observable<CreditCardsResponse>

    @DELETE(DELETE_CARD)
    fun deleteCard(@Path("id") cardId: Long,
                   @Query(AUTHORIZATION) token: String): Observable<DeleteCardResponse>

    @FormUrlEncoded
    @POST(DEFAULT_CARD)
    fun defaultCard(@Path("id") cardId: Long,
                    @Field(AUTHORIZATION) token: String): Observable<BaseResponse>
}