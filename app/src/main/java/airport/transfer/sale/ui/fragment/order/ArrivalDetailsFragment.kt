package airport.transfer.sale.ui.fragment.order

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.ListPopupWindow
import android.text.TextUtils
import android.widget.ArrayAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_arrival_details.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.Constants.Companion.DOZEN_HOURS
import airport.transfer.sale.mvp.presenter.AirlinesPresenter
import airport.transfer.sale.mvp.view.AirlinesView
import airport.transfer.sale.rest.models.response.model.Airline
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import java.text.SimpleDateFormat
import java.util.*

@EFragment(R.layout.fragment_arrival_details)
open class ArrivalDetailsFragment : BaseFragment(), AirlinesView {

    @InjectPresenter
    lateinit var presenter: AirlinesPresenter

    lateinit var airlinesList: ListPopupWindow
    lateinit var airlinesAdapter: ArrayAdapter<Airline>
    var shouldSkipNextQuery = false
    var lastPickedTime: Pair<Int, Int>? = null
    var lastPickedDate: Triple<Int, Int, Int>? = null
    var lastPickedAirlineId: Int? = null

    private var realm: Realm? = null

    companion object {
        const val KEY = "key"
        fun newInstance(isDestination: Boolean, previousText: String): ArrivalDetailsFragment {
            val args = Bundle()
            args.putBoolean(KEY, isDestination)
            args.putString(Constants.EXTRA_TEXT, previousText)
            val fragment = ArrivalDetailsFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        bottomText.text = getString(R.string.save)
        val isDestination = arguments.getBoolean(KEY)
        if (isDestination) timeLabel.setText(R.string.flight_time_destination)

        /*flightCompany.setTextChars(Preferences.getCurrentOrder(context).airline?.name)
        val airlinesObserver = RxTextView.textChanges(flightCompany)
        flightCompany.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) presenter.getAirlines(null) }
        airlinesAdapter = ArrayAdapter<Airline>(context, R.layout.view_dropdown)
        airlinesList = ListPopupWindow(context)
        airlinesList.setOnItemClickListener { parent, view, position, id ->
            shouldSkipNextQuery = true
            val airline = airlinesAdapter.getItem(position)
            lastPickedAirlineId = airline.id
            flightCompany.setTextChars(airline.name)
            applyIata(airline.iata)
            airlinesList.dismiss()
        }
        with(airlinesList) {
            anchorView = flightCompanyLayout
            setAdapter(airlinesAdapter)
        }
        airlinesObserver.skip(1)
                .debounce(600, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(airlinesSubscriber)*/
        acceptButton.setOnClickListener {
            if (isAllFieldsValid()) {
                saveOrderDetails()
                val intent = Intent()
                intent.putExtra(Constants.EXTRA_FLIGHT, flightNumber.text.toString())
                activity.intent.getStringExtra(Constants.EXTRA_TITLE)?.let {
                    intent.putExtra(Constants.EXTRA_TITLE, it)
                }
                activity.intent.getStringExtra(Constants.EXTRA_SUBTITLE)?.let {
                    intent.putExtra(Constants.EXTRA_SUBTITLE, it)
                }
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
            }
        }
        flightTimeLayout.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(context, { v, hour, minute ->
                val time = roundLastDigit(String.format(Locale.ENGLISH, "%02d:%02d", hour, minute))
                val hourMinutes = time.split(":")
                lastPickedTime = Pair(hourMinutes[0].toInt(), hourMinutes[1].toInt())
                flightTime.setText(time)
                val dialog = DatePickerDialog(context, {view, year, month, day ->
                    lastPickedDate = Triple(day, month, year)
                    flightTime.setText(flightTime.text.toString().plus(", ")
                            .plus(getUserFriendlyDate(day, month)))
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                dialog.datePicker.minDate = now.timeInMillis - DOZEN_HOURS
                dialog.show()
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }
        /*airlinesIcon.setOnClickListener {
            shouldSkipNextQuery = true
            flightCompany.setText("")
            airlinesList.dismiss()
        }*/
        arguments.getString(Constants.EXTRA_TEXT)?.let{
            val placeholder = getString(R.string.flight_hint)
            if (it != placeholder) {
                flightNumber.setText(it)
                flightNumber.setSelection(it.length)
            }
        }
    }

    private fun getUserFriendlyDate(day: Int, month: Int): String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        return SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date(calendar.timeInMillis))
    }

    fun applyIata(iata: String){
        val flight = flightNumber.text.toString()
        if (TextUtils.isEmpty(flight) || flight.length <= 2) {
            if (flight.matches("[0-9]+".toRegex())) flightNumber.setTextChars(iata.plus(flight))
            else flightNumber.setTextChars(iata)
        } else {
            val firstTwo = flight.substring(0, 2)
            if (firstTwo != iata) {
                if (firstTwo.matches("[0-9]+".toRegex())) flightNumber.setTextChars(iata.plus(flight))
                else flightNumber.setTextChars(iata.plus(flight.substring(2)))
            }
        }
    }

    val airlinesSubscriber = { text: CharSequence ->
        if (text.isNotEmpty()) {
            if (shouldSkipNextQuery) shouldSkipNextQuery = false
            else presenter.getAirlines(text.toString())
        } else {
            airlinesList.dismiss()
            lastPickedAirlineId = null
        }
        val cross = if (text.isNotEmpty()) resources.getDrawableCompat(context, R.drawable.cross)
        else resources.getDrawableCompat(context, R.drawable.chevron)
        airlinesIcon.setImageDrawable(cross)
    }

    override fun onAirlinesReceived(airlines: List<Airline>) {
        if (airlines.isEmpty()) airlinesList.dismiss()
        else {
            airlinesAdapter.clear()
            airlinesAdapter.addAll(airlines)
            if (!airlinesList.isShowing) {
                airlinesList.show()
                flightCompany.requestFocus()
            }
        }
    }

    private fun isAllFieldsValid(): Boolean {
        when {
            TextUtils.isEmpty(flightNumber.text) -> {
                context.makeToast(R.string.fill_flight_number)
                return false
            }
            /*TextUtils.isEmpty(flightCompany.text) &&*/
            flightNumber.text.length <= 2 || !flightNumber.text.substring(0,1).matches("^[a-zA-Z]*$".toRegex()) ||
                    !flightNumber.text.matches("[a-zA-Z]+[0-9]+[a-zA-Z0-9]*|[0-9]+[a-zA-Z][a-zA-Z0-9]*".toRegex()) -> {
                context.makeToast(R.string.fill_flight_number_letters)
                return false
            }
            /*TextUtils.isEmpty(flightTime.text) -> {
                context.makeToast(R.string.fill_flight_time)
                return false
            }*/
        }
        return true
    }

    private fun saveOrderDetails() {
        val order = getCurrentOrder(realm)
        order.flightNumber = flightNumber.text.toString()
        /*val time = order.deliveryTime?.fromServerFormat() ?: System.currentTimeMillis()
        val deliveryCalendar = newCalendarInstance(time)
        lastPickedTime?.let {
            deliveryCalendar.set(Calendar.HOUR_OF_DAY, it.first)
            deliveryCalendar.set(Calendar.MINUTE, it.second)
        }
        lastPickedDate?.let {
            deliveryCalendar.set(Calendar.DAY_OF_MONTH, it.first)
            deliveryCalendar.set(Calendar.MONTH, it.second)
            deliveryCalendar.set(Calendar.YEAR, it.third)
        }
        order.deliveryTime = deliveryCalendar.timeInMillis.toServerDate()*/
        saveCurrentOrder(realm, order)
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }
}