package com.medina.juanantonio.firemirror.features.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import coil.load
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.ble.BluetoothLEService
import com.medina.juanantonio.firemirror.ble.BluetoothLEServiceManager
import com.medina.juanantonio.firemirror.ble.IBluetoothLEManager
import com.medina.juanantonio.firemirror.common.Constants.PreferencesKey.SPOTIFY_ACCESS_TOKEN
import com.medina.juanantonio.firemirror.common.Constants.PreferencesKey.SPOTIFY_CODE
import com.medina.juanantonio.firemirror.common.Constants.PreferencesKey.SPOTIFY_REFRESH_TOKEN
import com.medina.juanantonio.firemirror.common.extensions.spotifyView
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.common.views.SpotifyView
import com.medina.juanantonio.firemirror.common.views.StackLayoutManager
import com.medina.juanantonio.firemirror.data.adapters.LyricsAdapter
import com.medina.juanantonio.firemirror.data.managers.FocusManager
import com.medina.juanantonio.firemirror.data.managers.HolidayManager
import com.medina.juanantonio.firemirror.data.managers.IAppManager
import com.medina.juanantonio.firemirror.data.managers.IDataStoreManager
import com.medina.juanantonio.firemirror.data.models.BlueButtDevice
import com.medina.juanantonio.firemirror.data.models.SpotifyCurrentTrack
import com.medina.juanantonio.firemirror.data.models.listdisplay.DefaultListDisplayItem
import com.medina.juanantonio.firemirror.data.models.listdisplay.IconLabelListDisplayItem
import com.medina.juanantonio.firemirror.data.models.listdisplay.ImageListDisplayItem
import com.medina.juanantonio.firemirror.data.models.listdisplay.SpotifyListDisplayItem
import com.medina.juanantonio.firemirror.databinding.FragmentHomeBinding
import com.medina.juanantonio.firemirror.databinding.ItemListDisplaySpotifyBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding by autoCleared()
    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val stackLayoutManager = StackLayoutManager(
        horizontalLayout = false
    )
    private lateinit var focusManager: FocusManager
    private lateinit var lyricsAdapter: LyricsAdapter

    private val localBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val bluetoothLeServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            @Suppress("UNCHECKED_CAST")
            val scannedDevices = intent?.getSerializableExtra(
                BluetoothLEService.BLUE_BUTT_DEVICES
            ) as? HashMap<String, BlueButtDevice>
        }
    }

    @Inject
    lateinit var bluetoothLEServiceManager: BluetoothLEServiceManager

    @Inject
    lateinit var bluetoothLeManager: IBluetoothLEManager

    @Inject
    lateinit var appManager: IAppManager

    @Inject
    lateinit var dataStoreManager: IDataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusManager = FocusManager()
        lyricsAdapter = LyricsAdapter()

        binding.recyclerViewLyrics.apply {
            layoutManager = stackLayoutManager
            adapter = lyricsAdapter
        }

//        TODO: Register Receiver
//        localBroadcastManager.registerReceiver(
//            bluetoothLeServiceReceiver,
//            IntentFilter(BluetoothLEService.SCANNED_DEVICES)
//        )

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

        binding.listDisplaySpotify.initialize(
            itemList = arrayListOf(
                SpotifyListDisplayItem(currentTrack = null)
            ),
            noBottomPadding = true,
            onClickAction = { _, binding ->
                if (binding !is ItemListDisplaySpotifyBinding) return@initialize
                if (binding.viewSpotify.isOnStandBy) {
                    viewModel.requestUserCurrentTrack(
                        activity = requireActivity(),
                        authenticate = true
                    ) { currentTrack ->
                        updateSpotifyLyrics(binding.viewSpotify, currentTrack)
                    }
                }
            }
        )

        binding.listDisplayViewApps.textAlignment = TEXT_ALIGNMENT_TEXT_END
        binding.listDisplayViewApps.initialize(
            title = getString(R.string.applications),
            itemList = ArrayList(
                appManager.getAppList().map {
                    DefaultListDisplayItem(it.name, it.packageName)
                }
            ),
            onClickAction = { item, _ ->
                appManager.openApplication((item as DefaultListDisplayItem).value)
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
            onClickAction = { item, _ ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToImageViewerDialog(
                        imageUrlArg = ((item as ImageListDisplayItem).imageUrl)
                    )
                )
            }
        )

        binding.listDisplayUpcomingEvents.post {
            focusManager.setupViewFocusList(
                arrayListOf(
                    binding.listDisplayUpcomingEvents,
                    binding.listDisplaySpotify,
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
        viewModel.requestUserCurrentTrack(
            activity = requireActivity(),
            authenticate = false
        ) { currentTrack ->
            binding.listDisplaySpotify.spotifyView?.let {
                updateSpotifyLyrics(it, currentTrack)
            }
        }
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
            it ?: return@observe

            val quoteString = if (it.author != null) getString(
                R.string.quote_with_author,
                it.text,
                it.author
            ) else it.text

            if (viewModel.songLyrics.value?.isEmpty() == true) {
                lyricsAdapter.setLyrics(arrayListOf(quoteString))
                stackLayoutManager.scrollToPosition(0f, duration = 500)
            }
        }

        viewModel.songLyrics.observe(viewLifecycleOwner) { lyrics ->
            if (lyrics.isEmpty()) viewModel.quote.run { value = value }
            else lyricsAdapter.setLyrics(lyrics)
            stackLayoutManager.scrollToPosition(0f, duration = 500)
        }

        viewModel.spotifyCode.observe(viewLifecycleOwner) { code ->
            viewModel.viewModelScope.launch {
                dataStoreManager.putString(SPOTIFY_CODE, code)
                viewModel.requestAccessToken(code)
            }
        }

        viewModel.spotifyAccessToken.observe(viewLifecycleOwner) { accessToken ->
            viewModel.viewModelScope.launch {
                dataStoreManager.putString(SPOTIFY_ACCESS_TOKEN, accessToken)
            }

            if (viewModel.isSpotifyRequestPending) {
                viewModel.requestUserCurrentTrack(
                    activity = requireActivity(),
                    authenticate = false
                ) { currentTrack ->
                    binding.listDisplaySpotify.spotifyView?.let {
                        updateSpotifyLyrics(it, currentTrack)
                    }
                }
                viewModel.isSpotifyRequestPending = false
            }
        }

        viewModel.spotifyRefreshToken.observe(viewLifecycleOwner) { refreshToken ->
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                dataStoreManager.putString(SPOTIFY_REFRESH_TOKEN, refreshToken)
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
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    val previousItem = stackLayoutManager.currentItem - 1
                    if (previousItem < 0) return@observe

                    stackLayoutManager.scrollToPosition(previousItem)
                }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    val nextItem = stackLayoutManager.currentItem + 1
                    val lyricsMaxIndex = viewModel.songLyrics.value?.size?.minus(1) ?: -1
                    if (nextItem > lyricsMaxIndex) return@observe

                    stackLayoutManager.scrollToPosition(nextItem)
                }
            }
        }

        mainViewModel.authorizationResponse.observe(viewLifecycleOwner) {
            when (it.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    viewModel.spotifyAccessToken.value = it.accessToken
                }
                AuthorizationResponse.Type.CODE -> {
                    viewModel.spotifyCode.value = it.code
                }
                else -> Unit
            }
        }
    }

    private fun updateSpotifyLyrics(
        spotifyView: SpotifyView,
        currentTrack: SpotifyCurrentTrack?
    ) {
        spotifyView.updateView(currentTrack)
        if (currentTrack == null) viewModel.quote.run { value = value }
        else viewModel.getSongLyrics(currentTrack)
    }
}