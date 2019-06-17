package airport.transfer.sale

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import com.google.gson.Gson
import io.realm.Realm
import airport.transfer.sale.rest.models.response.BaseResponse
import airport.transfer.sale.rest.models.response.model.User
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.ui.view.PaymentDialogView_
import airport.transfer.sale.ui.view.trips.TripView
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

fun Any?.networkError(error: Throwable){
    //Log.e("debugNetworkError", errorText)
    if (error is UnknownHostException || error is SocketTimeoutException) error.printStackTrace()
    else throw error
}

fun Context.makeToast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.makeToast(text: Int){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Throwable.isTokenError() = this is HttpException && this.code() == 422

fun List<AppCompatImageButton>.clearBackground(){
    forEach {
       //it.supportBackgroundTintList = ColorStateList.valueOf(Color.WHITE)
    }
}

class RobotoTextView : TextView {
    private val fontPath = "fonts/roboto.regular.ttf"
    constructor(context: Context) : super(context) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }
}

class RobotoBoldTextView : TextView {
    private val fontPath = "fonts/Roboto-Bold.ttf"
    constructor(context: Context) : super(context) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }
}

class RobotoMediumTextView : TextView {
    private val fontPath = "fonts/Roboto-Medium.ttf"
    constructor(context: Context) : super(context) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }
}

class RobotoLightTextView : TextView {
    private val fontPath = "fonts/Roboto-Light.ttf"
    constructor(context: Context) : super(context) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        typeface = Typeface.createFromAsset(context.assets, fontPath)
    }
}

fun Resources.getDrawableCompat(context: Context, resId: Int): Drawable{
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return getDrawable(resId, context.theme)
    else return getDrawable(resId)
}

fun Context.dip(v: Int) = (resources.displayMetrics.density * v).toInt()

fun List<View>.setActivated(activatedList: List<Boolean>){
    for (i in 0..size - 1) get(i).isActivated = activatedList[i]
}

class DividerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val mPaint: Paint
    private var orientation: Int = 0

    init {
        val dashGap: Int
        val dashLength: Int
        val dashThickness: Int
        val color: Int

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DividerView, 0, 0)

        try {
            dashGap = a.getDimensionPixelSize(R.styleable.DividerView_dashGap, 5)
            dashLength = a.getDimensionPixelSize(R.styleable.DividerView_dashLength, 5)
            dashThickness = a.getDimensionPixelSize(R.styleable.DividerView_dashThickness, 3)
            color = a.getColor(R.styleable.DividerView_color, 0xff000000.toInt())
            orientation = a.getInt(R.styleable.DividerView_orientation, ORIENTATION_HORIZONTAL)
        } finally {
            a.recycle()
        }

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dashThickness.toFloat()
        mPaint.pathEffect = DashPathEffect(floatArrayOf(dashLength.toFloat(), dashGap.toFloat()), 0f)
    }

    override fun onDraw(canvas: Canvas) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            val center = height * .5f
            canvas.drawLine(0f, center, width.toFloat(), center, mPaint)
        } else {
            val center = width * .5f
            canvas.drawLine(center, 0f, center, height.toFloat(), mPaint)
        }
    }

    companion object {
        var ORIENTATION_HORIZONTAL = 0
        var ORIENTATION_VERTICAL = 1
    }
}

class DividerItemDecoration(context: Context, val padding: Int, val isTrip: Boolean) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider) as GradientDrawable

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val left = padding
        val right = parent.width - padding

        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)

            if (!isTrip || (child is TripView && (i == childCount - 1 || parent.getChildAt(i + 1) is TripView) &&
                    child.findViewById<ViewGroup>(R.id.buttonsView).visibility == View.GONE)) {
                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }
}

class AsymDividerItemDecoration(context: Context,
                                private val leftPadding: Int,
                                private val rightPadding: Int,
                                private val isTrip: Boolean,
                                private val removeFirst: Boolean = false) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider) as GradientDrawable

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val left = leftPadding
        val right = parent.width - rightPadding

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            if ((!isTrip && !(removeFirst && i == 0)) || (child is TripView && (i == childCount - 1 || parent.getChildAt(i + 1) is TripView) &&
                    child.findViewById<ViewGroup>(R.id.buttonsView).visibility == View.GONE)) {
                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }
}

fun Int.toUserPrice(context: Context): String {
    return this.toInt().toString().plus(" ").plus(context.getString(R.string.rub))
}

fun String.fromHtmlCompat(): CharSequence{
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    else return Html.fromHtml(this)
}

fun String.boldFirstWord(): CharSequence{
    return "<b>".plus(this.substringBefore(" ")).plus("</b> ").plus(this.substringAfter(" ")).fromHtmlCompat()
}

