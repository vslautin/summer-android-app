package airport.transfer.sale.adapter

import android.view.ViewGroup
import airport.transfer.sale.mvp.model.CarPriceModel
import airport.transfer.sale.ui.view.CarPriceView
import airport.transfer.sale.ui.view.CarPriceView_


class CarPriceAdapter : RecyclerViewAdapterBase<CarPriceModel, CarPriceView>(){
    override fun onCreateItemView(parent: ViewGroup, viewType: Int): CarPriceView {
        return CarPriceView_.build(parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolderWrapper<CarPriceView>?, position: Int) {
        holder?.view?.bind(items[position])
    }
}