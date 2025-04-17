package com.jsojitra220201.ApplicatinProjecTest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jsojitra220201.applicatinprojectest.R;

import java.util.ArrayList;

public class SessionAdapter extends ArrayAdapter<Session> {
    public SessionAdapter(Context context, ArrayList<Session> sessions) {
        super(context, 0, sessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Session session = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.session_item, parent, false);
        }
        TextView sportName = convertView.findViewById(R.id.sportName);
        TextView sessionInfo = convertView.findViewById(R.id.sessionInfo);

        sportName.setText(session.getSportName());
        sessionInfo.setText(session.getTimeFrame() + " | Gym: " + session.getGymNumber());

        return convertView;
    }
}
