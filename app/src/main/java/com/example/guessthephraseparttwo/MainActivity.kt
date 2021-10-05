package com.example.guessthephraseparttwo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var Phrase: TextView
    lateinit var Letter: TextView
    lateinit var UserInput: EditText
    lateinit var SubmitButton: Button
    lateinit var MyRV: RecyclerView
    lateinit var ConsLO: ConstraintLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Phrase = findViewById(R.id.phraseTV)
        Letter = findViewById(R.id.letterTV)
        UserInput = findViewById(R.id.UserInput)
        SubmitButton = findViewById(R.id.GuessBtn)
        MyRV = findViewById(R.id.MyRV)
        ConsLO = findViewById(R.id.ConsLO)

        //When the guess button pressed
        SubmitButton.setOnClickListener(){ButtonClicked()}

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
       var myMessage = sharedPreferences.getString("myMessage", "").toString()  // --> retrieves data from Shared Preferences
// We can save data with the following code
        with(sharedPreferences.edit()) {
            putString("myMessage", myMessage)
            apply()
        }

    }//end oncreate

    fun ButtonClicked(){

        var Chances = 10
        val results = mutableListOf<String>()
        val PhrasesList = listOf<String>(
            "hit the hay",
            "costs an arm and a leg",
            "break a leg",
            "better late than never",
            "rule of thumb",
        )

        //Phrase from words --> *****
        val RandomPhrase = PhrasesList[Random.nextInt(PhrasesList.size)]
        var StaredPhrase = Regex("[A-Za-z]").replace(RandomPhrase,"*")
        Phrase.text = StaredPhrase
        var EnteredPhrase = true

        //RV
        MyRV.adapter = RecyclerViewAdapter(results)
        MyRV.layoutManager = LinearLayoutManager(this)

        var input = UserInput.text.toString().toLowerCase()
        if(Phrase.text.contains("*")){
            if(input.isEmpty()){
                Snackbar.make(ConsLO, "This can not be empty! You must enter at least one letter", Snackbar.LENGTH_SHORT).show()
            }else{
                if(EnteredPhrase){
                    if(input == RandomPhrase){
                        results.add("Greet job")
                        Phrase.text = RandomPhrase
                        Phrase.textSize = 18f
                        SubmitButton.isClickable = false
                    }
                    else{
                        results.add("Wrong guess: $input")
                    }
                    UserInput.setText("")
                    UserInput.hint = "Guess a letter"
                    EnteredPhrase = false
                }else{
                    Letter.text = input[0].toString()
                    if(RandomPhrase.contains(input[0])){
                        var counter = 0
                        var phraseChar = Phrase.text.toString().toCharArray()
                        for(i in 0..RandomPhrase.length-1){
                            if(RandomPhrase[i] == input[0]) {
                                phraseChar[i] = input[0]
                                counter++
                            }
                        }
                        Phrase.text = String(phraseChar)
                        results.add("Found $counter $input(s)")
                    }
                    else{
                        results.add("Wrong guess: $input")
                        results.add("${--Chances} guesses remaining")
                    }
                    UserInput.setText("")
                    UserInput.hint = "Guess the full phrase"
                    EnteredPhrase = true
                }
            }
        } else{
            results.add("Greet job")
            Phrase.text = RandomPhrase
            Phrase.textSize = 15f
            SubmitButton.isClickable = false
        }

        MyRV.adapter = RecyclerViewAdapter(results)
        MyRV.layoutManager = LinearLayoutManager(this)
        MyRV.scrollToPosition(results.size - 1)

        if(Chances == 0){
            results.add("Game over")
            SubmitButton.isClickable = false
        }
    }//end ButtonClicked
}