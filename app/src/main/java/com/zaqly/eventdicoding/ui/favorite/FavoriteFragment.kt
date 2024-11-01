package com.zaqly.eventdicoding.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaqly.eventdicoding.DetailEventActivity
import com.zaqly.eventdicoding.DetailViewModelFactory
import com.zaqly.eventdicoding.R
import com.zaqly.eventdicoding.api.adapter.AdapterActivity
import com.zaqly.eventdicoding.api.model.ListEventsItem
import com.zaqly.eventdicoding.room.FavoriteEventDatabase


class FavoriteFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: AdapterActivity
    private lateinit var progressBar: ProgressBar
    private lateinit var noDataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_event_favorite)
        progressBar = view.findViewById(R.id.progresbar_favorite)
        noDataTextView = view.findViewById(R.id.tv_no_data)

        adapter = AdapterActivity(emptyList()) { eventId ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", eventId)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dao = FavoriteEventDatabase.getDatabase(requireContext()).favoriteEventDao()
        val factory = DetailViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        observeFavorites()

        return view
    }

    private fun observeFavorites() {
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favorites ->
            progressBar.visibility = View.GONE
            if (favorites.isNullOrEmpty()) {
                noDataTextView.visibility = View.VISIBLE
                adapter.submitList(emptyList())
            } else {
                noDataTextView.visibility = View.GONE
                val items = favorites.map { favorite ->
                    ListEventsItem(
                        id = favorite.id.toIntOrNull() ?: 0,
                        name = favorite.name ?: "Unknown Event",
                        mediaCover = favorite.mediaCover
                    )
                }
                adapter.submitList(ArrayList(items))
            }
        }
    }
}