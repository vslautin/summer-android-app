package airport.transfer.sale

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder


class GlideImageLoader {

    fun loadImage(context: Context, url: String?, imageView: ImageView) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .encoder(BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                    .fitCenter()
                    .into(imageView)
        } else imageView.setImageDrawable(null)
    }
}