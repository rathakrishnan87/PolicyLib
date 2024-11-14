package com.cbre.privacyprompt

import android.content.Context
import android.telephony.TelephonyManager
import java.util.*

private enum class EUCountry {
    AT, BE, BG, HR, CY, CZ, DK, EE, FI, FR, DE, GR, HU, IE, IT, LV, LT, LU, MT, NL, PL, PT, RO, SK, SI, ES, SE, GB, // 28 member states
    GF, PF, TF, // French territories French Guiana, Polynesia, Southern Territories
    EL, UK, // alternative EU names for GR and GB
    ME, IS, AL, RS, TR, MK;
}
// Europe Union Helper
//Configure to user EU Zone only or Global Zone
object ZoneHelper {

    fun isEu(context: Context): Boolean {
        return isEuByNetwork(context) || isEuBySim(context) || isEuByZoneId()
    }

    private fun contains(countryCode: String): Boolean {
        return EUCountry.values().any { it.name.equals(countryCode, ignoreCase = true) }
    }
    /**
     * Check if the mobile card is from EU region
     * */
    private fun isEuBySim(context: Context): Boolean {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso?.uppercase()
            simCountry != null && simCountry.length == 2 && contains(simCountry)
        } catch (e: SecurityException) {
            false
        }
    }
    /**
     * Check if the network is in EU region
     * */
    private fun isEuByNetwork(context: Context): Boolean {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA && tm.phoneType != TelephonyManager.PHONE_TYPE_NONE) {
                val networkCountry = tm.networkCountryIso?.uppercase()
                networkCountry != null && networkCountry.length == 2 && contains(networkCountry)
            } else {
                false
            }
        } catch (e: SecurityException) {
            false
        }
    }
    /**
     * Check by Zone ID
     * */
    private fun isEuByZoneId(): Boolean {
        return try {
            val tz = TimeZone.getDefault().id.lowercase()
            tz.contains("euro")
        } catch (e: Exception) {
            false
        }
    }
}
