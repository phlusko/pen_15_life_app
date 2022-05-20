package com.p4u1.pen15lifecounter

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.p4u1.pen15lifecounter.databinding.FragmentFirstBinding
import org.json.JSONArray
import org.json.JSONException
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var games : ArrayList<Int> = arrayListOf()
    private var players : HashMap<Int, String> = hashMapOf()
    private var selectedGame = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.btnPing.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, "https://pen15-life-counter.herokuapp.com/api/v1/ping",
                { response ->
                    Toast.makeText(
                        requireContext(), "Pong",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                {
                    Toast.makeText(
                        requireContext(), "Uh Oh! " + it.message,
                        Toast.LENGTH_SHORT
                    ).show() })
            queue.add(stringRequest)
        }
        binding.btnGetPlayerList.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, "https://pen15-life-counter.herokuapp.com/api/v1/player",
                { response ->
                    Toast.makeText(
                        requireContext(), "Player List loaded!",
                        Toast.LENGTH_SHORT
                    ).show()
                    try {
                        val jsonArray = JSONArray(response)
                        var list = ""
                        players.clear()
                        for (i in 0 .. jsonArray.length() - 1) {
                            val current = jsonArray.getJSONObject(i)
                            players.put(current.getInt("id"), current.getString("screenName"))
                        }
                        val playerArray : ArrayList<String> = arrayListOf()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            players.forEach { i, s ->
                                playerArray.add(s)
                                list += s + "\n"
                            }
                        }
                        var adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item,playerArray)
                        binding.spinnerLifePlayer.adapter = adapter
                        binding
                    } catch (e : JSONException) {
                        Toast.makeText(
                            requireContext(), "json error: " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                {
                    Toast.makeText(
                        requireContext(), "Uh Oh! " + it.message,
                        Toast.LENGTH_SHORT
                    ).show() })
            queue.add(stringRequest)
        }

        binding.btnGetGameList.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val stringRequest = StringRequest(
                Request.Method.GET, "https://pen15-life-counter.herokuapp.com/api/v1/game",
                { response ->
                    Toast.makeText(
                        requireContext(), "Game List loaded! ",
                        Toast.LENGTH_SHORT
                    ).show()
                    try {
                        val jsonArray =  JSONArray(response)
                        var list = ""
                        games.clear()
                        for (i in 0 .. jsonArray.length() - 1) {
                            val current = jsonArray.getJSONObject(i)
                            list += "%d\n%s\nstart: %s\nend: %s\n".format(Locale.getDefault(),
                                current.getInt("id"),
                                if(current.getBoolean("active")) "active" else "inactive",
                                current.getString("start"),
                                current.getString("end")
                            )
                            games.add(current.getInt("id"))
                            val playersArray = current.getJSONArray("players")
                            if(playersArray.length() > 0){
                                list+= "players:\n"
                                for (j in 0..playersArray.length()-1) {
                                    val currentPlayer = playersArray.getJSONObject(j)
                                    list+="    - " + currentPlayer.getString("screenName") + "\n"
                                }
                            }
                            list+= "\n"
                        }
                        binding.textGameList.text = list
                        var adapter = ArrayAdapter<Int>(requireContext(), android.R.layout.simple_spinner_dropdown_item,games)
                        binding.spinnerGame.adapter = adapter
                        binding.spinnerGame.setOnItemSelectedListener(object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                selectedGame = games.get(p2)
                                binding.textLifeGameId.text = "Game ID: " + selectedGame
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        })

                    } catch( e: JSONException) {
                        Toast.makeText(
                            requireContext(), "json error: " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                {
                    Toast.makeText(
                        requireContext(), "Uh Oh! " + it.message,
                        Toast.LENGTH_SHORT
                    ).show() })
            queue.add(stringRequest)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}