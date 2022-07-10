package com.example.fitness.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitness.R
import com.example.fitness.databinding.FragmentProfileinfoBinding

class ProfileInfoFragment : Fragment(R.layout.fragment_profileinfo) {
    private var _binding :  FragmentProfileinfoBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileinfoBinding.bind(view)

        //Принимаю id от профиля
        val id = arguments?.getString(ARG_TEXT).orEmpty()

        //binding.numberPicker.minValue = 0
        //binding.numberPicker.maxValue = 250
        //binding.numberPicker.wrapSelectorWheel = false

        if (id.isNotEmpty()){
            binding.tvName.text = "${resources.getString(R.string.text1)} ${ProfileInfoRepository.infoList[id.toInt()].name.lowercase()}"
        }
        with(binding) {
            iBtnBack.setOnClickListener {
                findNavController().navigate(
                    R.id.action_profileInfoFragment_to_profileFragment
                )
            }
            // TODO: Сделать сохранение данных в бд (как напишут бд)
            val gender = arrayOf("Мужчина","Женщина")
            btnSave.setOnClickListener {
                val bundle: Bundle = if (id.toInt() != 1) {
                    ProfileFragment.createBundle(numberPicker.value.toString(), id)
                } else {
                    ProfileFragment.createBundle(gender[numberPicker.value], id)
                }
                findNavController().navigate(
                    R.id.action_profileInfoFragment_to_profileFragment,
                    bundle)
                }

            //Возраст
            if (id.toInt() == 0) {
                numberPicker.minValue = 14
                numberPicker.maxValue = 100
                numberPicker.wrapSelectorWheel = false
//                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
//                    textView.text = "Выбранное значение: $newVal"
//                }
            }
            //Пол
            if (id.toInt() == 1) {
                numberPicker.minValue = 0
                numberPicker.maxValue = gender.size - 1
                numberPicker.wrapSelectorWheel = false
                numberPicker.displayedValues = gender

            }
            //Вес
            if (id.toInt() == 2) {
                numberPicker.minValue = 20
                numberPicker.maxValue = 250
                numberPicker.wrapSelectorWheel = false
            }
            //Рост
            if (id.toInt() == 3) {
                numberPicker.minValue = 50
                numberPicker.maxValue = 250
                numberPicker.wrapSelectorWheel = false
            }
        }
    }

    companion object {
        private const val ARG_TEXT = "id"

        fun createBundle(text: String): Bundle {
            val bundle = Bundle()
            bundle.putString(
                ARG_TEXT,
                text
            )
            return bundle
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}