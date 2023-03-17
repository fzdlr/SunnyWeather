package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.FragmentPlaceBinding
import com.example.sunnyweather.ui.weather.WeatherActivity

private lateinit var recyclerView: RecyclerView
@SuppressLint("StaticFieldLeak")
private lateinit var actionBarLayout: FrameLayout
@SuppressLint("StaticFieldLeak")
private lateinit var searchPlaceEdit: EditText
@SuppressLint("StaticFieldLeak")
private lateinit var bgImageView: ImageView
@SuppressLint("StaticFieldLeak")
private lateinit var mRootView: View


//控件绑定
private fun initViews(view: View) {
    bgImageView = view.findViewById(R.id.bgImageView)
    searchPlaceEdit = view.findViewById(R.id.searchPlaceEdit)
    actionBarLayout = view.findViewById(R.id.actionBarLayout)
    recyclerView = view.findViewById(R.id.recyclerView)
}
class PlaceFragment : Fragment() {
    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
    private lateinit var adapter: PlaceAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.fragment_place, container, false)
        initViews(mRootView)
        return mRootView
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity is MainActivity && viewModel.isPlaceSaved())
        {
            val place = viewModel.getSavePlace()
            val intent = Intent(context,WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener {
            val content = it.toString()
            if(content.isNotEmpty())
            {
                viewModel.searchPlaces(content)
            }
            else
            {
               recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer {
                result ->
            val places = result.getOrNull()
            if(places!=null)
            {
                Log.d("Fragment","666")
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }
            else{
                Log.d("Fragment","777")
                Toast.makeText(activity,"未查询到任何地点",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}




