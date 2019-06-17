package airport.transfer.sale.rest.models.response.model.v2

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Review(@PrimaryKey var id: Long = 0,
                  var rate: Int = 0,
                  var comment: String = ""): RealmObject()