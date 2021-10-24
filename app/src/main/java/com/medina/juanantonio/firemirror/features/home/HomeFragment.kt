package com.medina.juanantonio.firemirror.features.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.medina.juanantonio.firemirror.Constants.PreferencesKey.SPOTIFY_TOKEN
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.data.managers.FocusManager
import com.medina.juanantonio.firemirror.data.managers.HolidayManager
import com.medina.juanantonio.firemirror.data.managers.IAppManager
import com.medina.juanantonio.firemirror.data.managers.IDataStoreManager
import com.medina.juanantonio.firemirror.data.managers.ISpotifyManager
import com.medina.juanantonio.firemirror.data.models.listdisplay.DefaultListDisplayItem
import com.medina.juanantonio.firemirror.data.models.listdisplay.IconLabelListDisplayItem
import com.medina.juanantonio.firemirror.data.models.listdisplay.ImageListDisplayItem
import com.medina.juanantonio.firemirror.databinding.FragmentHomeBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding by autoCleared()
    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var focusManager: FocusManager

    @Inject
    lateinit var appManager: IAppManager

    @Inject
    lateinit var dataStoreManager: IDataStoreManager

    @Inject
    lateinit var spotifyManager: ISpotifyManager

    private var isSpotifyRequestPending = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        focusManager = FocusManager()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listDisplayUpcomingEvents.initialize(
            title = getString(R.string.upcoming),
            itemList = ArrayList(
                HolidayManager.getHolidays().map {
                    IconLabelListDisplayItem(
                        drawable = R.drawable.ic_calendar,
                        label = it.name
                    )
                }
            )
        )

        binding.listDisplayViewApps.textAlignment = TEXT_ALIGNMENT_TEXT_END
        binding.listDisplayViewApps.initialize(
            title = getString(R.string.applications),
            itemList = ArrayList(
                appManager.getAppList().map {
                    DefaultListDisplayItem(it.name, it.packageName)
                }
            ),
            onClickAction = {
                appManager.openApplication((it as DefaultListDisplayItem).value)
            }
        )

        binding.listDisplayGuestWifiQr.textAlignment = TEXT_ALIGNMENT_TEXT_END
        binding.listDisplayGuestWifiQr.initialize(
            title = getString(R.string.guest_wifi_access),
            itemList = arrayListOf(
                ImageListDisplayItem(
                    imageUrl = getString(
                        R.string.qr_url,
                        "WIFI:T:WPA;S:Medina Condo WIFI;P:24418A001431;H:;"
                    )
                )
            ),
            onClickAction = {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToImageViewerDialog(
                        imageUrlArg = ((it as ImageListDisplayItem).imageUrl)
                    )
                )
            }
        )

        binding.listDisplayUpcomingEvents.post {
            focusManager.setupViewFocusList(
                arrayListOf(
                    binding.listDisplayUpcomingEvents,
                    binding.listDisplayGuestWifiQr,
                    binding.listDisplayViewApps
                )
            )
        }

        listenToVM()
        listenToActivityVM()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setupWeatherTimerTask()
        requestUserCurrentTrack()
    }

    override fun onDestroyView() {
        viewModel.destroyTimerTask()
        super.onDestroyView()
    }

    private fun listenToVM() {
        viewModel.currentWeather.observe(viewLifecycleOwner) {
            binding.imageViewWeatherIcon.load(
                getString(R.string.weather_icon_url, it.weather.first().icon)
            )
            binding.textViewTemperature.text =
                getString(R.string.temperature, it.main.temp)
            binding.textViewFeelsTemperature.text =
                getString(R.string.feels_temperature, it.main.feels_like)
            binding.textViewTemperatureLocation.text =
                getString(R.string.temperature_location, it.name)
        }

        viewModel.quote.observe(viewLifecycleOwner) {
            val quoteString = if (it.author != null) getString(
                R.string.quote_with_author,
                it.text,
                it.author
            ) else it.text
            binding.textViewQuote.text = quoteString
        }

        viewModel.spotifyAccessToken.observe(viewLifecycleOwner) {
            viewModel.viewModelScope.launch {
                dataStoreManager.putString(SPOTIFY_TOKEN, it)
                if (isSpotifyRequestPending) {
                    requestUserCurrentTrack()
                    isSpotifyRequestPending = false
                }
            }
        }
    }

    private fun listenToActivityVM() {
        mainViewModel.dispatchKeyEvent.observe(viewLifecycleOwner) {
            when (it.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> focusManager.focusPreviousItem()
                KeyEvent.KEYCODE_DPAD_DOWN -> focusManager.focusNextItem()
                KeyEvent.KEYCODE_DPAD_RIGHT -> focusManager.focusNextList()
                KeyEvent.KEYCODE_DPAD_LEFT -> focusManager.focusPreviousList()
                KeyEvent.KEYCODE_MENU -> {
                }
            }
        }

        mainViewModel.authorizationResponse.observe(viewLifecycleOwner) {
            when (it.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    viewModel.spotifyAccessToken.value = it.accessToken
                }
                else -> Unit
            }
        }
    }

    private fun requestUserCurrentTrack() {
        viewModel.viewModelScope.launch {
            val token = dataStoreManager.getString(SPOTIFY_TOKEN)
            if (token.isEmpty())
                spotifyManager.authenticate(requireActivity())
            else {
                val (requestCode, currentTrack) =
                    spotifyManager.getUserCurrentTrack(token)

                if (requestCode == 401) {
                    spotifyManager.authenticate(requireActivity())
                    isSpotifyRequestPending = true
                }

                binding.viewSpotify.updateView(currentTrack)

                Timer().schedule(2000) {
                    requestUserCurrentTrack()
                }
            }
        }
    }
}