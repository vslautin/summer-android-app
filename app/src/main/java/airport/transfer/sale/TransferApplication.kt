package airport.transfer.sale

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aviasales.core.identification.IdentificationData
import ru.aviasales.core.AviasalesSDK



class TransferApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
        AviasalesSDK.getInstance().init(this, IdentificationData("140747", "43e30387ba34637a9091b49973588a96"))
    }
}
