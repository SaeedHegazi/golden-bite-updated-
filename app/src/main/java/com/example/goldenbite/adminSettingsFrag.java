package com.example.goldenbite;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class adminSettingsFrag extends Fragment{
    private ListView listView;
    private ArrayList<String> myList;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_admin_settings, container, false);
        listView = view.findViewById(R.id.listSettings);
        myList = new ArrayList<>();
        myList.add("Today's Profits");
        myList.add("Golden Bite's Menu");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, myList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Toast.makeText(getContext(), "sassa", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), todaysProfits.class);
                    startActivity(intent);
                }
                if (position == 1){
                    Toast.makeText(getContext(), "sassdasdsafa", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(getContext(), adminMenu.class);
                    startActivity(intent);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}