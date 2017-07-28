package com.sensorberg.easyipc.testapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

	private final List<String> data = new ArrayList<>();

	public void add(String string) {
		data.add(0, string);
		notifyDataSetChanged();
	}

	@Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new Holder(new TextView(parent.getContext()));
	}

	@Override public void onBindViewHolder(Holder holder, int position) {
		((TextView) holder.itemView).setText(data.get(position));
	}

	@Override public int getItemCount() {
		return data.size();
	}

	static class Holder extends RecyclerView.ViewHolder {

		Holder(View itemView) {
			super(itemView);
			itemView.setPadding(0, 0, 0, (int) (itemView.getResources().getDisplayMetrics().density * 12));
		}
	}
}
