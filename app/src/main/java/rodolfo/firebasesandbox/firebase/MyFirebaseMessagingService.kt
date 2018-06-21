package rodolfo.firebasesandbox.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Rodolfo on 14/06/2018.
 */
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        Log.d("From: ", p0!!.from)

        if (p0.data.isNotEmpty()){
            Log.d("Message data payload: ", p0.data.toString())
        }

        if(p0.notification != null){
            Log.d("Notification Body: ", p0.notification!!.body)
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

}