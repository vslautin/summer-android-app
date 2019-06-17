package airport.transfer.sale.storage

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import io.realm.Realm
import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.MessagesResponse
import airport.transfer.sale.rest.models.response.model.Message
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.saveCurrentOrder
import airport.transfer.sale.saveUser

object Preferences {
    private const val TOKEN = "token_a"
    private const val PUSH = "token_p"
    private const val BEARER = "Bearer "
    private const val EXPLAIN = "switch explanation"
    private const val PLANS = "plans"
    private const val ORDER = "order"
    private const val USER = "user"
    private const val CURRENT = "current"
    private const val COMPLETED = "completed"
    private const val FAVORITES = "favorite"
    private const val MESSAGES = "body"
    private const val LAT = "lat"
    private const val LON = "lon"
    private const val ASK = "ask"
    private const val GPAY = "gpay"
    private const val G_CARD = "gcard"
    private const val G_TOKEN = "g_token"

    fun saveToken(stamp: String?, context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(TOKEN, stamp)
                .apply()
    }

    fun getAuthToken(context: Context): String? {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(TOKEN, null)
    }

    fun savePushToken(context: Context, stamp: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PUSH, stamp)
                .apply()
    }

    fun getPushToken(context: Context): String? {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PUSH, null)
    }

    fun setTutorShown(context: Context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(EXPLAIN, true)
                .apply()
    }

    fun hasTutorShown(context: Context) = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(EXPLAIN, false)

    private fun deleteUser(context: Context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(USER)
                .apply()
    }

    fun getMessages(context: Context): MutableSet<Message>{
        val messagesJson = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(MESSAGES, null) ?: return HashSet<Message>()
        return Gson().fromJson(messagesJson, MessagesResponse::class.java).messages.toHashSet()
    }

    fun addMessages(messages: List<Message>, context: Context){
        val savedMessages = getMessages(context)
        savedMessages.addAll(messages)
        val messagesResponse = MessagesResponse(Constants.Status.SUCCESS, 200, null, savedMessages.toList())
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(MESSAGES, Gson().toJson(messagesResponse))
                .apply()
    }

    private fun getKey(type: Constants.OrderTense): String{
        return when(type){
            Constants.OrderTense.FAVORITE -> FAVORITES
            Constants.OrderTense.CURRENT -> CURRENT
            Constants.OrderTense.COMPLETED -> COMPLETED
        }
    }

    fun logout(context: Context, realm: Realm?){
        saveToken(null, context)
        saveUser(realm, null)
        saveCurrentOrder(realm, Order())
    }

    fun saveLocation(lat: Double, lon: Double, context: Context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(LAT, lat.toFloat())
                .putFloat(LON, lon.toFloat())
                .apply()
    }

    fun getLocation(context: Context): Pair<Float, Float>?{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lat = prefs.getFloat(LAT, -1f)
        val lon = prefs.getFloat(LON, -1f)
        if (lat == -1f || lon == -1f) return null
        return Pair(lat, lon)
    }

    fun shouldAskLocationPermission(context: Context): Boolean{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(ASK, true)
    }

    fun setLocationPermissionAsked(context: Context){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean(ASK, false).apply()
    }

    fun saveIsLastPaymentGooglePay(context: Context, gPay: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(GPAY, gPay)
                .apply()
    }

    fun getIsLastPaymentGooglePay(context: Context): Boolean {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(GPAY, false)
    }

    fun saveGooglePayInfo(context: Context, info: String){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(G_CARD, info)
                .apply()
    }

    fun getGooglePayInfo(context: Context): String? =
            PreferenceManager.getDefaultSharedPreferences(context).getString(G_CARD, null)


    fun saveGooglePayToken(context: Context, info: String){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(G_TOKEN, info)
                .apply()
    }

    fun getGooglePayToken(context: Context): String? =
            PreferenceManager.getDefaultSharedPreferences(context).getString(G_TOKEN, null)
}