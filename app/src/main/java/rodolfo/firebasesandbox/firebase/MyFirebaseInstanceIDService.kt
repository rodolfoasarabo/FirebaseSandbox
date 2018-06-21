package rodolfo.firebasesandbox.firebase

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import rodolfo.firebasesandbox.Prefs

/**
 * Created by Rodolfo on 14/06/2018.
 */
class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refresh:String = FirebaseInstanceId.getInstance().token!!

        Prefs(applicationContext).putObject("PREFERENCIAS", refresh)
        Log.e("Token FCM", refresh)

    }

}