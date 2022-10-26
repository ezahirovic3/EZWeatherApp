package test.weather.application

import android.annotation.SuppressLint
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import test.weather.application.models.weekly.DetailModel
import java.text.SimpleDateFormat
import java.util.*

class SevenDayRecyclerAdapter(
    private var  sevenDay: DetailModel
) : RecyclerView.Adapter<SevenDayRecyclerAdapter. SevenDayViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  SevenDayViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_weather, parent, false)
        return  SevenDayViewHolder(view)
    }

    override fun getItemCount(): Int =  sevenDay.list.size

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder:  SevenDayViewHolder, position: Int) {
        val sdf = SimpleDateFormat("EEEE, dd LLL")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH,position)
        val currentDate=sdf.format(cal.time)
        holder.date.text = currentDate
        weatherIcon(sevenDay.list[position].weather[0].id,holder.icon)
        holder.icon.setColorFilter(Color.WHITE)
        holder.temp2.text = MainActivity().kelvinToCelsius(sevenDay.list[position].temp.max).toString()+"°"
        holder.temp3.text = "/ "+ MainActivity().kelvinToCelsius(sevenDay.list[position].temp.min).toString()+"°"

        if(position == 0 ){holder.card.removeViewAt(0)}
    }

    private fun weatherIcon(icon: Int, imgView: ImageView){
        if(icon in 200..232){imgView.setImageResource(R.drawable._11d)}
        if(icon in 300..321){imgView.setImageResource(R.drawable._9d)}
        if(icon in 500..531){imgView.setImageResource(R.drawable._10d)}
        if(icon in 600..622){imgView.setImageResource(R.drawable._13d)}
        if(icon in 701..781){imgView.setImageResource(R.drawable._50d)}
        if(icon in 801..804){imgView.setImageResource(R.drawable._4d)}
        if(icon == 800){imgView.setImageResource(R.drawable._1d)}
    }

    inner class  SevenDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.date3)
        val icon: ImageView = itemView.findViewById(R.id.icon3)
        val temp2: TextView = itemView.findViewById(R.id.temp2)
        val temp3: TextView = itemView.findViewById(R.id.temp3)
        val card: CardView = itemView.findViewById(R.id.card2)
    }

}
