package com.cyrillrx.tracker;

import com.cyrillrx.tracker.event.TrackEvent
import com.cyrillrx.tracker.event.context.Connectivity
import com.cyrillrx.tracker.event.context.TrackerContext;
import com.cyrillrx.tracker.event.context.TrackingApp
import com.cyrillrx.tracker.event.context.TrackingDeviceFactory
import com.cyrillrx.tracker.event.context.TrackingUser
import com.cyrillrx.tracker.event.context.UnknownUser

/**
 * This class is the main entry point of the library.
 *
 * It wraps a list of [Tracker] implementations.
 * It is responsible for updating the events before passing them to the trackers (e.g. to add some context).
 * It also prevents a crash of the app if one of the trackers raises an exception.
 *
 * @author Cyril Leroux
 *         Created on 17/04/2015
 */
object Tracker {
    private val trackers = HashMap<String, TrackerChild>()

    val context: TrackerContext = TrackerContext(
        app = TrackingApp(),
        user = UnknownUser,
        device = TrackingDeviceFactory().create(),
        connectivity = Connectivity.UNKNOWN,
    )

    private var catchException: ((Throwable) -> Unit)? = null

    fun init(app: TrackingApp, catchException: (Throwable) -> Unit) {
        this.context.app = app
        this.catchException = catchException
    }

    // This function is used in the iOS project
    fun setupApp(app: TrackingApp) {
        context.app = app
    }

    // This function is used in the iOS project
    fun setupExceptionCatcher(catchException: (Throwable) -> Unit) {
        this.catchException = catchException
    }

    fun hasChild(name: String): Boolean = trackers.containsKey(name)

    fun applyConsent(hasConsent: Boolean, tracker: TrackerChild) {
        val trackerName = tracker.name
        val trackerAlreadyAdded = hasChild(trackerName)

        if (hasConsent && !trackerAlreadyAdded) {
            addChild(tracker)
        } else if (!hasConsent && trackerAlreadyAdded) {
            removeChild(trackerName)
        }
    }

    fun addChild(child: TrackerChild) {
        trackers[child.name] = child
    }

    fun addChildren(vararg children: TrackerChild?) {
        children.filterNotNull().forEach(::addChild)
    }

    fun removeChild(name: String) {
        val tracker = trackers.remove(name)
        tracker?.onTrackerRemoved()
    }

    fun track(event: TrackEvent) {
        // Update event before passing it to each trackers.
        event.context = context

        trackers.values.forEach { tracker ->
            try {
                tracker.track(event)
            } catch (t: Throwable) {
                try {
                    catchException?.invoke(t)
                } catch (ignored: Exception) {
                    // Prevent the catcher from throwing an exception.
                }
            }
        }
    }

    fun updateConnectivity(connectivity: Connectivity) {
        context.connectivity = connectivity

        trackers.values.forEach { it.onConnectivityChanged(connectivity) }
    }

    fun updateUser(user: TrackingUser) {
        context.user = user

        trackers.values.forEach { it.onUserUpdated(user) }
    }

    fun logoutUser() {
        updateUser(UnknownUser)
    }
}