package rodolfo.firebasesandbox

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import rodolfo.firebasesandbox.models.DadosEnvio
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Rodolfo on 14/06/2018.
 */
class MainActivity : AppCompatActivity(), TopicsAdapter.OnItemClickListener {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private var sheetBehavior: BottomSheetBehavior<View>? = null
    var topics: MutableList<Boolean> = mutableListOf()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var dbRef: DatabaseReference
    lateinit var dbTopicsRef: DatabaseReference

    var databaseTopics:HashMap<Int, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        configFirebase()

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        (sheetBehavior)!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        return
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        btnEnviarMensagem.setOnClickListener {
            if (!edtMensagem.text.isEmpty()) {
                tilMensagem.isErrorEnabled = false
                val dados = DadosEnvio(Calendar.getInstance().timeInMillis.toString(), edtMensagem.text.toString(), topics)
                dbRef.setValue(dados)
            }
            else {
                tilMensagem.isErrorEnabled = true
                tilMensagem.error = ("Digite uma mensagem para continuar")
            }
        }

        if (mAuth.currentUser == null) {
            pbLoading.visibility = View.VISIBLE
            mAuth.signInAnonymously().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    pbLoading.visibility = View.GONE
                    txtLogado.text = ("Logado")
                    val teste:String = Prefs(this).getObject("PREFERENCIAS", String::class.java) as String
                    Log.e("Prefs", teste)
                }
            }
        }else {
            txtLogado.text = ("Logado")
            pbLoading.visibility = View.GONE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id:Int = item!!.itemId

        when(id) {
            R.id.notifications -> {
                sheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configFirebase() {
        dbRef = database.getReference(Prefs(this@MainActivity).getObject("PREFERENCIAS", String::class.java) as String)
        dbTopicsRef = database.getReference("topics")

        val messageListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.e("Database Error", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    txtMensagem.text = (p0
                            .child("message").value.toString())
                }
            }

        }

        dbRef.addValueEventListener(messageListener)

        val topicsListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.e("Database Error", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (i in 0 until p0.childrenCount){
                        databaseTopics[i.toInt()] = p0.child(i.toString()).value.toString()
                        topics.add(false)
                    }
                    setupRecycler(databaseTopics)
                }
            }
        }
        dbTopicsRef.addValueEventListener(topicsListener)
    }

    fun setupRecycler(list:HashMap<Int, String>){

        rvNotify.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvNotify.layoutManager = layoutManager
        val mAdapter = TopicsAdapter(list, topics)
        rvNotify.adapter = mAdapter

        mAdapter.setClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        val user:FirebaseUser? = mAuth.currentUser
        if(user != null) {
            txtLogado.text = ("Logado")
        } else {
            txtLogado.text = ("Deslogado")
        }
    }

    override fun onClick(view: View?, position: Int, switch: Switch) {
        Log.e("click","Clicou")
        if(!switch.isChecked) {
            switch.isChecked = true
            topics[position] = true
            FirebaseMessaging.getInstance().subscribeToTopic(databaseTopics[position])
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Toast.makeText(this, "Erro ao se inscrever", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this, "Inscrito com Sucesso", Toast.LENGTH_SHORT).show()
                    }
        } else {
            switch.isChecked = false
            topics[position] = false
            FirebaseMessaging.getInstance().unsubscribeFromTopic(databaseTopics[position])
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Toast.makeText(this, "Erro ao se desinscrever", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this, "Desinscrito com Sucesso", Toast.LENGTH_SHORT).show()
                    }
        }
        dbRef.child("topics").setValue(topics)
    }

}