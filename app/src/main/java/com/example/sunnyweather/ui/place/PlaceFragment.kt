package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
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
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.FragmentPlaceBinding

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
    private val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
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
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter = adapter
        Log.d("Fragment","222")
        searchPlaceEdit.addTextChangedListener {
            val content = it.toString()
            Log.d("Fragment","111")
            if(content.isNotEmpty())
            {
                Log.d("Fragment","yes")
                viewModel.searchPlaces(content)
            }
            else
            {
                Log.d("Fragment","no")
               recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        Log.d("Fragment","444")
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer {
                result -> Log.d("Fragment","555")
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




