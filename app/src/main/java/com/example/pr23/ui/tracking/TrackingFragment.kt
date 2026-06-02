package com.example.pr23.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pr23.R
import com.example.pr23.databinding.FragmentTrackingBinding
import com.example.pr23.model.RoutePoint
import com.example.pr23.utils.collectWhenStarted
import com.example.pr23.viewmodel.SharedViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch

class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }
    private var googleMap: GoogleMap? = null
    private var lastRoutePoints: List<RoutePoint> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            googleMap = map
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isMapToolbarEnabled = true
            renderRoute(lastRoutePoints)
        }

        collectWhenStarted {
            launch {
                viewModel.routePoints.collect { points ->
                    lastRoutePoints = points
                    renderRoute(points)
                }
            }
        }
    }

    private fun renderRoute(points: List<RoutePoint>) {
        val map = googleMap ?: return
        if (points.isEmpty()) return

        map.clear()

        val routeCoordinates = points.map { LatLng(it.latitude, it.longitude) }
        points.forEach { point ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(point.latitude, point.longitude))
                    .title(point.title)
            )
        }

        val routeColor = ContextCompat.getColor(requireContext(), R.color.delivery_green)
        map.addPolyline(
            PolylineOptions()
                .addAll(routeCoordinates)
                .color(routeColor)
                .width(10f)
                .geodesic(true)
        )

        val boundsBuilder = LatLngBounds.Builder()
        routeCoordinates.forEach { boundsBuilder.include(it) }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 96))

        binding.routeSummaryText.text = getString(
            R.string.route_summary_format,
            points.first().title,
            points.last().title
        )
    }

    override fun onDestroyView() {
        googleMap = null
        super.onDestroyView()
        _binding = null
    }
}
