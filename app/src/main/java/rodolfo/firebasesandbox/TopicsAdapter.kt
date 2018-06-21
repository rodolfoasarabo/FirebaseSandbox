package rodolfo.firebasesandbox

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView

/**
 * Created by Rodolfo on 19/06/2018.
 */
class TopicsAdapter(var values: HashMap<Int, String>, var subscribedList: MutableList<Boolean>) : RecyclerView.Adapter<TopicsAdapter.TopicsAdapterHolder>() {

    lateinit var itemClickListener: OnItemClickListener

    fun setClickListener(itemClickListener: OnItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicsAdapterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_notificacao, parent, false)
        return TopicsAdapterHolder(view)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: TopicsAdapterHolder, position: Int) {
        val valor:String = values[position]!!



        holder.txtNotificacao.text = valor
    }

    inner class TopicsAdapterHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var btnOnOff:Switch = itemView!!.findViewById(R.id.btnOnOff)
        var txtNotificacao:TextView = itemView!!.findViewById(R.id.txtNotificacao)

        override fun onClick(p0: View?) {
            itemClickListener.onClick(p0, adapterPosition, btnOnOff)
        }

        init {
            itemView!!.setOnClickListener(this)
        }


    }

    interface OnItemClickListener{
        fun onClick(view: View?, position: Int, switch: Switch)
    }

}