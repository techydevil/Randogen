package com.androar.randogen

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var prefs: SharedPreferences? = null
    lateinit var  randomQuote : TextView
    lateinit var randomButton : TextView
    lateinit var episodeImage : ImageView
    lateinit var gradientImage : ImageView
    lateinit var episodeTitle : TextView
    lateinit var firstRunText : TextView
    lateinit var arrayList :List<Episode>
    var jsonString: String = ""
    var appTitles = arrayOf("Could you be watching any more episodes?!",
    "We're not good at advice, we have more episodes.",
    "Are they on a break?",
    "See? we're lobsters.",
    "Joey doesn't share food, do you?",
    "SEVEN!",
    "This is all a moo point.",
    "How you doin'?",
    "You don’t even have a 'pla'",
    "They don’t know that we know they know.")
    var errorMessages = arrayOf("Damn it chandler!",
            "Joey spilt sauce on our servers!",
            "fat Monica sat on our servers!",
            "Could your internet be any slower?!",
            "Ross divorced our servers",
            "Rachel spent our server money on lipstick",
            "Phoebe sings smelly internet",
            "Your internet is moo point.",
            "Our servers are ON A BREAK",
            "Could our servers be any more busy?!")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FullScreen
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        randomQuote = findViewById<TextView>(R.id.tvRandomQuote)
        randomButton = findViewById<TextView>(R.id.randombutton)
        episodeImage = findViewById<ImageView>(R.id.episodeImage)
        gradientImage = findViewById<ImageView>(R.id.gradientImage)
        episodeTitle = findViewById<TextView>(R.id.episodeTitle)
        firstRunText = findViewById<TextView>(R.id.firstRunText)

        //Arrays
        arrayList = generateEpisodes()

        randomButton.setOnClickListener {
            doRandom(232)
        }

        prefs = getSharedPreferences("com.androar.randogen", MODE_PRIVATE);

        if (prefs!!.getBoolean("firstrun", true)) {
            gradientImage.setImageResource(R.drawable.gradation_pink)
            randomButton.text = "Tap me! ;)"
            episodeTitle.visibility = View.INVISIBLE
            randomButton.setOnClickListener {
                randomButton.setText("Choose Next Random Episode")
                gradientImage.setImageResource(R.drawable.gradation_black)
                episodeTitle.visibility = View.VISIBLE
                firstRunText.visibility = View.INVISIBLE
                firstRunLottie.visibility = View.INVISIBLE
                doRandom(232)
            }
            prefs!!.edit().putBoolean("firstrun", false).apply()
        }

        else {
            firstRunText.visibility = View.INVISIBLE
            firstRunLottie.visibility = View.INVISIBLE
            episodeTitle.visibility = View.VISIBLE
        }


    }




    private fun generateEpisodes(): List<Episode> {
        val arrayList = ArrayList<Episode>()
        val retrofit = Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(Api::class.java)
        val call = api.episodes
        call.enqueue(object : Callback<List<Episode>> {
            override fun onResponse(call: Call<List<Episode>>, response: Response<List<Episode>>) {
                val episodeList = response.body()!!
                for (i in episodeList.indices) {
                    arrayList.add(Episode(i.toLong(), episodeList[i].t, episodeList[i].d, episodeList[i].i))
                }
            }

            override fun onFailure(call: Call<List<Episode>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
        return arrayList;
    }


    fun doRandom(max: Int) {

        var rnds = (0..max).random()
        val randomArray : ArrayList<Int> = arrayListOf()
        if (randomArray.size != max-1) {
            if (randomArray.contains(rnds)) {
                rnds = (0 .. max).random()
            }
            else {
                randomArray.add(rnds)
            }
        }

        val r = Random()
        randomQuote.setText(appTitles[r.nextInt(9 - 1)+ 1])


        try {
            Log.i("errors", arrayList.get(randomArray[0]).t)
            episodeTitle.setText(arrayList.get(randomArray[0]).t)

            val x = Random().nextInt(4);
            var resource = R.drawable.pic_014
            when (x) {
                0 ->
                    resource = R.drawable.pic_042
                1 ->
                    resource = R.drawable.pic_014
                2 ->
                    resource = R.drawable.pic_021
                3 ->
                    resource = R.drawable.pic_023
            }
            episodeImage.setImageResource(resource)
            episodeImage.scaleType = ImageView.ScaleType.CENTER_CROP

            episodeImage.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Plot")
                //set message for alert dialog
                builder.setMessage(arrayList.get(randomArray[0]).d)

                builder.setNegativeButton("Aight"){dialogInterface, which ->
                   dialogInterface.dismiss()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()

            }
        } catch (e: Exception) {
            Toast.makeText(this, errorMessages[r.nextInt(10 - 1)+ 1],Toast.LENGTH_LONG).show()
        }
    }

}