package com.enex.covt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CountryViewAdaptor extends RecyclerView.Adapter <CountryViewAdaptor.ViewHolder>{
    List<Country> list;
    List<Country> fullList;
    Context context;
    public CountryViewAdaptor(List<Country> list,Context context){
        this.list = list;
        this.context = context;
        this.fullList = new ArrayList<>(list);
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.country_name_text,parent,false);
        ViewHolder viewHolder = new ViewHolder(textView);
        this.fullList = new ArrayList<>(list);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getCountryName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SearchByCountryActivity.class);
                intent.putExtra("countryname",list.get(position).getCountryName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    @Override
//    public Filter getFilter() {
//        return countryFilter;
//    }
    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Country> filteredList = new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                filteredList.addAll(fullList);
            }else{
                String pattern = constraint.toString().toLowerCase().trim();
                for (Country item : fullList){
                    if (item.getCountryName().toLowerCase().contains(pattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.add((Country)results.values);
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.countryNameText);
        }

    }
}
