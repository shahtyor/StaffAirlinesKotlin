package com.stukalov.staffairlines.pro.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    val RC_SIGN_IN: Int = 1
    private var _binding: FragmentLoginBinding? = null
    //private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //auth = FirebaseAuth.getInstance()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cvGoogleLogin = view.findViewById<CardView>(R.id.cvGoogleLogin)
        cvGoogleLogin.setOnClickListener()
        {
            login()
        }
    }

    private fun login() {
        val loginIntent: Intent = GlobalStuff.googleInClient!!.signInIntent
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    /*private fun googleFirebaseAuth(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                startActivity(LoggedInActivity.getLaunchIntent(GlobalStuff.activity))
            } else {
                Toast.makeText(GlobalStuff.activity, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }*/

    override fun onResume() {
        super.onResume()

        val event = GlobalStuff.GetBaseEvent("show login form", true, true)
        GlobalStuff.amplitude?.track(event)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}