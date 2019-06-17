package airport.transfer.sale.rest.models.response.model.v2


class Transaction(val status: Boolean = false,
                  val AcsUrl: String? = null,
                  val PaReq: String? = null,
                  val TransactionId: Long? = null,
                  val TermUrl: String? = null,
                  val message: String? = null)