package airport.transfer.sale.mvp.view


interface PriceView : BaseView{
    fun onPriceReceived(price: Int)
}