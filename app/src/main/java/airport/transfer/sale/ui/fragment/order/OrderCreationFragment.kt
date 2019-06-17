package airport.transfer.sale.ui.fragment.order

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_order_creation.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.mvp.model.CarPriceModel
import airport.transfer.sale.mvp.presenter.OrderPresenter
import airport.transfer.sale.mvp.presenter.PricePresenter
import airport.transfer.sale.mvp.view.OrderView
import airport.transfer.sale.mvp.view.PriceView
import airport.transfer.sale.rest.models.response.model.Plan
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Transaction
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.*
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.drawer.TransferFragment
import airport.transfer.sale.ui.fragment.order.param.PlansFragment
import airport.transfer.sale.ui.fragment.order.param.PlansFragment_
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import java.text.SimpleDateFormat
import java.util.*

@EFragment(R.layout.fragment_order_creation)
open class OrderCreationFragment : BaseFragment(), OrderView, PriceView {

    companion object {
        const val EDIT_KEY = "key"

        fun newInstance(edit: Boolean): OrderCreationFragment {
            val args = Bundle()
            args.putBoolean(EDIT_KEY, edit)
            val fragment = OrderCreationFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    private var isDestinationAirport = false

    @InjectPresenter
    lateinit var presenter: OrderPresenter

    @InjectPresenter
    lateinit var pricePresenter: PricePresenter

    lateinit var plansFragment: PlansFragment
    private var realm: Realm? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        val savedOrder = getCurrentOrder(realm)
        isDestinationAirport = true == savedOrder.destination?.isAirport
        setOrderDateText()
        bottomText.setText(R.string.choose_tariff)
        val edit = arguments.getBoolean(EDIT_KEY)
        setupLastOrderPoints(!edit)
        sendButton.setOnClickListener {
            val order = getCurrentOrder(realm)
            if (isOrderValid(order)) {
                val token = Preferences.getAuthToken(context)
                if (token == null) startActivityForResult(Intent(context, RegistrationActivity_::class.java), Constants.REQUEST_AUTHORIZATION)
                else showPlansActivity()/*showConfirmFragment()if (edit && !back) presenter.editOrder(token, order)
                else presenter.createOrder(token, order)*/
            }
        }
        if (edit) {
            switchOriginDestination()
            saveSwitchOriginDestination()
            val order = getCurrentOrder(realm)
            order.flightNumber = null
            saveCurrentOrder(realm, order)
        }
        if (!Preferences.hasTutorShown(context)) (parentFragment.parentFragment as TransferFragment).showTutor()
        setupClickListeners()
    }

    /*override fun onPlansReceived(plans: PlansResponse) {
        Preferences.savePlans(plans, context)
        initPlansDialog(plans.plans)
        adjustPlan(getCurrentOrder(realm))
    }*/

    override fun onPriceReceived(price: Int) {
        //leftButton.text = price.toUserPrice(context)
        val savedOrder = getCurrentOrder(realm)
        //savedOrder.cost = price
        saveCurrentOrder(realm, savedOrder)
    }

    override fun onCurrentStateReceived(edit: Boolean, back: Boolean) {
    }

    override fun onOrderCreated(order: Order, transaction: Transaction?) {
        saveCurrentOrder(realm, order)
        //(parentFragment.parentFragment as TransferFragment).showFragment(AcceptedFragment.getInstance(!arguments.getBoolean(EDIT_KEY)))
    }

    override fun onOrderCancelled(positionToRemove: Int) {
    }

    private fun setOrderDateText() {
        var orderMillis = getCurrentOrder(realm).deliveryTime?.fromServerFormat()
        if (orderMillis == null) {
            val calendarInstance = Calendar.getInstance()
            calendarInstance.add(Calendar.HOUR_OF_DAY, 24)
            calendarInstance.add(Calendar.MINUTE, 5)
            orderMillis = calendarInstance.timeInMillis
            saveDate(calendarInstance)
            saveTime(calendarInstance)
        }
        val date = Date(orderMillis)
        dateText.text = SimpleDateFormat("d", Locale.ENGLISH).format(date)
        monthText.text = SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
        timeText.text = roundLastDigit(SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date))
    }

    private var childSeats = 0

