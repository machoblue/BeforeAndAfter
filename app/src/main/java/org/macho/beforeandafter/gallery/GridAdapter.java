package org.macho.beforeandafter.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.record.ImageCache;

import java.io.File;
import java.util.List;

import static org.macho.beforeandafter.BeforeAndAfterConst.PATH;

/**
 * Created by yuukimatsushima on 2017/10/01.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private Context context;
//    private int resource;
    private LayoutInflater layoutInflater;
    private List<String> items;
    private ImageCache imageCache;
    private Fragment fragment;

    public GridAdapter(Fragment fragment, List<String> items, ImageCache imageCache) {
        this.context = fragment.getContext();
//        this.resource = resource;
        layoutInflater = LayoutInflater.from(fragment.getContext());
        this.items = items;
        this.imageCache = imageCache;
        this.fragment = fragment;
    }

//    @Override
//    public int getCount() {
//        return items.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return items.get(i);
//    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.grid_item, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        String path = PATH + "/" + items.get(position);
        Bitmap imageBitmap = imageCache.get(path);
        if (imageBitmap == null) {
            new ImageLoadTask(position, path, 200).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            holder.imageView.setImageDrawable(null);
        } else {
            holder.imageView.setImageBitmap(imageBitmap);
        }
    }

//    @Override
//    public long getItemId(int i) {
//        return i;
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
//            holder = new ViewHolder();
//            holder.setImageView((ImageView) convertView.findViewById(R.id.imageView));
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        holder.getImageView().setImageBitmap(BitmapFactory.decodeFile("/data/data/org.macho.beforeandafter/files/" + items.get(position)));
//        return convertView;
//    }
//    class ViewHolder {
//        private ImageView imageView;
//        public void setImageView(ImageView imageView) {
//            this.imageView = imageView;
//        }
//        public ImageView getImageView() {
//            return imageView;
//        }
//    }

    public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View parent;
        private ImageView imageView;
        public GridViewHolder(View view) {
            super(view);
            parent = view;
            parent.setOnClickListener(this);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PhotoActivity.class);
            intent.putExtra("INDEX", getAdapterPosition());
            intent.putExtra("PATHS", items.toArray(new String[items.size()]));
            fragment.startActivity(intent);
        }
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Boolean> {
        private int position;
        private int viewHeight;
        private String filePath;
        public ImageLoadTask(int position, String url, int viewHeight) {
            this.position = position;
            this.filePath = url;
            this.viewHeight = viewHeight;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File file = new File(filePath);

            if (!file.exists()) {
                return false;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(filePath, options);
            int imageHeight = options.outHeight;

            int inSampleSize = 1;
            if (imageHeight > viewHeight) {
                inSampleSize = Math.round((float) imageHeight / (float) viewHeight);
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imageCache.put(filePath, bitmap);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result && GridAdapter.this != null) {
                GridAdapter.this.notifyItemChanged(position);
            }
        }
    }
}
