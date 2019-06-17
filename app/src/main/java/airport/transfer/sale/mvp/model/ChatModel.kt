package airport.transfer.sale.mvp.model


data class ChatModel(val id: Int,
                     val message: String,
                     val timestamp: Long,
                     val fromClient: Boolean,
                     var isDelivered: Boolean)