    private fun setupClickListeners() {
        originLayout.setOnClickListener { startAddressActivity(Constants.REQUEST_ARRIVAL, originTitle.text.toString()) }
        destinationLayout.setOnClickListener { startAddressActivity(Constants.REQUEST_DESTINATION, destinationTitle.text.toString()) }
        flightLayout.setOnClickListener {
            startArrivalDetailsActivity(
                    if (isDestinationAirport) Constants.REQUEST_DESTINATION_FLIGHT
                    else Constants.REQUEST_ARRIVAL_FLIGHT)
        }
        plusChair.setOnClickListener { onChildSeatsChanged(++childSeats) }
        minusChair.setOnClickListener { onChildSeatsChanged(--childSeats) }
        rotateButton.setOnClickListener {
            switchOriginDestination()
            saveSwitchOriginDestination()
        }
        adjustPickers()
        deleteOrigin.setOnClickListener {
            originTitle.text = ""
            val order = getCurrentOrder(realm)
            order.delivery = null
            saveCurrentOrder(realm, order)
        }
        deleteDestination.setOnClickListener {
            destinationTitle.text = ""
            val order = getCurrentOrder(realm)
            order.destination = null
            saveCurrentOrder(realm, order)
        }
    }

    private fun onChildSeatsChanged(seats: Int) {
        if (childSeats < 0) childSeats++
        else {
            if (childSeats > 2) childSeats--
            else childSeatsText.text = seats.toString()
        }
        val order = getCurrentOrder(realm)
        order.babyChair = childSeats
        saveCurrentOrder(realm, order)
    }

    private fun startAddressActivity(request: Int, text: String) {
        val intent = Intent(context, AddressActivity_::class.java)
        with(intent) {
            putExtra(Constants.EXTRA_KEY, request)
            putExtra(Constants.EXTRA_FLAG, isDestinationAirport)
            putExtra(Constants.EXTRA_AIRPORT, getCurrentOrderAirport())
            putExtra(Constants.EXTRA_TEXT, text)
        }
        startActivityForResult(intent, request)
    }

    private fun getCurrentOrderAirport(): String? {
        val order = getCurrentOrder(realm)
        if (order.destination?.isAirport == true) {
            if (order.destination?.lat != 200f && order.destination?.lat != null) {
                return "${order.destination?.lat},${order.destination?.lng}"
            }
        } else if (order.delivery?.isAirport == true) {
            if (order.delivery?.lat != 200f && order.delivery?.lat != null) {
                return "${order.delivery?.lat},${order.delivery?.lng}"
            }
        }
        return null
    }

