package com.medina.juanantonio.firemirror.features.home

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.medina.juanantonio.firemirror.R
import com.medina.juanantonio.firemirror.common.utils.autoCleared
import com.medina.juanantonio.firemirror.data.managers.IAppManager
import com.medina.juanantonio.firemirror.data.managers.IFocusManager
import com.medina.juanantonio.firemirror.data.managers.IHolidayManager
import com.medina.juanantonio.firemirror.data.models.DefaultListDisplayItem
import com.medina.juanantonio.firemirror.data.models.IconLabelListDisplayItem
import com.medina.juanantonio.firemirror.data.models.ImageListDisplayItem
import com.medina.juanantonio.firemirror.databinding.FragmentHomeBinding
import com.medina.juanantonio.firemirror.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding by autoCleared()
    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var appManager: IAppManager

    @Inject
    lateinit var holidayManager: IHolidayManager

    @Inject
    lateinit var focusManager: IFocusManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listDisplayUpcomingEvents.initialize(
            title = "Upcoming",
            itemList = ArrayList(
                holidayManager.getHolidays().map {
                    IconLabelListDisplayItem(
                        drawable = R.drawable.ic_calendar,
                        label = "${it.date} | ${it.name}"
                    )
                }
            )
        )

        binding.listDisplayViewApps.textAlignment = TEXT_ALIGNMENT_TEXT_END
        binding.listDisplayViewApps.initialize(
            title = "Installed Apps",
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
            title = "Guest WIFI Access",
            itemList = arrayListOf(
                ImageListDisplayItem(
                    imageUrl = getString(R.string.qr_url_link, "Sample")
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
    }

    override fun onDestroyView() {
        viewModel.destroyTimerTask()
        super.onDestroyView()
    }

    private fun listenToVM() {
        viewModel.currentWeather.observe(viewLifecycleOwner) {
            Log.d("DEVELOP", "$it")
        }
    }

    private fun listenToActivityVM() {
        mainViewModel.dispatchKeyEvent.observe(viewLifecycleOwner) {
            when (it.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> focusManager.focusPreviousItem()
                KeyEvent.KEYCODE_DPAD_DOWN -> focusManager.focusNextItem()
                KeyEvent.KEYCODE_DPAD_RIGHT -> focusManager.focusNextList()
                KeyEvent.KEYCODE_DPAD_LEFT -> focusManager.focusPreviousList()
            }
        }
    }
}