package com.cyrillrx.android.tracker.extension;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.cyrillrx.android.tracker.TrackFilter;
import com.cyrillrx.android.tracker.TrackWrapper;
import com.cyrillrx.android.tracker.TrackerChild;
import com.cyrillrx.android.tracker.TrackerContext;
import com.cyrillrx.android.tracker.event.ActionEvent;
import com.cyrillrx.android.tracker.event.RatingEvent;
import com.cyrillrx.android.tracker.event.TrackEvent;
import com.cyrillrx.android.tracker.event.ViewEvent;

/**
 * A {@link TrackWrapper} wrapping a Answer (Fabric) {@link TrackerChild}.
 *
 * @author Cyril Leroux
 *         Created on 11/11/2015.
 */
public class FabricTracker extends TrackWrapper {

    public FabricTracker(TrackFilter filter) { super(new FabricTrackChild(), filter); }

    public FabricTracker() { super(new FabricTrackChild()); }

    private static class FabricTrackChild implements TrackerChild {

        @Override
        public void track(TrackerContext context, TrackEvent event) {

            if (event instanceof ViewEvent) {
                trackView((ViewEvent) event);

            } else if (event instanceof ActionEvent) {
                trackAction((ActionEvent) event);

            } else if (event instanceof RatingEvent) {
                trackRating((RatingEvent) event);

            } else {
                trackCustom(event);
            }
        }

        private void trackView(ViewEvent event) {
            Answers.getInstance().logContentView(
                    new ContentViewEvent()
                            .putContentId(event.getId())
                            .putContentType(event.getType())
                            .putContentName(event.getName())
                            .putCustomAttribute("createdAt", event.getCreatedAt())
            );
        }

        private void trackAction(ActionEvent event) {
            Answers.getInstance().logCustom(
                    new CustomEvent(event.getAction())
                            .putCustomAttribute("category", event.getCategory())
                            .putCustomAttribute("id", event.getId())
                            .putCustomAttribute("type", event.getType())
                            .putCustomAttribute("name", event.getName())
                            .putCustomAttribute("action", event.getAction())
                            .putCustomAttribute("createdAt", event.getCreatedAt())
            );
        }

        private void trackRating(RatingEvent event) {
            Answers.getInstance().logRating(
                    new com.crashlytics.android.answers.RatingEvent()
                            .putRating(event.getRating())
                            .putContentName(event.getName())
                            .putContentType(event.getType())
                            .putContentId(event.getId())
                            .putCustomAttribute("createdAt", event.getCreatedAt())
            );
        }

        private void trackCustom(TrackEvent event) {

            String eventName = event.getName();
            if (eventName == null) {
                // Fallback on event category if no name is found
                eventName = event.getCategory();
                if (event.getType() != null && event.getId() != null) {
                    // Add event type and id if available
                    eventName += "_" + event.getType() + "_" + event.getId();
                }
            }

            Answers.getInstance().logCustom(
                    new CustomEvent(eventName)
                            .putCustomAttribute("category", event.getCategory())
                            .putCustomAttribute("id", event.getId())
                            .putCustomAttribute("type", event.getType())
                            .putCustomAttribute("name", event.getName())
                            .putCustomAttribute("createdAt", event.getCreatedAt())
            );
        }
    }
}