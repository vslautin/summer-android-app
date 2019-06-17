package airport.transfer.sale.ui.fragment.main

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_trips.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import kotlinx.android.synthetic.main.view_bottom_button_double.*
import airport.transfer.sale.*
import airport.transfer.sale.adapter.BaseTripViewHolder
import airport.transfer.sale.adapter.TripAdapter
import airport.transfer.sale.mvp.model.PushModel
import airport.transfer.sale.mvp.presenter.OrderPresenter
import airport.transfer.sale.mvp.presenter.TripsPresenter
import airport.transfer.sale.mvp.view.OrderView
import airport.transfer.sale.mvp.view.TripsView
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Transaction
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.MainActivity
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.drawer.TransferFragment
import airport.transfer.sale.ui.fragment.order.RateFragment
import airport.transfer.sale.ui.view.trips.TripView
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.greenrobot.eventbus.Subscribe

@EFragment(R.layout.fragment_trips)
open class TripListFragment : BaseFragment(), TripsView, OrderView {

    lateinit var tripAdapter: TripAdapter

    @InjectPresenter
    lateinit var presenter: TripsPresenter

    @InjectPresenter
    lateinit var orderPresenter: OrderPresenter

    var previous: BaseTripViewHolder? = null
    var lastClickedItem: Order? = null

    lateinit var realm: Realm

