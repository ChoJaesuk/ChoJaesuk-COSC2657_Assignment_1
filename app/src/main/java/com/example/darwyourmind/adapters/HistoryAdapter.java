package com.example.darwyourmind.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.darwyourmind.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<JSONObject> historyList;
    private Context context;

    public HistoryAdapter(List<JSONObject> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        try {
            JSONObject testResult = historyList.get(position);

            // Set title
            String title = testResult.getString("title");
            holder.titleTextView.setText(title);

            // Load drawing
            String drawingPath = testResult.getString("drawingPath");
            File file = new File(drawingPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.drawingImageView.setImageBitmap(bitmap);
            } else {
                holder.drawingImageView.setImageResource(R.drawable.ic_placeholder); // Default image
            }

            // Show questions, answers, and explanations
            JSONArray resultsArray = testResult.getJSONArray("results");
            StringBuilder resultsBuilder = new StringBuilder();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                String question = result.getString("question");
                String answer = result.getString("answer");
                String explanation = result.getString("explanation");
                resultsBuilder.append("Q: ").append(question)
                        .append("\nA: ").append(answer)
                        .append("\nExplanation: ").append(explanation)
                        .append("\n\n");
            }
            holder.detailsTextView.setText(resultsBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView drawingImageView;
        TextView titleTextView, detailsTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            drawingImageView = itemView.findViewById(R.id.drawingImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
        }
    }
}
