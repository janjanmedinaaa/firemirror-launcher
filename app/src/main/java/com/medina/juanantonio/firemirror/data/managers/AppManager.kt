package com.medina.juanantonio.firemirror.data.managers

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import com.medina.juanantonio.firemirror.data.models.AppInfo

class AppManager(private val context: Context) : IAppManager {

    override fun getAppList(): List<AppInfo> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

        return arrayListOf<AppInfo>().apply {
            val allApps: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
            for (ri in allApps) {
                if (context.packageName == ri.activityInfo.packageName) continue

                val appInfo = AppInfo(
                    name = ri.loadLabel(packageManager).toString(),
                    packageName = ri.activityInfo.packageName
                )
                add(appInfo)
            }
        }.distinctBy { it.packageName }
    }

    override fun openApplication(packageName: String) {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(packageName)
        val leanbackLaunchIntent =
            context.packageManager.getLeanbackLaunchIntentForPackage(packageName)

        (launchIntent ?: leanbackLaunchIntent)?.let {
            context.startActivity(it)
        }
    }
}

interface IAppManager {
    fun getAppList(): List<AppInfo>
    fun openApplication(packageName: String)
}