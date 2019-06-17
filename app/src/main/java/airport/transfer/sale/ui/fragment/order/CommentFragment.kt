package airport.transfer.sale.ui.fragment.order

import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_comment)
open class CommentFragment : BaseFragment(){

    @AfterViews
    fun ready(){
       /* bottomText.text = getString(R.string.add_comment)
        val order = Preferences.getCurrentOrder(context)
        order.comment?.let { commentText.setTextChars(it) }
        addCommentButton.setOnClickListener {
            order.comment = commentText.text.toString()
            Preferences.saveCurrentOrder(order, context)
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        }*/
    }
}
