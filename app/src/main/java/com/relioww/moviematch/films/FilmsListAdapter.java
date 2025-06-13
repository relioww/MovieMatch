package com.relioww.moviematch.films;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.BitmapCallback;
import com.relioww.moviematch.core.ImageLoader;
import com.relioww.moviematch.callbacks.OnFilmItemClickListener;
import com.relioww.moviematch.callbacks.OnFilmsListUpdated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class FilmsListAdapter extends RecyclerView.Adapter<FilmsListAdapter.MyViewHolder> {
    private final String TAG = "fjsfhsfhsdfs";


    private final List<FilmItem> films;
    private final OnFilmItemClickListener clickListener;
    private final OnFilmsListUpdated updateListener;
    private final ImageLoader imageLoader;

    public FilmsListAdapter(List<FilmItem> films, OnFilmItemClickListener clickListener,
                            OnFilmsListUpdated updateListener) {
        this.films = films;
        this.clickListener = clickListener;
        this.updateListener = updateListener;
        imageLoader = new ImageLoader();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, year;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.filmPoster);
            year = view.findViewById(R.id.year);
        }

        public void bind(final FilmItem item, final OnFilmItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onClick(item);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.film_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FilmItem item = films.get(position);
        holder.name.setText(item.getName());
        holder.image.setImageBitmap(item.getImage());
        holder.year.setText(String.valueOf(item.getYear()));
        holder.bind(films.get(position), clickListener);
    }

    public void insertData(JSONArray newData) throws JSONException {
        for (int i = 0; i < newData.length(); i++) {
            JSONObject item = (JSONObject) newData.get(i);
            int id = item.getInt("id");
            String name = item.getString("name");
            int year = item.getInt("year");
            String poster = item.getString("poster");
            films.add(new FilmItem(id, name,
                    null, year));

            imageLoader.getImage(poster, i, new BitmapCallback() {
                @Override
                public void onSuccess(Bitmap bitmap, int index) {
                    films.get(index).setImage(bitmap);
                    updateListener.onItemChanged(index);
                }

                @Override
                public void onFailure(IOException e) {
                    Log.e(TAG, e.toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return films.size();
    }
}
