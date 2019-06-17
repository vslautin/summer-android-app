package airport.transfer.sale.mvp.view

import airport.transfer.sale.mvp.model.CreditCard


interface PaymentView : BaseView {
    fun onCreditCardsReceived(cards: List<CreditCard>){}
    fun onCreditCardAdded(card: CreditCard){}
    fun onCreditCardRemoved(id: Long, adapterPosition: Int, newDefaultId: Long?){}
    fun onPublicIdReceived(publicId: String){}
    fun onDefaultChanged(id: Long){}
}