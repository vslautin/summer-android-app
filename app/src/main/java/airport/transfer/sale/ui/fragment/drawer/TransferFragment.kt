package airport.transfer.sale.ui.fragment.drawer

import airport.transfer.sale.Constants
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_transfer.*
import airport.transfer.sale.R
import airport.transfer.sale.adapter.MainPagerAdapter
import airport.transfer.sale.makeToast
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.UseSwitchFragment_
import airport.transfer.sale.ui.view.TasksTabView
import airport.transfer.sale.ui.view.TasksTabView_
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_transfer)
open class TransferFragment : BaseFragment() {

    lateinit var pagerAdapter: MainPagerAdapter

    @AfterViews
    fun ready(){
        val tabTitles = arrayListOf(R.string.order, R.string.trips, R.string.favorites).map { getString(it) }
        pagerAdapter = MainPagerAdapter(childFragmentManager, tabTitles)
        mainViewPager.adapter = pagerAdapter
        mainViewPager.offscreenPageLimit = 2

        tabLayout.setupWithViewPager(mainViewPager)
        (0 until tabTitles.size).forEach {
            val view = TasksTabView_.build(context)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            val title = tabTitles[it]
            view.bind(title, 0)
            tabLayout.getTabAt(it)?.customView = view
        }
        mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 1 && Preferences.getAuthToken(context) == null) {
                    context.makeToast(R.string.trips_need_authorization)
                }
            }
        })
        if (!activity.intent.getStringExtra("click_action").isNullOrEmpty()) {
            mainViewPager.setCurrentItem(1, true)
        }
    }

    fun setCurrentOrdersQuantity(quantity: Int){
        (tabLayout.getTabAt(1)?.customView as TasksTabView).bind(getString(R.string.trips), quantity)
    }

    fun refreshFavorites(){
        pagerAdapter.refreshFavoritesList()
    }

    fun refreshTrips(){
        pagerAdapter.refreshTripsList()
    }

    private fun refreshCurrentOrder(shouldClear: Boolean, back: Boolean){
        pagerAdapter.refreshOrderTab(shouldClear, back)
    }

    fun showTutor(){
        childFragmentManager.beginTransaction().add(R.id.fragmentContainer, UseSwitchFragment_())
                .addToBackStack("tutor").commit()
    }

    fun hideTutor(){
        childFragmentManager.popBackStack()
        Preferences.setTutorShown(context)
    }

    fun showFragment(fragment: Fragment){
        childFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment)
                .addToBackStack("success")
                .commitAllowingStateLoss()//todo refactor
    }

    fun popBackStack(shouldRefresh: Boolean, needBackTransfer: Boolean){
        childFragmentManager.popBackStack()
        refreshAll(shouldRefresh, needBackTransfer)
    }

    fun refreshAll(shouldRefresh: Boolean, needBackTransfer: Boolean){
        refreshCurrentOrder(shouldRefresh, needBackTransfer)
        refreshTrips()
    }

    fun repeatOrder(){
        refreshCurrentOrder(true, false)
        showTransferPage()
    }

    fun showTransferPage(){
        mainViewPager.setCurrentItem(0, true)
    }
}