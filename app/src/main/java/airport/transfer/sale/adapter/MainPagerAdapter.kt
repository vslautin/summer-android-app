package airport.transfer.sale.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import airport.transfer.sale.ui.fragment.main.*
import airport.transfer.sale.ui.fragment.main.TripListFragment_

class MainPagerAdapter(fm: FragmentManager, val titles: List<CharSequence>) : FragmentPagerAdapter(fm){

    lateinit var favoritesFragment: FavoriteTripsFragment
    lateinit var tripsFragment: TripListFragment
    lateinit var orderFragment: OrderFragment

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                orderFragment = OrderFragment_()
                return orderFragment
            }
            1 -> {
                tripsFragment = TripListFragment_()
                return tripsFragment
            }
            else -> {
                favoritesFragment = FavoriteTripsFragment_()
                return favoritesFragment
            }
        }
    }

    fun refreshFavoritesList(){
        favoritesFragment.refreshList()
    }

    fun refreshTripsList(){
        tripsFragment.refreshList()
    }

    fun refreshOrderTab(shouldClear: Boolean, back: Boolean){
        orderFragment.onCurrentStateReceived(!shouldClear, back)
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}