fun showInfoPage(context: Context, resId: Int): Dialog{
    val dialog = Dialog(context)
    val content = View.inflate(context, resId, null)
    with(dialog) {
        setCancelable(true)
        content.setOnClickListener { dismiss() }
        setContentView(content)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        show()
    }
    return dialog
}

fun showKeyboard(context: Context, view: View){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun hideKeyboard(context: Context, view: View?){
    view?.let {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Context.getColorCompat(resId: Int): Int {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return resources.getColor(resId, theme)
    else return resources.getColor(resId)
}

fun TextView.switch(tv: TextView){
    val text = this.text
    this.text = tv.text
    tv.text = text
}

fun TextView.switchColors(tv: TextView){
    val textColors = this.textColors
    this.setTextColor(tv.textColors)
    tv.setTextColor(textColors)
}

fun TextView.switchWithPlaceholders(tv: TextView, thisPlaceholder: String, targetPlaceholder: String){
    val text = this.text
    if (tv.text != targetPlaceholder) this.text = tv.text
    else this.text = thisPlaceholder
    if (text != thisPlaceholder) tv.text = text
    else tv.text = targetPlaceholder
}

fun Fragment.isLocationPermissionGranted(shouldAsk: Boolean): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
    val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val fine = activity.checkSelfPermission(perms[0]) == PackageManager.PERMISSION_GRANTED
    val coarse = activity.checkSelfPermission(perms[1]) == PackageManager.PERMISSION_GRANTED
    val accessDenied = !fine || !coarse
    if (accessDenied && shouldAsk) {
        requestPermissions(perms, Constants.REQUEST_PERMISSION)
    }
    return !accessDenied
}

fun Long.toServerDate(): String{
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(calendar.time)
}

fun String.fromServerFormat(): Long?{
    if (isEmpty()) return null
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(this).time
}

fun String.fromServerToUserDate(context: Context): String?{
    val millis = this.fromServerFormat() ?: return null
    val calendar = newCalendarInstance(millis)
    if (Locale.getDefault().language == "tr") return SimpleDateFormat("dd.MM", Locale.getDefault()).format(calendar.time)
            .plus(", ")
            .plus(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time))
    val at = context.getString(R.string.at)
    return SimpleDateFormat("dd.MM", Locale.getDefault()).format(calendar.time)
            .plus(" ").plus(at).plus(" ")
            .plus(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time))
}

fun newCalendarInstance(timeInMillis: Long): Calendar{
    val instance = Calendar.getInstance()
    instance.timeInMillis = timeInMillis
    return instance
}

fun EditText.setTextChars(text: String?){
    setText(text)
    text?.length?.let{ setSelection(it) }
}

fun roundLastDigit(time: String): String{
    if (TextUtils.isEmpty(time)) return ""
    val parts = time.split(":")
    val minutes = (parts[1].toDouble() - 0.1) / 5
    val m = Math.floor(minutes + 1).toInt() * 5
    if (m == 60) {
        val hours = Integer.parseInt(parts[0])
        return String.format(Locale.ENGLISH, "%02d:%02d", hours + 1, 0)
    }
    return parts[0] + ":" + String.format(Locale.ENGLISH, "%02d", m)
}

fun Int.getPassengersString(context: Context): String {
    var quantity = this
    if (quantity in 10..19) return context.getString(R.string.passengers5)
    while (quantity > 100) quantity -= 100
    val res: Int
    when (quantity % 10) {
        1 -> res = R.string.passengers1
        2, 3, 4 -> res = R.string.passengers2
        else -> res = R.string.passengers5
    }
    return String.format(Locale.CANADA, "%d %s", this, context.getString(res))
}

fun Int.getLuggageString(context: Context): String {
    var quantity = this
    if (quantity in 10..19) return context.getString(R.string.luggage5)
    while (quantity > 100) quantity -= 100
    val res: Int
    when (quantity % 10) {
        1 -> res = R.string.luggage1
        2, 3, 4 -> res = R.string.luggage2
        else -> res = R.string.luggage5
    }
    return String.format(Locale.CANADA, "%d %s", this, context.getString(res))
}

fun Int.getResourceByTariffId(): Int = when(this){
    1 -> R.drawable.car_comfort
    2 -> R.drawable.car_business
    3 -> R.drawable.car_vip
    4 -> R.drawable.car_minibus
    5 -> R.drawable.car_premium
    6 -> R.drawable.car_bus
    else -> 0
}

fun retrofit2.adapter.rxjava.HttpException.getErrorString(): String? {
    val body = response().errorBody()?.source()?.readString(Charset.defaultCharset()) ?: return null
    val response = Gson().fromJson(body, BaseResponse::class.java)
    return response.message
}

