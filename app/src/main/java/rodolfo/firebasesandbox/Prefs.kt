package rodolfo.firebasesandbox

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Created by Rodolfo on 15/06/2018.
 */
class Prefs {

    var prefs: SharedPreferences? = null



    constructor(ctx:Context){
        this.prefs = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), MODE_PRIVATE)
    }

    fun putObject(key:String, obj:Any?){
        val prefsEditor: SharedPreferences.Editor = prefs!!.edit()
        val gson = Gson()
        val json:String = gson.toJson(obj)
        prefsEditor.putString(key, json)
        prefsEditor.apply()
    }

    fun getObject(key:String, c: Class<String>): Any? {
        val gson = Gson()
        val json: String = prefs!!.getString(key, "")
        return if (json == "")
            ""
        else
            gson.fromJson(json, c)
    }


}