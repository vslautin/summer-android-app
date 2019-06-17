package airport.transfer.sale.ui.fragment.main

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_favorite_trips.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.adapter.*
import airport.transfer.sale.mvp.presenter.TripsPresenter
import airport.transfer.sale.mvp.view.TripsView
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.MainActivity
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.drawer.TransferFragment
import airport.transfer.sale.ui.view.trips.TripView
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment


@EFragment(R.layout.fragment_favorite_trips)
open class FavoriteTripsFragment : BaseFragment(), TripsView{

    lateinit var favoritesAdapter: FavoriteTripsAdapter

    @InjectPresenter
    lateinit var presenter: TripsPresenter

    lateinit var realm: Realm

    private var mIsLastPage = false
    private var mIsLoading = false
    private var mPage = 0

    @AfterViews
    fun ready(){
        favoriteTripsRecyclerView.layoutManager = LinearLayoutManager(context)
        val dividerPadding = resources.getDimension(R.dimen.horizontal_padding).toInt()
        favoriteTripsRecyclerView.addItemDecoration(DividerItemDecoration(context, dividerPadding, true))
        favoritesAdapter = FavoriteTripsAdapter()
        favoritesAdapter.setItemClickListener(object : BaseTripViewHolder.ClickListener {
            var previous: BaseTripViewHolder? = null

            override fun onItemClicked(position: Int, holder: BaseTripViewHolder, buttonType: BaseTripViewHolder.ButtonType?) {
                (holder.view as TripView).expandButtonsView()
                previous?.let { (previous?.view as TripView).collapseButtonsView() }
                if (holder == previous) previous = null
                else previous = holder
                when (buttonType) {
                    BaseTripViewHolder.ButtonType.REPEAT -> {
                        val order = favoritesAdapter.getItem(position)
                        order.flightNumber = null
                        order.deliveryTime = ""
                        saveCurrentOrder(realm, order)
                        (parentFragment as TransferFragment).repeatOrder()
                    }
                    BaseTripViewHolder.ButtonType.DELETE -> {
                        presenter.addFavorite(Preferences.getAuthToken(context)!!, favoritesAdapter.getItem(position))
                        favoritesAdapter.removeItem(position)
                    }
                }
            }
        })
        favoriteTripsRecyclerView.adapter = favoritesAdapter
        favoriteTripsRecyclerView.addOnScrollListener(onScrollListener)
        realm = Realm.getDefaultInstance()
        favoritesAdapter.addAll(realm.where(Order::class.java).equalTo("isFavorite", true).findAll())
        refreshList()
        swipeRefreshLayout.setOnRefreshListener {
            mPage = 0
            refreshList()
        }
    }

    fun refreshList(){
        if (Preferences.getAuthToken(context) != null) {
            getFavorites()
        } else adjustTransferButton(true)
    }

    private fun getFavorites(){
        mIsLoading = true
        presenter.getFavoriteOrders(Preferences.getAuthToken(context)!!, Constants.ITEMS_PER_PAGE, mPage * Constants.ITEMS_PER_PAGE)
    }

    override fun onCurrentTripsReceived(trips: List<Order>) {
    }

    override fun onCompletedTripsReceived(trips: List<Order>) {
    }

    override fun onFavoriteTripsReceived(trips: List<Order>) {
        if (mPage == 0) favoritesAdapter.replaceAll(trips)
        else favoritesAdapter.addAll(trips)
        mIsLastPage = trips.size < Constants.ITEMS_PER_PAGE
        mIsLoading = false
        swipeRefreshLayout.isRefreshing = false
        realm.executeTransaction {
            it.copyToRealmOrUpdate(trips)
        }
        adjustTransferButton(true)//trips.isEmpty()) - asked in Basecamp, no answer
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
                getFavorites()
            }
        }
    }

    private fun adjustTransferButton(shouldShow: Boolean){
        if (shouldShow) {
            orderButton.visibility = View.VISIBLE
            orderButtonShadow.visibility = View.VISIBLE
            bottomText.setText(R.string.order_transfer_)
            orderButton.setOnClickListener { (parentFragment as TransferFragment).showTransferPage() }
        } else {
            orderButton.visibility = View.GONE
            orderButtonShadow.visibility = View.GONE
        }
    }

    override fun onFavoriteAdded(order: Order) {
        if (order.isFavorite == true) context.makeToast(R.string.added_to_favorites)
        else context.makeToast(R.string.removed_from_favorites)
        (parentFragment as TransferFragment).refreshTrips()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        //activity.makeToast(R.string.auth_error)
        (activity as? MainActivity)?.onLogout()
    }
}