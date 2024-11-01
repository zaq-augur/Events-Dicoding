package com.zaqly.eventdicoding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zaqly.eventdicoding.api.model.Event
import com.zaqly.eventdicoding.databinding.ActivityDetailEventBinding
import com.zaqly.eventdicoding.room.FavoriteEvent
import com.zaqly.eventdicoding.room.FavoriteEventDao
import com.zaqly.eventdicoding.room.FavoriteEventDatabase

class DetailEventActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetailEventBinding
    private lateinit var viewModel: DetailEventViewModel
    private lateinit var eventId: String
    private lateinit var fabFavorite: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao: FavoriteEventDao = FavoriteEventDatabase.getDatabase(this).favoriteEventDao()
        viewModel = ViewModelProvider(this, DetailViewModelFactory(dao)).get(DetailEventViewModel::class.java)

        fabFavorite = findViewById(R.id.fab)

        eventId = intent.getStringExtra("EVENT_ID") ?: ""

        if (eventId.isNotEmpty()) {
            setupFavoriteButton(eventId)
            viewModel.fetchEventDetails(eventId)
            observeViewModel()
        } else {
            showError("Invalid event ID")
            finish()
        }

        setupActionBar()
        setupScrollListener()
    }

    private fun setupFavoriteButton(eventId: String) {
        viewModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            if (favoriteEvent == null) {
                fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24dp)
            } else {
                fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24dp)
            }

            fabFavorite.setOnClickListener {
                if (favoriteEvent == null) {
                    val eventDetail = viewModel.eventDetail.value
                    if (eventDetail != null) {
                        val newFavorite = FavoriteEvent(
                            id = eventId,
                            name = eventDetail.name ?: "Unknown Event",
                            mediaCover = eventDetail.mediaCover
                        )
                        viewModel.insertFavorite(newFavorite)
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24dp)
                        showToast("Event added to favorites")
                    } else {
                        showError("Unable to add to favorites")
                    }
                } else {
                    viewModel.deleteFavorite(favoriteEvent)
                    fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24dp)
                    showToast("Event removed from favorites")
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(this) { event ->
            event?.let { displayEventDetails(it) }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                showError(message)
            }
        }
    }

    private fun displayEventDetails(event: Event) {
        binding.apply {
            supportActionBar?.title = event.name
            tvJudulDetail.text = event.name ?: "No Name"
            tvSummaryDetail.text = event.summary ?: "No Summary"

            val remainingQuota = (event.quota ?: 0) - (event.registrants ?: 0)
            val displayQuota = maxOf(remainingQuota, 0)
            tvQuotaDetail.text = "Sisa Quota $displayQuota"
            tvTanggalJamDetail.text = event.beginTime ?: "-"

            tvDescription.text = HtmlCompat.fromHtml(
                event.description ?: "No Description",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(this@DetailEventActivity)
                .load(event.mediaCover)
                .centerCrop()
                .into(ivEventDetail)

            btnRegister.setOnClickListener {
                event.link?.let { link ->
                    openLinkInBrowser(link)
                } ?: run {
                    showError("No link available")
                }
            }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            hide()
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
    }

    private fun setupScrollListener() {
        binding.main.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                supportActionBar?.show()
            } else {
                supportActionBar?.hide()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progresBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