fun getCurrentOrder(realm: Realm?): Order{
    val result = if (realm != null && !realm.isClosed){
        //System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + realm.where(Order::class.java).findAll().joinToString() )
        val existedOrder = realm.where(Order::class.java).equalTo("id", -1L).findFirst()
        if (existedOrder != null) realm.copyFromRealm(existedOrder)
        else Order(-1L)
    } else Order(-1L)
    val forcedAirport = realm?.where(Address::class.java)?.equalTo("isForcedAirport", true)?.findFirst()
    if (forcedAirport != null){
        if (result.destination?.isAirport == true) result.destination = forcedAirport
        else result.delivery = forcedAirport
    }
    return result
}

fun saveCurrentOrder(realm: Realm?, order: Order){
    realm?.let{
        if (!it.isClosed) it.executeTransaction {
            order.id = -1L
            it.copyToRealmOrUpdate(order)
        }
    }
}

fun saveUser(realm: Realm?, user: User?){
    realm?.let{
        if (!it.isClosed) it.executeTransaction {
            if (user == null) it.delete(User::class.java)
            else {
                user.id = -1L
                it.copyToRealmOrUpdate(user)
            }
        }
    }
}

fun getUser(realm: Realm?): User?{
    realm?.let{
        if (!it.isClosed){
            val existedUser = it.where(User::class.java).equalTo("id", -1L).findFirst()
            return if (existedUser != null) it.copyFromRealm(existedUser)
            else null
        }
    }
    return null
}

fun Any?.registerEventBus(){
    val bus = EventBus.getDefault()
    if (!bus.isRegistered(this)) bus.register(this)
}

fun Any?.unregisterEventBus(){
    EventBus.getDefault().unregister(this)
}


fun getHowToUseLink(): String = Constants.HOW_TO_USE_LINK.replace("ru", Locale.getDefault().language)

fun getTermsLink(): String = Constants.TERMS_LINK.replace("ru", Locale.getDefault().language)

fun getPaymentLink(): String = Constants.PAYMENT_LINK.replace("ru", Locale.getDefault().language)

fun Context.showChooseItemDialog(items: List<String>, callback: (position: Int) -> Unit) {
    AlertDialog.Builder(this).setItems(items.toTypedArray(),
            { dialog, which ->
                callback.invoke(which)
            })
            .setNegativeButton(R.string.cancel, null)
            .show()
}

fun Context.showPaymentDialog(context: Context, callback: (position: Int) -> Unit): AlertDialog {
    return AlertDialog.Builder(this)
            .setView(PaymentDialogView_.build(context).bind(callback))
            .setNegativeButton(R.string.cancel, null)
            .show()
}

//Payment utils

private fun createTransaction(price: String): TransactionInfo {
    return TransactionInfo.newBuilder()
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setTotalPrice(price)
            .setCurrencyCode("USD")
            .build()
}

fun createPaymentsClient(activity: Activity): PaymentsClient {
    val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
    return Wallet.getPaymentsClient(activity, walletOptions)
}

fun isReadyToPay(client: PaymentsClient): Task<Boolean> {
    val request = IsReadyToPayRequest.newBuilder()
    for (allowedMethod in listOf(
            WalletConstants.PAYMENT_METHOD_CARD,
            WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)) {
        request.addAllowedPaymentMethod(allowedMethod)
    }
    return client.isReadyToPay(request.build())
}

fun createPaymentDataRequest(price: String, publicId: String): PaymentDataRequest {
    val transactionInfo = createTransaction(price)
    val paramsBuilder = PaymentMethodTokenizationParameters.newBuilder()
            .setPaymentMethodTokenizationType(
                    WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
            .addParameter("gateway", "cloudpayments")
            .addParameter("gatewayMerchantId", publicId)
    return createPaymentDataRequest(transactionInfo, paramsBuilder.build())
}

private fun createPaymentDataRequest(transactionInfo: TransactionInfo, params: PaymentMethodTokenizationParameters): PaymentDataRequest {

    return PaymentDataRequest.newBuilder()
            .setPhoneNumberRequired(false)
            .setEmailRequired(true)
            .setShippingAddressRequired(false)
            .setTransactionInfo(transactionInfo)
            .addAllowedPaymentMethods(listOf(WalletConstants.PAYMENT_METHOD_CARD, WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD))
            .setCardRequirements(
                    CardRequirements.newBuilder()
                            .addAllowedCardNetworks(listOf(WalletConstants.CARD_NETWORK_VISA,
                                    WalletConstants.CARD_NETWORK_MASTERCARD))
                            .setAllowPrepaidCards(true)
                            .setBillingAddressRequired(true)
                            .build())
            .setPaymentMethodTokenizationParameters(params)
            .setUiRequired(true)
            .build()
}

fun isValidEmail(target: CharSequence): Boolean =
        !TextUtils.isEmpty(target.trim()) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()