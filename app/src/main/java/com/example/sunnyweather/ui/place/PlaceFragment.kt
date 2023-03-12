package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.FragmentPlaceBinding


class PlaceFragment : Fragment() {
    val binding = FragmentPlaceBinding.inflate(LayoutInflater.from(context))
    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
    private lateinit var adapter: PlaceAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().lifecycle.addObserver(object : DefaultLifecycleObserver {
            @SuppressLint("NotifyDataSetChanged")
            override fun onCreate(owner: LifecycleOwner) {
                val layoutManager = LinearLayoutManager(activity)
                val recyclerView : RecyclerView = binding.root.findViewById(R.id.recyclerView)
                recyclerView.layoutManager = layoutManager
                adapter = PlaceAdapter(viewLifecycleOwner as Fragment,viewModel.placeList)
                recyclerView.adapter = adapter
                val searchPlaceEdit : EditText = binding.root.findViewById(R.id.searchPlaceEdit)
                val baImageView : ImageView = binding.root.findViewById(R.id.bgImageView)
                searchPlaceEdit.addTextChangedListener {
                     editable -> val content = editable.toString()
                    if(content.isNotEmpty())
                    {
                        viewModel.searchPlaces(content)
                    }
                    else
                    {
                        recyclerView.visibility = View.GONE
                        baImageView.visibility = View.VISIBLE
                        viewModel.placeList.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
                viewModel.placeLiveData.observe(viewLifecycleOwner, Observer {
                        result -> val places = result.getOrNull()
                    if(places!=null)
                    {
                        recyclerView.visibility = View.VISIBLE
                        baImageView.visibility = View.GONE
                        viewModel.placeList.clear()
                        viewModel.placeList.addAll(places)
                        adapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(activity,"未查询到任何地点",Toast.LENGTH_SHORT).show()
                        result.exceptionOrNull()?.printStackTrace()
                    }
                })
                owner.lifecycle.removeObserver(this)
            }
        })

    }

}




