package airport.transfer.sale.adapter

import android.view.ViewGroup
import airport.transfer.sale.mvp.model.CarPriceModel
import airport.transfer.sale.ui.view.CarClassView
import airport.transfer.sale.ui.view.CarClassView_

class CarClassAdapter : RecyclerViewAdapterBase<CarPriceModel, CarClassView>(){

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): CarClassView {
        return CarClassView_.build(parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolderWrapper<CarClassView>?, position: Int) {
        holder?.view?.bind(items[position])
    }

    fun findPositionById(id: Int): Int = items.find { it.id == id }?.let { getItemPosition(it) } ?: -1

}