package com.dawolf.yea.resources

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.viewpager.widget.PagerAdapter


class SlideAdapter(requireContext: Context, uris: ArrayList<Uri>) : PagerAdapter(){

    private var context: Context? = null
   // private var color: ArrayList<Int>? = null
    //private var colorName: List<String>? = null
    private  lateinit var feature: MutableList<String?>
    private lateinit var myBackground: List<Int>
    private lateinit var myClasses: List<Class<*>>
    private lateinit var storage: Storage
    private var uris : ArrayList<Uri> = ArrayList()


    init {
        this.context = requireContext
        this.uris = uris
    }

//    fun SlideAdapter(
//        context: Context?,
//        color: List<Int>?
////        colorName: List<String>?,
////        backgrounds: List<Int>?,
////        classes: List<Class<*>>?,
////        features: MutableList<String?>
//    ) {
//        this.context = context
//        this.color = color
////        this.colorName = colorName
////        myBackground = backgrounds!!
////        myClasses = classes!!
////        this.feature = features
//    }

    override fun getCount(): Int {
        return uris.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view: View = inflater.inflate(R.layout.item_slider, null)
//        val pic = view.findViewById<ImageView>(R.id.imgSlide)
//        val linearLayout = view.findViewById<LinearLayout>(R.id.linLayout)
//        storage = Storage(context!!)
//        linearLayout.setBackgroundResource(R.color.white)
//      //  textView.text = colorName!![position]
//       // linearLayout.setBackgroundColor(color!!.get(position));
//       // linearLayout.setBackgroundResource(myBackground[position])
//        linearLayout.setOnClickListener {
////            storage.fEATURE = (feature.get(position))
////            val intent = Intent(context, myClasses[position])
////            context!!.startActivity(intent)
//        }
//        Picasso.get().load(uris[position]).fit().into(pic)
//        val viewPager: ViewPager = container as ViewPager
//        viewPager.addView(view, 0)
//        return view
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        val viewPager: ViewPager = container as ViewPager
//        val view = `object` as View?
//        viewPager.removeView(view)
//    }


}