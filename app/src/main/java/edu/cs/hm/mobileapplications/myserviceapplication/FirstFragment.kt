package edu.cs.hm.mobileapplications.myserviceapplication

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import edu.cs.hm.mobileapplications.myserviceapplication.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    /** For Bound Service */
    private lateinit var mService: MyBoundService
    private var mBound: Boolean = false
    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to MyBoundService, cast the IBinder and get LocalService instance
            val binder = service as MyBoundService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonStartBackgroundService.setOnClickListener {

            Intent(context, MyBackgroundService::class.java).also { intent ->
                context?.startService(intent)
            }
        }

        binding.buttonStartForegroundService.setOnClickListener {

            Intent(context, MyForegroundService::class.java).also { intent ->
                context?.startForegroundService(intent)
            }
        }

        binding.buttonStartBoundService.setOnClickListener {
            Intent(context, MyBoundService::class.java).also { intent ->
                context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }

        binding.buttonBoundAction.setOnClickListener {
            if (mBound) {
                val num: Int = mService.randomNumber
                Toast.makeText(context, "number: $num", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No Service Bound!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        context?.unbindService(connection)
        mBound = false
    }
}