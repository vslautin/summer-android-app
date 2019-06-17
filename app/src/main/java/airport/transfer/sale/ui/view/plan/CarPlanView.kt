package airport.transfer.sale.ui.view.plan

import android.content.Context
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_plan.view.*
import airport.transfer.sale.*
import airport.transfer.sale.rest.models.response.model.Plan
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_plan)
open class CarPlanView(context: Context) : BasePlanView(context){

    fun bind(plan: Plan, imageLoader: GlideImageLoader){
        planName.text = plan.title
        planDescription.text = plan.about
        passengersText.text = plan.passenger.getPassengersString(context)
        luggageText.text = plan.baggage.getLuggageString(context)
        //carImage.setImageResource(plan.id.getResourceByTariffId())
        if (plan.coupon == null) priceText.text = plan.viewPrice// String.format(Locale.CANADA,"%s%s", context.getString(R.string.dollar), plan.price.toString())
        else priceText.text = plan.coupon?.viewPrice
        imageLoader.loadImage(context, plan.image, carImage)
    }

}