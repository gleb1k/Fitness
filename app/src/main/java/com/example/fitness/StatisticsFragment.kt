package com.example.fitness

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.fitness.databinding.FragmentStatisticsBinding
import com.example.fitness.profile.ProfileInfoFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.*
import com.google.gson.internal.bind.util.ISO8601Utils.format
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatisticsBinding.bind(view)

        //для подсчёта индекса массы тела
        val profileSettingsSharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_profilesettings),
            Context.MODE_PRIVATE
        )

        val weight=profileSettingsSharedPreferences?.getString("2",null)?.toDoubleOrNull()
        val height=profileSettingsSharedPreferences?.getString("3",null)?.toDoubleOrNull()
        if (weight!=null && height!=null){
            val result=(weight/((height/100)*height/100))
            val roundedUp=result.toBigDecimal().setScale(1,RoundingMode.UP).toDouble()
            if (roundedUp>=18 && roundedUp<=25){
                binding.textView2.text="${resources.getString(R.string.MassIndex)}\n$roundedUp${resources.getString(R.string.MassIndex2)}"
            }
            if(result>25){
                binding.textView2.text="${resources.getString(R.string.MassIndex)}\n$roundedUp${resources.getString(R.string.MassIndex3)}"
            }
            if(result<18){
                binding.textView2.text="${resources.getString(R.string.MassIndex)}\n$roundedUp${resources.getString(R.string.MassIndex4)}"
            }
        }
        else{
            binding.textView2.text="${resources.getString(R.string.BadData)}"
        }

        val weightSharedPref =
            activity!!.getSharedPreferences(
                getString(R.string.preference_weight),
                Context.MODE_PRIVATE
            )
        val entries = ArrayList<Entry>()
        val gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                ProfileInfoFragment.LocalDateTimeSerializer(),
            ).registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()

        val json = weightSharedPref?.getString("WeightDate", null)
        var arr: ArrayList<WeightDate> = ArrayList<WeightDate>()

        if (json != null) {
            val myobj = object : TypeToken<ArrayList<WeightDate>>() {}.type
            arr = gson.fromJson<ArrayList<WeightDate>>(json, myobj)
        }

        for (i in 0 until arr.size) {
            entries.add(Entry(
                arr[i].Date.toEpochSecond(ZoneOffset.UTC).toFloat(),
                arr[i].Weight.toFloat())
            )
        }
        val vl = LineDataSet(entries, "Ваш вес")

        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.black
        vl.fillAlpha = R.color.red

        //привязка информации к графику
        binding.lineChart.data = LineData(vl)
        //настройка отображения графика
        with(binding) {
            //угол поворота по ОХ
            lineChart.xAxis.labelRotationAngle = 0F
            //удаление правой оси
            lineChart.axisRight.isEnabled = false
            //Позволяет зумить график
            lineChart.setTouchEnabled(false)
            lineChart.setPinchZoom(false)
            //Легенда графика
            lineChart.xAxis.isEnabled = false
            lineChart.description.isEnabled = false
            // и надпись при ошибке выборки данных
            lineChart.setNoDataText("Нет данных!")
            //анимация добавления данных
        }
    }

}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        val time = json?.asJsonPrimitive?.asString
            ?: throw IllegalArgumentException("Неверный формат даты")
        return LocalDateTime.parse(time)
    }
}