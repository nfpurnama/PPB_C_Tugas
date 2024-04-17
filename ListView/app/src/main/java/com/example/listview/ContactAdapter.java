package com.example.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    private static class ViewHolder{
        TextView name;
        TextView phoneNbr;
        ImageView image;
    }

    public ContactAdapter(Context context, int resource, List<Contact> contacts){
        super(context, resource, contacts);
    }

    public View getView(int position, View ConvertView, ViewGroup parent){
        Contact contact = getItem(position);

        ViewHolder viewHolder;
        if(ConvertView == null){
            viewHolder = new ViewHolder();
            ConvertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
            viewHolder.name = ConvertView.findViewById(R.id.name);
            viewHolder.phoneNbr = ConvertView.findViewById(R.id.phoneNbr);
//            viewHolder.image = ConvertView.findViewById(R.id.img);
            ConvertView.setTag(viewHolder);

//            Button button = (Button) ConvertView.findViewById(R.id.btn);
//            button.setTag(position);
//            button.setOnClickListener(operation);
        }else{
            viewHolder = (ViewHolder) ConvertView.getTag();
        }
        viewHolder.name.setText(contact.getName());
        viewHolder.phoneNbr.setText(contact.getPhoneNbr());

        return ConvertView;
    }
}
