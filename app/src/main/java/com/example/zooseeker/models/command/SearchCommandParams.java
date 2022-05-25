package com.example.zooseeker.models.command;

import android.content.Context;

import com.example.zooseeker.activities.HomeActivity;

public class SearchCommandParams {
    public String query;
    public Context context;

    public SearchCommandParams(Context context, String query) {
        this.context = context;
        this.query = query;
    }
}
