package com.cyrillrx.android.tracker.event;

import android.text.TextUtils;

import java.util.Map;

/**
 * @author Cyril Leroux
 *         Created on 26/11/2015.
 */
public class ViewEvent extends TrackEvent {

    ViewEvent() { }

    public static class Builder {

        private final ViewEvent event;

        public Builder() { event = new ViewEvent(); }

        public ViewEvent build() {
            if (TextUtils.isEmpty(event.category) ||
                    TextUtils.isEmpty(event.name)) {
                throw new IllegalStateException("Category and name are mandatory");
            }

            return event;
        }

        public Builder setCategory(String category) {
            event.category = category;
            return this;
        }

        public Builder setId(String id) {
            event.id = id;
            return this;
        }

        public Builder setType(String type) {
            event.type = type;
            return this;
        }

        public Builder setName(String name) {
            event.name = name;
            return this;
        }

        public Builder putCustomAttribute(String key, String value) {
            event.customAttributes.put(key, value);
            return this;
        }

        public Builder putCustomAttributes(Map<String, String> values) {
            event.customAttributes.putAll(values);
            return this;
        }
    }
}