    private var mIsLastPage = false
    private var mIsLoading = false
    private var mPage = 0

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        val dividerPadding = resources.getDimension(R.dimen.horizontal_padding).toInt()
        historyRecyclerView.addItemDecoration(DividerItemDecoration(context, dividerPadding, true))
        val token = Preferences.getAuthToken(context)
        tripAdapter = TripAdapter()
        if (token == null) {
            adjustTransferButton(true)
        } else {
            addSavedTrips()
            refreshList()
        }
        tripAdapter.setItemClickListener(object : BaseTripViewHolder.ClickListener {

            override fun onItemClicked(position: Int, holder: BaseTripViewHolder, buttonType: BaseTripViewHolder.ButtonType?) {
                val item = tripAdapter.getItem(position)
                lastClickedItem = item
                with(holder.view as TripView){
                    expandDetailView()
                    fulfillInfo(item)
                }
                repeatButton.visibility = View.VISIBLE
                repeatShadow.visibility = View.VISIBLE
                previous?.let { (previous?.view as TripView).collapseDetailView() }
                if (holder == previous) {
                    previous = null
                    repeatButton.visibility = View.GONE
                    repeatShadow.visibility = View.GONE
                } else previous = holder
                if (item.isFavorite == true) rightButton.text = getString(R.string.remove_from_favorites)
                else rightButton.text = getString(R.string.to_favorites)
                val leftButton = repeatButton.findViewById<TextView>(R.id.leftButton) as TextView
                if (item.status != 6) {
                    leftButton.setText(R.string.cancel_vb)
                    leftButton.setOnClickListener {
                        AlertDialog.Builder(context)
                                .setTitle(R.string.order_cancel)
                                .setMessage(R.string.cancel_confirm)
                                .setPositiveButton(R.string.ok, {dialog, which ->
                                    orderPresenter.cancelOrder(Preferences.getAuthToken(context)!!, item.id!!, position)
                                }).setNegativeButton(R.string.cancel, null)
                                .show()
                    }
                } else {
                    leftButton.setText(R.string.repeat)
                    leftButton.setOnClickListener {
                        saveCurrentOrder(realm, item)
                        (parentFragment as TransferFragment).repeatOrder()
                    }
                }
            }
        })
        historyRecyclerView.adapter = tripAdapter
        historyRecyclerView.addOnScrollListener(onScrollListener)
        swipeRefreshLayout.setOnRefreshListener {
            mPage = 0
            refreshList()
            previous = null
            repeatButton.visibility = View.GONE
            repeatShadow.visibility = View.GONE
        }
        rightButton.text = getString(R.string.to_favorites)
        rightButton.setOnClickListener {
            lastClickedItem?.let { presenter.addFavorite(Preferences.getAuthToken(context)!!, it) }
        }
        registerEventBus()
    }

    override fun onCurrentStateReceived(edit: Boolean, back: Boolean) {
    }

    override fun onOrderCreated(order: Order, transaction: Transaction?) {
    }

    override fun onOrderCancelled(positionToRemove: Int) {
        context.makeToast(R.string.order_cancelled)
        val removed = tripAdapter.removeItem(positionToRemove)
        realm.executeTransaction {
            it.where(Order::class.java).equalTo("id", removed.id).findFirst()?.deleteFromRealm()
        }
        repeatButton.visibility = View.GONE
        repeatShadow.visibility = View.GONE
        (parentFragment as TransferFragment).setCurrentOrdersQuantity(tripAdapter.getCurrentOrdersQuantity())
    }

    @Subscribe
    fun onPushReceived(push: PushModel){
        refreshList()
    }

    fun refreshList(){
        waitingForResponse = true
        presenter.getCurrentOrders(Preferences.getAuthToken(context)!!)
        getCompletedOrders()
    }

    fun getCompletedOrders(){
        mIsLoading = true
        presenter.getCompletedOrders(Preferences.getAuthToken(context)!!, Constants.ITEMS_PER_PAGE, mPage * Constants.ITEMS_PER_PAGE)
    }

    private fun addSavedTrips(){
        val current = realm.where(Order::class.java)
                .lessThan("status", 6 as Int)
                .sort("deliveryTime")
                .findAll()
        val completed = realm.where(Order::class.java)
                .equalTo("status", 6 as Int)
                .sort("deliveryTime")
                .findAll()
        if (waitingForResponse) {
            tripAdapter.addAll(completed)
            tripAdapter.addAll(current)
            (parentFragment as TransferFragment).setCurrentOrdersQuantity(current.size)
        }
    }

    private var waitingForResponse: Boolean = false

    override fun onCurrentTripsReceived(trips: List<Order>) {
        if (waitingForResponse) tripAdapter.removeAll()
        else swipeRefreshLayout.isRefreshing = false
        waitingForResponse = false
        tripAdapter.addAll(trips)
        adjustTransferButton(true)//trips.isEmpty()) - asked in Basecamp, no answer
        val savedCurrent = realm.where(Order::class.java).lessThan("status", 6).findAll()
        realm.executeTransaction {
            savedCurrent.forEach { savedTrip ->
                if (!trips.map { it.id }.contains(savedTrip.id) && savedTrip.id != -1L) savedTrip.deleteFromRealm()
            }
            it.copyToRealmOrUpdate(trips)
        }
        (parentFragment as TransferFragment).setCurrentOrdersQuantity(trips.size)
    }

    private fun adjustTransferButton(shouldShow: Boolean) = if (shouldShow) {
        orderButton.visibility = View.VISIBLE
        orderButtonShadow.visibility = View.VISIBLE
        bottomText.setText(R.string.order_transfer_)
        orderButton.setOnClickListener { (parentFragment as TransferFragment).showTransferPage() }
    } else {
        orderButton.visibility = View.GONE
        orderButtonShadow.visibility = View.GONE
    }

    override fun onCompletedTripsReceived(trips: List<Order>) {
        if (waitingForResponse) tripAdapter.removeAll()
        else swipeRefreshLayout.isRefreshing = false
        waitingForResponse = false
        mIsLoading = false
        tripAdapter.addAll(trips)
        mIsLastPage = trips.size < Constants.ITEMS_PER_PAGE
        val savedCompleted = realm.where(Order::class.java).equalTo("status", 6 as Int).findAll()
        realm.executeTransaction {
            savedCompleted.forEach { savedTrip ->
                if (!trips.map { it.id }.contains(savedTrip.id) && savedTrip.id != -1L) savedTrip.deleteFromRealm()
            }
            it.copyToRealmOrUpdate(trips)
        }
        trips.maxBy{ it.updatedAt!! }?.let { lastOrder ->
            if (lastOrder.isReviewProposed == false) {
                (parentFragment as TransferFragment).showFragment(RateFragment.newInstance(Gson().toJson(lastOrder)))
            }
        }
    }

    private var onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView!!.layoutManager as LinearLayoutManager
            val total = manager.itemCount
            val visible = manager.childCount
            val firstVisible = manager.findFirstVisibleItemPosition()
            val pageNumber = total / Constants.ITEMS_PER_PAGE
            if (!mIsLastPage && !mIsLoading && visible + firstVisible >= total && firstVisible > 0 &&
                    total >= Constants.ITEMS_PER_PAGE) {
                mPage = pageNumber
                getCompletedOrders()
            }
        }
    }

    override fun onFavoriteTripsReceived(trips: List<Order>) {
    }

    override fun onFavoriteAdded(order: Order) {
        if (order.isFavorite == true) context.makeToast(R.string.added_to_favorites)
        else context.makeToast(R.string.removed_from_favorites)
        tripAdapter.items.find { it.id == order.id }?.isFavorite = order.isFavorite
        repeatButton.visibility = View.GONE
        repeatShadow.visibility = View.GONE
        previous?.let { (previous?.view as TripView).collapseDetailView() }
        previous = null
        (parentFragment as TransferFragment).refreshFavorites()
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        //activity.makeToast(R.string.auth_error)
        (activity as? MainActivity)?.onLogout()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        unregisterEventBus()
    }
}
