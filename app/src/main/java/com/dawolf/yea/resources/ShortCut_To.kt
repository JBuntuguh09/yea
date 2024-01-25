package com.dawolf.yea.resources

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dawolf.yea.R
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object ShortCut_To {
    const val DATEWITHTIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATEFORMATDDMMYYYY = "dd/MM/yyyy"
    const val DATEFORMATDDMMYYYY2 = "dd-MM-yyyy"
    const val DATEFORMATYYYYMMDD = "yyyy-MM-dd"
    const val TIME = "hh:mm a"
    const val DATEWITHTIMEDDMMYYY = "dd-MM-yyyy'T'HH:mm:ss.SSS'Z'"
    val currentDatewithTime: String
        get() {
            val dateFormat = SimpleDateFormat(DATEWITHTIMEDDMMYYY, Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }

    fun getCurrentDateTime(): String {
        val currentDateTime = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(currentDateTime)
    }

    fun hideKeyboard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            Log.d("No keyboard", "No keyboard to drop")
        }
    }

    val currentDates: String
        get() {
            val dateFormat = SimpleDateFormat(DATEFORMATYYYYMMDD, Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }
    val currentDateFormat2: String
        get() {
            val dateFormat = SimpleDateFormat(DATEFORMATDDMMYYYY, Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }
    var getServices = arrayOf(
        "Select Service",
        "Android App",
        "Web App",
        "Mobile App",
        "Website Development",
        "Web App",
        "Technical Support"
    )

    fun getRawResourceUri(context: Context, rawResourceId: Int): Uri {
        return Uri.parse("android.resource://${context.packageName}/${rawResourceId}")
    }

    fun decodeBase64(input: String?): Bitmap? {
        try {
            val decodedByte = Base64.decode(input, 0)
            return BitmapFactory.decodeByteArray(
                decodedByte, 0,
                decodedByte.size
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getTimeFromDate(str: String?): String {
        return if (str != null && !str.equals(
                "null",
                ignoreCase = true
            ) && str.trim { it <= ' ' }.length != 0
        ) {
            val sdf1 = SimpleDateFormat(DATEWITHTIME, Locale.US)
            val sdf2 = SimpleDateFormat(TIME, Locale.US)
            try {
                val date = sdf1.parse(str)
                sdf2.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        } else {
            " "
        }
    }

    val currentDay: String
        get() {
            val daysArray = arrayOf("sun", "mon", "tue", "wed", "thu", "fri", "sat")
            val calendar = Calendar.getInstance()
            val day = calendar[Calendar.DAY_OF_WEEK]
            val mt = calendar[Calendar.MONTH]
            val yr = calendar[Calendar.YEAR]
            return yr.toString() + daysArray[day - 1] + mt
        }
    val currentMonthYear: String
        get() {
            val c = Calendar.getInstance()
            val currMonth = c[Calendar.MONTH] + 1
            val currYear = c[Calendar.YEAR]
            val curDay = c[Calendar.DAY_OF_MONTH]
            return "$currMonth/$currYear"
        }
    val currentDayMonthYear: String
        get() {
            val c = Calendar.getInstance()
            val currMonth = c[Calendar.MONTH]
            val currYear = c[Calendar.YEAR]
            val curDay = c[Calendar.DAY_OF_MONTH]
            return if (currMonth == 0) {
                "January $curDay, $currYear"
            } else if (currMonth == 1) {
                "February $curDay, $currYear"
            } else if (currMonth == 2) {
                "March $curDay, $currYear"
            } else if (currMonth == 3) {
                "April $curDay, $currYear"
            } else if (currMonth == 4) {
                "May $curDay, $currYear"
            } else if (currMonth == 5) {
                "June $curDay, $currYear"
            } else if (currMonth == 6) {
                "July $curDay, $currYear"
            } else if (currMonth == 7) {
                "August $curDay, $currYear"
            } else if (currMonth == 8) {
                "September $curDay, $currYear"
            } else if (currMonth == 9) {
                "October $curDay, $currYear"
            } else if (currMonth == 10) {
                "November $curDay, $currYear"
            } else if (currMonth == 11) {
                "December $curDay, $currYear"
            } else {
                ""
            }
        }

    fun getCurrentTime(): String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm")
         return formatter.format(time)
    }
    fun getDateTimeForAPI(dateFormatted: String?): String {
        val apiDate = Calendar.getInstance()
        try {
            val dateFormat = SimpleDateFormat(DATEFORMATDDMMYYYY)
            apiDate.time = dateFormat.parse(dateFormatted)
            val corrTime = Calendar.getInstance()
            apiDate[Calendar.HOUR_OF_DAY] = corrTime[Calendar.HOUR_OF_DAY]
            apiDate[Calendar.MINUTE] = corrTime[Calendar.MINUTE]
            apiDate[Calendar.SECOND] = corrTime[Calendar.SECOND]
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        //2014-03-15T21:04:43.162Z
        val dateFormat = SimpleDateFormat(DATEWITHTIME)
        return dateFormat.format(apiDate.time)
    }

    fun getDateForAPP(strDate: String?): String? {
        return if (strDate != null && !strDate.equals(
                "null",
                ignoreCase = true
            ) && strDate.trim { it <= ' ' }.length != 0
        ) {
            val sdf1 = SimpleDateFormat(DATEWITHTIME)
            val sdf2 = SimpleDateFormat(DATEFORMATDDMMYYYY2)
            try {
                val date = sdf1.parse(strDate)
                sdf2.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            ""
        }
    }

    fun getFormatDateAPI(str: String?): String? {
        return if (str != null && !str.equals(
                "null",
                ignoreCase = true
            ) && str.trim { it <= ' ' }.length != 0
        ) {
            val sdf1 = SimpleDateFormat(DATEFORMATDDMMYYYY)
            val sdf2 = SimpleDateFormat(DATEFORMATYYYYMMDD)
            try {
                val date = sdf1.parse(str)
                sdf2.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            ""
        }
    }

    fun sortData(list: ArrayList<HashMap<String, String>>, field: String?) {
        Collections.sort(list) { lhs: HashMap<String, String>, rhs: HashMap<String, String> ->
            lhs[field]!!
                .compareTo(rhs[field]!!)
        }
    }

    fun sortDataInvert(list: ArrayList<HashMap<String, String>>?, field: String?) {
        Collections.sort(list, { lhs, rhs ->
            rhs[field]!!.compareTo(
                lhs[field]!!
            )
        })
    }

    fun convertDate(date: String): String {
        val nDate = date.split("T".toRegex()).toTypedArray()
        val mDate = nDate[0]
        val oDate = mDate.split("-".toRegex()).toTypedArray()
        return oDate[2] + "/" + oDate[1] + "/" + oDate[0]
    }

    fun addDaysToDate(date: String, numDays: Int): String {
        val arrDate = date.split("/".toRegex()).toTypedArray()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = arrDate[0].toInt()
        cal[Calendar.MONTH] = arrDate[1].toInt() - 1
        cal[Calendar.YEAR] = arrDate[2].toInt()
        cal.add(Calendar.DAY_OF_MONTH, numDays)
        return sdf.format(cal.time)
    }

    fun getMonthYear(date: String): String {
        val c = Calendar.getInstance()
        val currMonth = date.split("/".toRegex()).toTypedArray()[1].toInt() - 1
        val currYear = date.split("/".toRegex()).toTypedArray()[2].toInt()
        val curDay = date.split("/".toRegex()).toTypedArray()[0].toInt()
        return if (currMonth == 0) {
            "January, $currYear"
        } else if (currMonth == 1) {
            "February, $currYear"
        } else if (currMonth == 2) {
            "March, $currYear"
        } else if (currMonth == 3) {
            "April, $currYear"
        } else if (currMonth == 4) {
            "May, $currYear"
        } else if (currMonth == 5) {
            "June, $currYear"
        } else if (currMonth == 6) {
            "July, $currYear"
        } else if (currMonth == 7) {
            "August, $currYear"
        } else if (currMonth == 8) {
            "September, $currYear"
        } else if (currMonth == 9) {
            "October, $currYear"
        } else if (currMonth == 10) {
            "November, $currYear"
        } else if (currMonth == 11) {
            "December, $currYear"
        } else {
            ""
        }
    }

    fun getMonthYearShort(date: String): String {
        val c = Calendar.getInstance()
        val currMonth = date.split("/".toRegex()).toTypedArray()[1].toInt() - 1
        val currYear = date.split("/".toRegex()).toTypedArray()[2].toInt()
        val curDay = date.split("/".toRegex()).toTypedArray()[0].toInt()
        return if (currMonth == 0) {
            "Jan. $currYear"
        } else if (currMonth == 1) {
            "February, $currYear"
        } else if (currMonth == 2) {
            "Mar. $currYear"
        } else if (currMonth == 3) {
            "Apr. $currYear"
        } else if (currMonth == 4) {
            "May. $currYear"
        } else if (currMonth == 5) {
            "Jun. $currYear"
        } else if (currMonth == 6) {
            "Jul. $currYear"
        } else if (currMonth == 7) {
            "Aug. $currYear"
        } else if (currMonth == 8) {
            "Sep. $currYear"
        } else if (currMonth == 9) {
            "Oct. $currYear"
        } else if (currMonth == 10) {
            "Nov. $currYear"
        } else if (currMonth == 11) {
            "Dec. $currYear"
        } else {
            ""
        }
    }

    fun numberToPosition(num : Int) : String{
        return  when{
            num % 100 in 11..13 -> "${num}th"  //For ending with 11, 12, 13
            num % 10 == 1 -> "${num}st"
            num % 10 == 2 -> "${num}nd"
            num % 10 == 3 -> "${num}rd"
            else -> "${num}th"
        }
    }

    fun sortNumerically(arrayList: ArrayList<HashMap<String, String>>, key:String){
        val comparator = Comparator<HashMap<String, String>> { map1, map2 ->
            val value1 = map1[key]?.toIntOrNull() ?: 0
            val value2 = map2[key]?.toIntOrNull() ?: 0
            value1 - value2 // Compare numerically based on the "number" key's value
        }

        Collections.sort(arrayList, comparator)
    }

    fun sortNumericallyReverse(arrayList: ArrayList<HashMap<String, String>>, key: String) {
        val comparator = Comparator<HashMap<String, String>> { map1, map2 ->
            val value1 = map1[key]?.toIntOrNull() ?: 0
            val value2 = map2[key]?.toIntOrNull() ?: 0
            value2 - value1 // Compare numerically in descending order based on the specified key's value
        }

        Collections.sort(arrayList, comparator)
    }

    fun getDayOFWeek(dDate: String): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = dDate.split("/".toRegex()).toTypedArray()[0].toInt()
        calendar[Calendar.MONTH] = dDate.split("/".toRegex()).toTypedArray()[1].toInt() - 1
        calendar[Calendar.YEAR] = dDate.split("/".toRegex()).toTypedArray()[2].toInt()
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).uppercase(
            Locale.getDefault()
        )
    }

    fun getCompany(company: String?, limit: String): String {
        var newCompany = "Alpha"
        if (limit == "Charlie") {
            when (company) {
                "None" -> newCompany = "Alpha"
                "Alpha" -> newCompany = "Bravo"
                "Bravo" -> newCompany = "Charlie"
                "Charlie" -> newCompany = "Alpha"
            }
        } else if (limit == "Delta") {
            when (company) {
                "None" -> newCompany = "Alpha"
                "Alpha" -> newCompany = "Bravo"
                "Bravo" -> newCompany = "Charlie"
                "Charlie" -> newCompany = "Delta"
                "Delta" -> newCompany = "Alpha"
            }
        } else if (limit == "Echo") {
            when (company) {
                "None" -> newCompany = "Alpha"
                "Alpha" -> newCompany = "Bravo"
                "Bravo" -> newCompany = "Charlie"
                "Charlie" -> newCompany = "Alpha"
                "Delta" -> newCompany = "Echo"
                "Echo" -> newCompany = "Alpha"
            }
        } else if (limit == "Foxtrot") {
            when (company) {
                "None" -> newCompany = "Alpha"
                "Alpha" -> newCompany = "Bravo"
                "Bravo" -> newCompany = "Charlie"
                "Charlie" -> newCompany = "Delta"
                "Delta" -> newCompany = "Echo"
                "Echo" -> newCompany = "Foxtrot"
                "Foxtrot" -> newCompany = "Alpha"
            }
        } else if (limit == "Gulf") {
            when (company) {
                "None" -> newCompany = "Alpha"
                "Alpha" -> newCompany = "Bravo"
                "Bravo" -> newCompany = "Charlie"
                "Charlie" -> newCompany = "Delta"
                "Delta" -> newCompany = "Echo"
                "Echo" -> newCompany = "Foxtrot"
                "Foxtrot" -> newCompany = "Gulf"
                "Gulf" -> newCompany = "Alpha"
            }
        }
        return newCompany
    }

    fun getAgeAlt(dob: String): Int {
        val arrYear = dob.split("/".toRegex()).toTypedArray()
        val currYear = currentDateFormat2.split("/".toRegex()).toTypedArray()
        var newYear = currYear[2].toInt() - arrYear[2].toInt()
        if (arrYear[1].toInt() > currYear[1].toInt()) {
            newYear = newYear - 1
        } else {
            if (arrYear[1].toInt() == currYear[1].toInt()) {
                if (arrYear[0].toInt() > currYear[0].toInt()) {
                    newYear = newYear - 1
                }
            }
        }
        return newYear
    }

    fun getAge(dobString: String?): Int {
        var date: Date? = null
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        try {
            date = sdf.parse(dobString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date == null) return 0
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.time = date
        val year = dob[Calendar.YEAR]
        val month = dob[Calendar.MONTH]
        val day = dob[Calendar.DAY_OF_MONTH]
        dob[year, month + 1] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age + 1
    }

    fun getDateAfterNumberOfDays(strDate: String?, num: Int): String {
        if (strDate == null || strDate.equals(
                "null",
                ignoreCase = true
            ) || strDate.trim { it <= ' ' }.length == 0
        ) {
            return ""
        }
        try {
            val sdf = SimpleDateFormat(DATEFORMATYYYYMMDD)
            val calendar = Calendar.getInstance()
            calendar.time = sdf.parse(strDate)
            calendar.add(Calendar.DATE, -num)
            val resultdate = Date(calendar.timeInMillis)
            return sdf.format(resultdate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun addMonth(date: Date?, i: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.MONTH, i)
        return cal.time
    }

    fun getCountOfDays(createdDateString: String?, expireDateString: String?): Int {
        val dateFormat = SimpleDateFormat("dd/mm/yyyy")
        var createdConvertedDate: Date? = null
        var expireCovertedDate: Date? = null
        try {
            createdConvertedDate = dateFormat.parse(createdDateString)
            expireCovertedDate = dateFormat.parse(expireDateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val start: Calendar = GregorianCalendar()
        start.time = createdConvertedDate
        val end: Calendar = GregorianCalendar()
        end.time = expireCovertedDate
        val diff = end.timeInMillis - start.timeInMillis
        val dayCount = diff.toFloat() / (24 * 60 * 60 * 1000)
        return dayCount.toInt()
    }

    fun getCountOfDay(createdDateString: String?, expireDateString: String?): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var createdConvertedDate: Date? = null
        var expireCovertedDate: Date? = null
        var todayWithZeroTime: Date? = null
        try {
            createdConvertedDate = dateFormat.parse(createdDateString)
            expireCovertedDate = dateFormat.parse(expireDateString)
            val today = Date()
            todayWithZeroTime = dateFormat.parse(dateFormat.format(today))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var cYear = 0
        var cMonth = 0
        var cDay = 0
        if (createdConvertedDate!!.after(todayWithZeroTime)) {
            val cCal = Calendar.getInstance()
            cCal.time = createdConvertedDate
            cYear = cCal[Calendar.YEAR]
            cMonth = cCal[Calendar.MONTH]
            cDay = cCal[Calendar.DAY_OF_MONTH]
        } else {
            val cCal = Calendar.getInstance()
            cCal.time = todayWithZeroTime
            cYear = cCal[Calendar.YEAR]
            cMonth = cCal[Calendar.MONTH]
            cDay = cCal[Calendar.DAY_OF_MONTH]
        }


        /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */
        val eCal = Calendar.getInstance()
        eCal.time = expireCovertedDate
        val eYear = eCal[Calendar.YEAR]
        val eMonth = eCal[Calendar.MONTH]
        val eDay = eCal[Calendar.DAY_OF_MONTH]
        val date1 = Calendar.getInstance()
        val date2 = Calendar.getInstance()
        date1.clear()
        date1[cYear, cMonth] = cDay
        date2.clear()
        date2[eYear, eMonth] = eDay
        val diff = date2.timeInMillis - date1.timeInMillis
        val dayCount = diff.toFloat() / (24 * 60 * 60 * 1000)
        return "" + dayCount.toInt()
    }

    fun showAlert(activity: Activity?, title: String?, message: String?) {
        val alert = AlertDialog.Builder(activity)
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setCancelable(false)
        alert.show()
    }

    val regions: List<String>
        get() {
            val listRegions: MutableList<String> = ArrayList()
            listRegions.add("Select your Current region")
            listRegions.add("Ahafo")
            listRegions.add("Ashanti")
            listRegions.add("Bono")
            listRegions.add("Bono East")
            listRegions.add("Central")
            listRegions.add("Eastern")
            listRegions.add("Greater Accra")
            listRegions.add("North East")
            listRegions.add("Northern")
            listRegions.add("Oti")
            listRegions.add("Savannah")
            listRegions.add("Upper East")
            listRegions.add("Upper West")
            listRegions.add("Volta")
            listRegions.add("Western")
            listRegions.add("Western North")
            return listRegions
        }
    val regionsHome: List<String>
        get() {
            val listRegions: MutableList<String> = ArrayList()
            listRegions.add("Select your Home region")
            listRegions.add("Ahafo")
            listRegions.add("Ashanti")
            listRegions.add("Bono")
            listRegions.add("Bono East")
            listRegions.add("Central")
            listRegions.add("Eastern")
            listRegions.add("Greater Accra")
            listRegions.add("North East")
            listRegions.add("Northern")
            listRegions.add("Oti")
            listRegions.add("Savannah")
            listRegions.add("Upper East")
            listRegions.add("Upper West")
            listRegions.add("Volta")
            listRegions.add("Western")
            listRegions.add("Western North")
            return listRegions
        }
    val gender: List<String>
        get() {
            val list: MutableList<String> = ArrayList()
            list.add("Select Gender")
            list.add("Female")
            list.add("Male")
            return list
        }

    val grade: List<String>
        get() {
            val list: MutableList<String> = ArrayList()
            list.add("Select Grade")
            list.add("SHS 1")
            list.add("SHS 2")
            list.add("SHS 3")
            list.add("Completed")
            list.add("Teacher")

            return list
        }

    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    fun randomStringByKotlinRandom(limit: Int) = (1..limit)
        .map { kotlin.random.Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")

    fun timeStamp() = System.currentTimeMillis().toString()

    val countryies: Array<String>
        get() = arrayOf(
            "Select Country",
            "Ghana",
            "Afghanistan",
            "Albania",
            "Algeria",
            "Andorra",
            "Angola",
            "Antarctica",
            "Antigua and Barbuda",
            "Argentina",
            "Armenia",
            "Australia",
            "Austria",
            "Azerbaijan",
            "Bahamas",
            "Bahrain",
            "Bangladesh",
            "Barbados",
            "Belarus",
            "Belgium",
            "Belize",
            "Benin",
            "Bermuda",
            "Bhutan",
            "Bolivia",
            "Bosnia and Herzegovina",
            "Botswana",
            "Brazil",
            "Brunei",
            "Bulgaria",
            "Burkina Faso",
            "Burma",
            "Burundi",
            "Cambodia",
            "Cameroon",
            "Canada",
            "Cape Verde",
            "Central African Republic",
            "Chad",
            "Chile",
            "China",
            "Colombia",
            "Comoros",
            "Congo, Democratic Republic",
            "Congo, Republic of the",
            "Costa Rica",
            "Cote d'Ivoire",
            "Croatia",
            "Cuba",
            "Cyprus",
            "Czech Republic",
            "Denmark",
            "Djibouti",
            "Dominica",
            "Dominican Republic",
            "East Timor",
            "Ecuador",
            "Egypt",
            "El Salvador",
            "Equatorial Guinea",
            "Eritrea",
            "Estonia",
            "Ethiopia",
            "Fiji",
            "Finland",
            "France",
            "Gabon",
            "Gambia",
            "Georgia",
            "Germany",
            "Greece",
            "Greenland",
            "Grenada",
            "Guatemala",
            "Guinea",
            "Guinea-Bissau",
            "Guyana",
            "Haiti",
            "Honduras",
            "Hong Kong",
            "Hungary",
            "Iceland",
            "India",
            "Indonesia",
            "Iran",
            "Iraq",
            "Ireland",
            "Israel",
            "Italy",
            "Jamaica",
            "Japan",
            "Jordan",
            "Kazakhstan",
            "Kenya",
            "Kiribati",
            "Korea, North",
            "Korea, South",
            "Kuwait",
            "Kyrgyzstan",
            "Laos",
            "Latvia",
            "Lebanon",
            "Lesotho",
            "Liberia",
            "Libya",
            "Liechtenstein",
            "Lithuania",
            "Luxembourg",
            "Macedonia",
            "Madagascar",
            "Malawi",
            "Malaysia",
            "Maldives",
            "Mali",
            "Malta",
            "Marshall Islands",
            "Mauritania",
            "Mauritius",
            "Mexico",
            "Micronesia",
            "Moldova",
            "Mongolia",
            "Morocco",
            "Monaco",
            "Mozambique",
            "Namibia",
            "Nauru",
            "Nepal",
            "Netherlands",
            "New Zealand",
            "Nicaragua",
            "Niger",
            "Nigeria",
            "Norway",
            "Oman",
            "Pakistan",
            "Panama",
            "Papua New Guinea",
            "Paraguay",
            "Peru",
            "Philippines",
            "Poland",
            "Portugal",
            "Qatar",
            "Romania",
            "Russia",
            "Rwanda",
            "Samoa",
            "San Marino",
            " Sao Tome",
            "Saudi Arabia",
            "Senegal",
            "Serbia and Montenegro",
            "Seychelles",
            "Sierra Leone",
            "Singapore",
            "Slovakia",
            "Slovenia",
            "Solomon Islands",
            "Somalia",
            "South Africa",
            "Spain",
            "Sri Lanka",
            "Sudan",
            "Suriname",
            "Swaziland",
            "Sweden",
            "Switzerland",
            "Syria",
            "Taiwan",
            "Tajikistan",
            "Tanzania",
            "Thailand",
            "Togo",
            "Tonga",
            "Trinidad and Tobago",
            "Tunisia",
            "Turkey",
            "Turkmenistan",
            "Uganda",
            "Ukraine",
            "United Arab Emirates",
            "United Kingdom",
            "United States",
            "Uruguay",
            "Uzbekistan",
            "Vanuatu",
            "Venezuela",
            "Vietnam",
            "Yemen",
            "Zambia",
            "Zimbabwe"
        )
    val netWorks: Array<String>
        get() = arrayOf("Select Network", "MTN", "Vodafone", "AirtelTigo")
    val netWorksVal: Array<String>
        get() = arrayOf("", "mtn", "vod", "tgo")

    val getObjTopics:MutableList<String>
        get() = mutableListOf("Historiography and Historical Skills", "Trans – Saharan Trade",
    "Islam in West Africa", "European Contact with West Africa", "Trans – Atlantic slave trade",
    "Christians Missionary Activities in West Africa",
    "The Scramble for the Partition of West Africa",
    "Colonial Rule in west Africa",
    "Problems of Independent West Africa States",
    "West Africa and International Organizations")

    val getSectBTopics:MutableList<String>
        get() = mutableListOf("Historiography and Historical Skills",
                "Pre-History of Africa",
                "The Civilization of Ancient Egypt",
                "The Civilization of North Africa Berbers",
                "The Civilization of Axum Ancient Ethiopia",
                "The Civilization of The Bantu",
                "The Civilization of East Africa Swahili",
            "Trans – Saharan Trade", "The Civilization of West African Sudanese States",
    "History of Ghana", "Pre-History of Ghana",
    "Peopling of Ghana: Origin and Rise of States and Kingdoms",
    "Social and Political systems of Pre-Colonial Ghana",
    "History of Medicine", "History of Arts and Technology Visual Art",
    "History of the Economy of Ghana up to 1900",
    "European Contact with West Africa",
    "The Trans – Atlantic slave trade",
    "Christian Missionary Activities in West Africa Ghana",
    "The Scramble for the Partition of West Africa",
    "Colonial Rule and Nationalism in west Africa",
    "Problems of Independent and After Nkrumah's Era",
    "Ghana Under Nkrumah's Regime",
    "Ghana in the Community of Nations")

    fun getTopics(type : String) : MutableList<String>{
        if(type == "Objectives"){
            return mutableListOf("Historiography and Historical Skills", "Trans – Saharan Trade",
                "Islam in West Africa", "European Contact with West Africa", "Trans – Atlantic slave trade",
                "Christians Missionary Activities in West Africa",
                "The Scramble for the Partition of West Africa",
                "Colonial Rule in west Africa",
                "Problems of Independent West Africa States",
                "West Africa and International Organizations")
        }else if(type == "Section B"){
            return mutableListOf("Historiography and Historical Skills", "Pre-History of Africa",
                "The Civilization of Ancient Egypt",
                "The Civilization of North Africa (Berbers)",
                "The Civilization of Axum (Ancient Ethiopia)",
                "The Origin and Spread of Bantu Civilization",
                "Swahili Civilization of East African Coast",
                "Trans – Saharan Trade",
                "Islam in West Africa",
                "The Civilization of West African Sudanese States",
                "The Civilization of West African Coastal States and Forest States",
                "Introduction to History of Ghana",
                "Pre-History of Ghana",
                "Peopling of Ghana: Origin and Rise of States and Kingdoms",
                "Social and Political systems of Pre-Colonial Ghana",
                "History of Medicine",
                "History of Arts and Technology (Visual Art)",
                "History of the Economy of Ghana up to 1900",
                "European Contact with West Africa",
                "The Trans – Atlantic Slave Trade",
                "Christian Missionary Activities in West Africa (Ghana)",
                "The Scramble for And Partition of West Africa (Ghana)",
                "Colonial Rule in West Africa",
                "Nationalism in West Africa (Ghana)",
                "Independence and After :  Nkrumah's Era",
                "Ghana After Nkrumah's Regime",
                "Problems Of Independent West Africa",
                "Ghana in the Community of Nations")
        }else if(type == "WASSCE Textbook") {
            return mutableListOf("Historiography and Historical Skills",
                    "Trans – Saharan Trade",
                    "Islam in West Africa",
                    "European Contact with West Africa",
                    "Trans – Atlantic slave trade",
                    "Christian Missionary Activities in West Africa",
                    "The Scramble for the Partition of West Africa",
                "Colonial Rule in West Africa",
                "Problems of Independent West Africa States",
                "West Africa and International Organizations")
        }else if(type == "Quiz"){
            return mutableListOf("Objective Quiz", "Word Scramble", "Missing Letters", "Define Quiz", "Horde")
        }else
        {
            return mutableListOf()

        }
    }

    fun getGrade(mark : Int) : String{
        if(mark>=80){
            return "A"
        }else if(mark>=70){
            return "B2"
        }else if(mark>=65){
            return "B3"
        }else if(mark>=60){
            return "C4"
        }else if(mark>=55){
            return "C5"
        }else if(mark>=50){
            return "C6"
        }else if(mark>=45){
            return "D7"
        }else if(mark>=40){
            return "E8"
        }else {
            return "F9"
        }
    }



    fun boldText(inputString: String, textView: TextView){
        // Create a SpannableString from the complete string
        val regex = "\\*(.*?)\\*".toRegex()
        val matches = regex.findAll(inputString)

        var num = 0
        val spannableBuilder = SpannableStringBuilder(inputString)
        for (match in matches) {
            val boldStyle = StyleSpan(Typeface.BOLD)
            val start = match.range.start + 1-num // Skip the initial asterisk
            val end = match.range.endInclusive-num // Include the closing asterisk
            spannableBuilder.setSpan(boldStyle, start, end, SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE)


            spannableBuilder.replace(start- 1, start, "")
            spannableBuilder.replace(end - 1, end, "")
            num +=2
        }

// Set the modified SpannableStringBuilder to the TextView
        textView.text = spannableBuilder
    }



    fun showKonfet(konfettiView: KonfettiView) {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )
        konfettiView.start(party)
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileExtension = getFileExtension(context.contentResolver, uri)
            ?: // Handle if the file extension cannot be determined
            return null

        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.$fileExtension")

        return try {
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Helper function to get file extension from Uri
    fun getFileExtension(contentResolver: ContentResolver, uri: Uri): String? {
        val mimeType = contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    fun deleteFile(file: File) {
        if (file.exists()) {
            file.delete()
            println("successfully deleted ${file}")
        }
    }

    fun getLastPart(name:String, split: String) : String{
        val lPart = name.split(split)
        return lPart[lPart.size-1]

    }

    fun getFileSize(context: Context, uri: Uri): Long {
        val contentResolver: ContentResolver = context.contentResolver
        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri)
            return inputStream?.available()?.toLong() ?: 0L
        } catch (e: IOException) {
            e.printStackTrace()
            return 0L
        } finally {
            inputStream?.close()
        }
    }

    fun isTodayOrWithinWeekOrMonth(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = dateFormat.parse(dateString)
        val now = Date()

        val today = Calendar.getInstance()
        today.time = now

        val givenDate = Calendar.getInstance()
        givenDate.time = date

        return when {
            givenDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    givenDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "day"
            isWithinWeek(givenDate, today) -> "week"
            isWithinMonth(givenDate, today) -> "month"
            else -> "Not within this week or month"
        }
    }

    fun checkIfWithinDayWeekMonth(inputString: String, type: String): Boolean{
        //2024-01-03T20:01:07.000000Z
        val split = inputString.split("T")[0]
        var grp = inputString.split("-")
        val specificDate = split

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Calendar.getInstance().time
        val today = dateFormat.format(currentDate)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val specificCalendar = Calendar.getInstance().apply {
            time = sdf.parse(specificDate) ?: Date()
        }

        if(type=="day"){

            return isSameDay(today, specificDate)
        }else if(type=="week"){

            return isSameWeek(today, specificDate)
        }else if (type=="month"){

            return isSameMonth(today, specificDate)
        }

        return false
    }

    private fun isSameDay(cal1: String, cal2: String): Boolean {
        return cal1 == cal2
    }

    fun isSameWeek(date1: String, date2: String): Boolean {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val date1 = dateFormat.parse(date1) ?: Date()
        val date2 = dateFormat.parse(date2) ?: Date()


        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()

        cal1.time = date1
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    }

    fun isSameMonth(date1: String, date2: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val date1 = dateFormat.parse(date1) ?: Date()
        val date2 = dateFormat.parse(date2) ?: Date()


        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()

        cal1.time = date1
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }

    fun isWithinWeek(date1: Calendar, date2: Calendar): Boolean {
        val daysDiff = date2.get(Calendar.DAY_OF_WEEK) - date1.get(Calendar.DAY_OF_WEEK)
        return daysDiff in 0..6 && isWithinDays(date1, date2, 7)
    }

    fun isWithinMonth(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) && isWithinDays(date1, date2, 30)
    }

    fun isWithinDays(date1: Calendar, date2: Calendar, days: Int): Boolean {
        val diffDays = Math.abs(date1.timeInMillis - date2.timeInMillis) / (24 * 60 * 60 * 1000)
        return diffDays <= days
    }

    fun runSwipe(swipeRefreshLayout: SwipeRefreshLayout, cases: () -> Unit){
        swipeRefreshLayout.setOnRefreshListener {
            // Perform your action here, like fetching new data from a source
            // When the action is complete, call setRefreshing(false) to stop the refreshing animation
            cases()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    fun moveButton(binding : View, frame : View){
        var isDragging = false
        var  parentWidth = 0
        var parentHeight = 0
        var offsetX = 0
        var offsetY = 0
        frame.post {
            parentWidth = frame.width
            parentHeight = frame.height
        }
        binding.setOnTouchListener { view, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    offsetX = event.rawX.toInt() - binding.x.toInt()
                    offsetY = event.rawY.toInt() - binding.y.toInt()
                    isDragging = false
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    isDragging = true
                    if (isDragging) {
                        var newX = event.rawX.toInt() - offsetX
                        var newY = event.rawY.toInt() - offsetY

                        newX = Math.max(0, Math.min(newX, parentWidth - binding.width));
                        newY = Math.max(0, Math.min(newY, parentHeight - binding.height));

                        binding.x = newX.toFloat()
                        binding.y = newY.toFloat()
                    }
                    isDragging
                }
                MotionEvent.ACTION_UP -> {
                    // isDragging = false
//                    val deltaX = Math.abs(event.rawX - initialX)
//                    val deltaY = Math.abs(event.rawY - initialY)
//                    if (deltaX < CLICK_THRESHOLD && deltaY < CLICK_THRESHOLD) {
//                        // If the movement is within a threshold, treat it as a click
//                        fab.performClick()
//                    }
                    isDragging
                }
                else -> {

                    false
                }
            }
        }
    }

    fun blinkCardView(cardView: CardView, activity: Activity) {
        val animator = ObjectAnimator.ofArgb(
            cardView,
            "cardBackgroundColor",
            ContextCompat.getColor(activity, R.color.primary),
            ContextCompat.getColor(activity, android.R.color.white)
        )
        animator.duration = 500
        animator.repeatCount = 3 // Number of blinks
        animator.start()
    }

    fun generateStringWithTimestamp(): String {
        val randomPart = (100000L + (Math.random() * 999999L - 100000L)).toLong()

        // Get the current timestamp
        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())

        // Concatenate the random part and timestamp
        return "E$randomPart$timestamp"
    }

    fun showCal(edt : EditText, context: Context){
        // on below line we are getting
        // the instance of our calendar.
        val c = Calendar.getInstance()

        // on below line we are getting
        // our day, month and year.
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            context,
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our edit text.
                val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                edt.setText(dat)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        // at last we are calling show
        // to display our date picker dialog.
        datePickerDialog.show()
    }

    fun reverseDate(date: String, split: String, newSplit: String): String {
        return try {
            "${date.split(split)[2]}$newSplit${date.split(split)[1]}$newSplit${date.split(split)[0]}"
        }catch (e : Exception){
            e.printStackTrace()
            date
        }

    }

    fun convertDateFormat(inputDate: String): String {
        // Parse the input string to Instant
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = Instant.parse(inputDate)

            // Convert Instant to LocalDateTime in UTC
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)

            // Format LocalDateTime to the desired output format
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' h:mma")
            val formattedDate = localDateTime.format(formatter)

            return formattedDate
        }else{
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy 'at' h:mma", Locale.US)

            return try {
                // Parse input date string
                val date = inputFormat.parse(inputDate)

                // Format date to the desired output format
                outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                ""
            }
        }
    }

    fun convertDateFormat2(inputDate: String): String {
        // Define input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy 'at' h:mma", Locale.US)

        return try {
            // Parse input date string
            val date = inputFormat.parse(inputDate)

            // Format date to the desired output format
            outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun convertDateFormat3(inputDate: String): String {
        // Define input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        return try {
            // Parse input date string
            val date = inputFormat.parse(inputDate)

            // Format date to the desired output format
            outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""

        }
    }

    fun extractAndCountCreatedDates(jsonData: String): Map<String, Int> {
        val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
        val dateFormatOutput = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        val jsonArray = JSONArray(jsonData)

        val dateOccurrences = mutableMapOf<String, Int>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val createdAt = jsonObject.optString("created_at", "")

            try {
                val date = dateFormatInput.parse(createdAt)
                val formattedDate = dateFormatOutput.format(date)

                if (dateOccurrences.containsKey(formattedDate)) {
                    dateOccurrences[formattedDate] = dateOccurrences[formattedDate]!! + 1
                } else {
                    dateOccurrences[formattedDate] = 1
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val sortedByDate = dateOccurrences.toList().sortedBy { it.first }.toMap()

        return sortedByDate
    }

    fun convertForSort(dateString: String):String{


        // Define the date pattern
        val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"

        // Create a SimpleDateFormat with the defined pattern
        val sdf = SimpleDateFormat(pattern)

        // Set the time zone to UTC
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            // Parse the date string
            val date = sdf.parse(dateString)

            // Convert Date to timestamp (milliseconds since epoch)
            val timestamp = date?.time

            timestamp?.toString() ?: ""
        } catch (e: Exception) {
            ""
        }

    }



}