    private fun startArrivalDetailsActivity(request: Int) {
        if (!isDestinationAirport && originTitle.text.isEmpty()) {
            startAddressActivity(Constants.REQUEST_ARRIVAL, "")
            return
        }
        if (isDestinationAirport && destinationTitle.text.isEmpty()) {
            startAddressActivity(Constants.REQUEST_DESTINATION, "")
            return
        }
        val intent = Intent(context, ArrivalDetailsActivity_::class.java)
        intent.putExtra(Constants.EXTRA_KEY, request)
        intent.putExtra(Constants.EXTRA_TEXT, flightNumber.text.toString())
        startActivityForResult(intent, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_ARRIVAL -> {
                    originTitle.text = data?.getStringExtra(Constants.EXTRA_TITLE)
                    originSubtitle.text = data?.getStringExtra(Constants.EXTRA_SUBTITLE)
                    originHint.text = ""
                }
                Constants.REQUEST_DESTINATION -> {
                    destinationTitle.text = data?.getStringExtra(Constants.EXTRA_TITLE)
                    destinationSubtitle.text = data?.getStringExtra(Constants.EXTRA_SUBTITLE)
                    destinationHint.text = ""
                }
                Constants.REQUEST_ACCEPTED -> {
                    val backTransferNeed = data?.getBooleanExtra(Constants.EXTRA_KEY, false)
                            ?: false
                    (parentFragment.parentFragment as TransferFragment).refreshAll(true, backTransferNeed)
                    return
                }
                Constants.REQUEST_AUTHORIZATION -> {
                    sendButton.performClick()
                    (activity as MainActivity).setupDrawerHeader()
                    (activity as MainActivity).getCards()
                    return
                }
                Constants.REQUEST_PLAN, Constants.REQUEST_PAYMENT, Constants.REQUEST_PROFILE -> {
                    showConfirmFragment()
                }
            }
            data?.getStringExtra(Constants.EXTRA_FLIGHT)?.let { flightNumber.text = it }
            setOrderDateText()
            //checkPrice()
        } else if (resultCode == Activity.RESULT_FIRST_USER) {
            if (requestCode == Constants.REQUEST_ACCEPTED) {
                onTokenError()
            }
        }
    }

    private fun showConfirmFragment() {
        val user = getUser(realm)
        if (user?.lastName.isNullOrEmpty()) {
            Toast.makeText(context, R.string.should_add_last_name, Toast.LENGTH_LONG).show()
            startActivityForResult(Intent(context, PersonalInfoActivity_::class.java), Constants.REQUEST_PROFILE)
        } else {
            val intent = Intent(context, AcceptedActivity_::class.java)
            intent.putExtra(Constants.EXTRA_FLAG, !arguments.getBoolean(OrderCreationFragment.EDIT_KEY))
            startActivityForResult(intent, Constants.REQUEST_ACCEPTED)
        }
    }

    private fun showPlansActivity() {
        startActivityForResult(Intent(context, ChoosePlanActivity_::class.java), Constants.REQUEST_PLAN)
    }

    private fun switchOriginDestination() {
        if (originTitle.text.isEmpty()) {
            if (isDestinationAirport) destinationHint.setText(R.string.destination_hint)
            else destinationHint.setText(R.string.choose_airport)
        } else destinationHint.text = ""
        if (destinationTitle.text.isEmpty()) {
            if (isDestinationAirport) originHint.setText(R.string.choose_airport)
            else originHint.setText(R.string.origin_hint)
        } else originHint.text = ""
        originTitle.switch(destinationTitle)
        originSubtitle.switch(destinationSubtitle)
        switchFlightUi(false)
    }

    private fun switchFlightUi(fromStart: Boolean) {
        val isDestination = if (fromStart) !isDestinationAirport else isDestinationAirport
        if (isDestination) {
            val v = flightLayout
            labelsLayout.removeView(v)
            labelsLayout.addView(v, 1)
            flightSeparator.visibility = View.VISIBLE
            destinationSeparator.visibility = View.GONE
        } else {
            val v = destinationLayout
            labelsLayout.removeView(v)
            labelsLayout.addView(v, 1)
            flightSeparator.visibility = View.GONE
            destinationSeparator.visibility = View.VISIBLE
        }
    }

    private fun saveSwitchOriginDestination() {
        val order = getCurrentOrder(realm)
        val origin = order.delivery
        isDestinationAirport = !isDestinationAirport
        order.delivery = order.destination
        order.destination = origin
        saveCurrentOrder(realm, order)
    }

    private fun isOrderValid(order: Order): Boolean {
        when {
            order.delivery == null || TextUtils.isEmpty(order.delivery?.title) -> {
                context.makeToast(R.string.fill_origin); return false
            }
            order.destination == null || TextUtils.isEmpty(order.destination?.title) -> {
                context.makeToast(R.string.fill_destination); return false
            }
            TextUtils.isEmpty(order.flightNumber) -> {
                context.makeToast(R.string.fill_flight_number); return false
            }
            order.deliveryTime == null || order.deliveryTime!!.fromServerFormat() == null -> {
                context.makeToast(R.string.fill_flight_time); return false
            }
            order.deliveryTime != null &&
                    order.deliveryTime!!.fromServerFormat() != null &&
                    order.deliveryTime!!.fromServerFormat()!! <= System.currentTimeMillis() + 2 * Constants.DOZEN_HOURS -> {
                context.makeToast(R.string.fill_flight_time_correctly); return false
            }
        /*order.plan?.id == null -> {
            context.makeToast(R.string.fill_plan); return false
        }*/
        }
        return true
    }

    private fun setupLastOrderPoints(setFlightNumber: Boolean) {
        val order = getCurrentOrder(realm)
        order.delivery?.let {
            if (it.title.isNotEmpty()) {
                originTitle.text = it.title
                originSubtitle.text = it.subtitle
                originHint.text = ""
            }
            if (order.destination == null || TextUtils.isEmpty(order.destination?.title)) {
                if (it.isAirport == true) destinationHint.setText(R.string.destination_hint)
                else destinationHint.setText(R.string.choose_airport)
            }
        }
        order.destination?.let {
            if (it.title.isNotEmpty()) {
                destinationTitle.text = it.title
                destinationSubtitle.text = it.subtitle
                destinationHint.text = ""
            }
            if (order.delivery == null || TextUtils.isEmpty(order.delivery?.title)) {
                if (it.isAirport == true) originHint.setText(R.string.origin_hint)
                else originHint.setText(R.string.choose_airport)
            }
        }
        if (setFlightNumber) order.flightNumber?.let { if (it.isNotEmpty()) flightNumber.text = it }
        order.babyChair?.let {
            childSeats = it
            childSeatsText.text = it.toString()
        }
        if (order.delivery?.isAirport == false || order.destination?.isAirport == true) {
            switchFlightUi(true)
        }
    }

    private fun adjustPickers() {
        val now = Calendar.getInstance()
        dateLayout.setOnClickListener {
            val dialog = DatePickerDialog(context, { v, year, month, day ->
                val arrival = Calendar.getInstance()
                arrival.set(year, month, day)
                dateText.text = day.toString()
                monthText.text = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date(arrival.timeInMillis))
                saveDate(newCalendarInstance(arrival.timeInMillis))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = now.timeInMillis + 24 * 60 * 60 * 1000
            val maxDateCalendar = Calendar.getInstance()
            maxDateCalendar.add(Calendar.YEAR, 1)
            dialog.datePicker.maxDate = maxDateCalendar.timeInMillis
            dialog.show()
        }
        timeLayout.setOnClickListener {
            TimePickerDialog(context, { v, hour, minute ->
                val arrival = Calendar.getInstance()
                arrival.set(Calendar.HOUR_OF_DAY, hour)
                arrival.set(Calendar.MINUTE, minute)
                val time = roundLastDigit(SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(arrival.timeInMillis)))
                timeText.text = time
                saveTime(newCalendarInstance(arrival.timeInMillis))
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }
        dateTimeLabel.setOnClickListener { showInfoPage(context, R.layout.view_info_time) }
    }

    private fun saveDate(date: Calendar) {
        val order = getCurrentOrder(realm)
        val serverDateMillis = order.deliveryTime?.fromServerFormat() ?: System.currentTimeMillis()
        val serverCalendar = Calendar.getInstance()
        with(serverCalendar) {
            timeInMillis = serverDateMillis
            set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
        }
        order.deliveryTime = serverCalendar.timeInMillis.toServerDate()
        saveCurrentOrder(realm, order)
    }

    private fun saveTime(date: Calendar) {
        val order = getCurrentOrder(realm)
        val serverDateMillis = order.deliveryTime?.fromServerFormat() ?: System.currentTimeMillis()
        val serverCalendar = Calendar.getInstance()
        with(serverCalendar) {
            timeInMillis = serverDateMillis
            set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, date.get(Calendar.MINUTE))
        }
        order.deliveryTime = serverCalendar.timeInMillis.toServerDate()
        saveCurrentOrder(realm, order)
    }

    private fun initPlansDialog(plans: List<Plan>?) {
        plansFragment = PlansFragment_.builder().build()
        plansFragment.setChoosePlanListener(object : PlansFragment.ChoosePlanListener {
            override fun onPlanChosen(model: CarPriceModel) {
                val plan = plans?.find { it.id == model.id }
                val savedOrder = getCurrentOrder(realm)
                savedOrder.plan = plan
                saveCurrentOrder(realm, savedOrder)
                plansFragment.dialog.dismiss()
                plans?.let { plansFragment.setPlans(it.map { CarPriceModel(it.id, it.title, it.about, it.id == plan?.id) }) }
                //checkPrice()
            }
        })
        val savedPlanId = getCurrentOrder(realm).plan?.id
        plans?.let { plansFragment.setPlans(it.map { CarPriceModel(it.id, it.title, it.about, it.id == savedPlanId) }) }
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        (activity as? MainActivity)?.onLogout()
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }

}