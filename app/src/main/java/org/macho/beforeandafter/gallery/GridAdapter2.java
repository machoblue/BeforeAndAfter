package org.macho.beforeandafter.gallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.macho.beforeandafter.GlideApp;
import org.macho.beforeandafter.R;

import java.io.File;
import java.util.List;

import static org.macho.beforeandafter.BeforeAndAfterConst.PATH;

public class GridAdapter2 extends RecyclerView.Adapter<GridAdapter2.ViewHolder> {
    private Fragment fragment;
    private LayoutInflater layoutInflater;
    private List<String> items;

    public GridAdapter2(Fragment fragment, List<String> items) {
        this.fragment = fragment;
        this.layoutInflater = LayoutInflater.from(fragment.getContext());
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = items.get(position);
        GlideApp.with(fragment)
                .load(Uri.fromFile(new File(PATH, path == null ? "" : path)))
                .thumbnail(.1f)
                .error(new ColorDrawable(Color.GRAY))
                .into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View parent;
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            parent.setOnClickListener(this);
            imageView = (ImageView) parent.findViewById(R.id.imageView);
        }
        public void onClick(View view) {
            Intent intent = new Intent(fragment.getContext(), PhotoActivity2.class);
            intent.putExtra("INDEX", getAdapterPosition());
            intent.putExtra("PATHS", items.toArray(new String[items.size()]));
            fragment.startActivity(intent);
        }
        public ImageView getImageView() {
            return this.imageView;
        }
        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}
