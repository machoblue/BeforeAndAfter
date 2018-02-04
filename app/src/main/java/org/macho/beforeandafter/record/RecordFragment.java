package org.macho.beforeandafter.record;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.macho.beforeandafter.ImageUtil;
import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static org.macho.beforeandafter.BeforeAndAfterConst.PATH;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class RecordFragment extends Fragment {
    private RecyclerView listView;
    private FloatingActionButton fab;
    private List<Record> items;
    private RecordAdapter recordAdapter;
    private ImageCache imageCache = new ImageCache();
    private static final int EDIT_REQUEST_CODE = 98;
    private View.OnClickListener onFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), EditActivity.class);
//            startActivity(intent);
            startActivityForResult(intent, EDIT_REQUEST_CODE);
        }
    };
//    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent intent = new Intent(getContext(), EditActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putLong("DATE", items.get(i).getDate());
//            intent.putExtra("DATE", items.get(i).getDate());
//            startActivityForResult(intent, EDIT_REQUEST_CODE);
//        }
//    };
    public static Fragment getInstance() {
        Fragment fragment = new RecordFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_record, container, false);
//        listView = (ListView) view.findViewById(R.id.workname_list_view);
//        listView.setOnItemClickListener(onItemClickListener);
//        fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(onFabClickListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = (RecyclerView) view.findViewById(R.id.record_list_view);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(onFabClickListener);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        items = findItems();
//        listView.setAdapter(new ecordAdapter(getContext(), items));
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
//            throw new RuntimeException("[RESULT_NG] requestCode:" + requestCode + ", resultCode:" + resultCode);
        }
        if (requestCode == EDIT_REQUEST_CODE) {
            if (data == null) { // androidの戻るボタンを使った場合、何もしない。
                return;
            }
            switch (data.getIntExtra("TYPE", 0)) {
                case 0:
                    break;
                case 1:
                    int index2 = data.getIntExtra("INDEX", 0);
                    items.remove(index2);
                    recordAdapter.notifyItemRemoved(index2);

                    break;
                case 2:
                    if (data.getBooleanExtra("ISNEW", false)) {
                        long date = data.getLongExtra("DATE", 0);
                        Record record = RecordDao.getInstance().find(date);
                        items.add(record);
                        recordAdapter.notifyItemInserted(items.size() - 1);

                        // レビューの誘導
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        boolean reviewed = preferences.getBoolean("REVIEWED", false);

                        Record firstRecord = RecordDao.getInstance().findAll().get(0);
                        float diff = firstRecord.getWeight() - record.getWeight();

                        if (!reviewed && diff > 1f) {
                            ReviewDialog.newInstance().show(getFragmentManager(), "");
                        }

                    } else {
                        int index = data.getIntExtra("INDEX", 0);
                        System.out.println("onActivityResult:" + index);
                        Record record = items.get(index);
                        Record after = RecordDao.getInstance().find(record.getDate());
                        record.setWeight(after.getWeight());
                        record.setRate(after.getRate());
                        record.setMemo(after.getMemo());
                        record.setFrontImagePath(after.getFrontImagePath());
                        record.setSideImagePath(after.getSideImagePath());
                        recordAdapter.notifyItemChanged(index);
                    }
                    break;
                default:
                    break;
            }
        }
    }

//    public List<Record> findItems() {
//        List<Record> items = new ArrayList<>();
//        List<Record> results = RecordDao.getInstance().findAll();
//        if (results.size() > 0) {
//            items.addAll(results);
//        }
//        return items;
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().restartLoader(1, null, loaderCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageCache.clear();
        getLoaderManager().destroyLoader(1);
    }

    private final LoaderManager.LoaderCallbacks<List<Record>> loaderCallback = new LoaderManager.LoaderCallbacks<List<Record>>() {
        @Override
        public Loader<List<Record>> onCreateLoader(int id, Bundle args) {
            RecordLoader loader = new RecordLoader(RecordFragment.this.getContext());
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<Record>> loader, List<Record> data) {
            items = data;
            recordAdapter = new RecordAdapter(getContext(), items, 100, imageCache);
            listView.setAdapter(recordAdapter);
        }

        @Override
        public void onLoaderReset(Loader<List<Record>> loader) {
            recordAdapter.clear();
        }
    };

    private static class RecordLoader extends AsyncTaskLoader<List<Record>> {
        public RecordLoader(Context context) {
            super(context);
        }

        @Override
        public List<Record> loadInBackground() {
            List<Record> items = new ArrayList<>();
            List<Record> results = RecordDao.getInstance().findAll();
            if (results.size() > 0) {
                items.addAll(results);
            }
            return items;
        }
    }


    public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordItemViewHolder> {
        private List<Record> records;
        //    private Context context;
        private LayoutInflater layoutInflater;
        private ImageCache imageCache;
        private int viewHeight;

        public RecordAdapter(Context context, List<Record> records, int viewHeight, ImageCache imageCache) {
//        this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.records = records;
            this.viewHeight = viewHeight;
            this.imageCache = imageCache;
        }
//    @Override
//    public int getCount() {
//        return records.size();
//    }

//    @Override
//    public Object getItem(int i) {
//        return records.get(i);
//    }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public void clear() {
            records.clear();
            notifyDataSetChanged();
        }

        @Override
        public RecordItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.list_item_record, parent, false);
            return new RecordItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordItemViewHolder holder, int position) {
            Record currentRecord = records.get(position);

            String frontImageFile = PATH + "/" + currentRecord.getFrontImagePath();
            Bitmap frontImageBitmap = imageCache.get(frontImageFile);
            if (frontImageBitmap == null) {
                new ImageLoadTask(position, frontImageFile, viewHeight, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                holder.getFrontImage().setImageDrawable(null);
            } else {
                holder.getFrontImage().setImageBitmap(frontImageBitmap);
            }

            String sideImageFile = PATH + "/" + currentRecord.getSideImagePath();
            Bitmap sideImageBitmap = imageCache.get(sideImageFile);
            if (sideImageBitmap == null) {
                new ImageLoadTask(position, sideImageFile, viewHeight, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                holder.getSideImage().setImageDrawable(null);
            } else {
                holder.getSideImage().setImageBitmap(sideImageBitmap);
            }

            holder.getDate().setText(String.format("%1$tF %1$tH:%1$tM:%1$tS", new Date(currentRecord.getDate())));
            holder.getWeight().setText(String.format("%.2fkg", currentRecord.getWeight()));
            holder.getRate().setText(String.format("%.2f％", currentRecord.getRate()));
            holder.getMemo().setText(currentRecord.getMemo());
        }
        public class RecordItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private View parent;
            private ImageView frontImage;
            private ImageView sideImage;
            private TextView date;
            private TextView weight;
            private TextView rate;
            private TextView memo;
            public RecordItemViewHolder(View view) {
                super(view);
                parent = view;
                frontImage = (ImageView) view.findViewById(R.id.frontImage);
                sideImage = (ImageView) view.findViewById(R.id.sideImage);
                date = (TextView) view.findViewById(R.id.date);
                weight = (TextView) view.findViewById(R.id.weight);
                rate = (TextView) view.findViewById(R.id.rate);
                memo = (TextView) view.findViewById(R.id.memo);
                parent.setOnClickListener(this);
            }

            @Override
            public void onClick(View View) {
                try {
                    Intent intent = new Intent(getContext(), EditActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putLong("DATE", items.get(i).getDate());
                    intent.putExtra("INDEX", getAdapterPosition());
                    intent.putExtra("DATE", records.get(getAdapterPosition()).getDate());
                    startActivityForResult(intent, EDIT_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            public ImageView getFrontImage() {
                return frontImage;
            }

            public void setFrontImage(ImageView frontImage) {
                this.frontImage = frontImage;
            }

            public ImageView getSideImage() {
                return sideImage;
            }

            public void setSideImage(ImageView sideImage) {
                this.sideImage = sideImage;
            }

            public TextView getDate() {
                return date;
            }

            public void setDate(TextView date) {
                this.date = date;
            }

            public TextView getWeight() {
                return weight;
            }

            public void setWeight(TextView weight) {
                this.weight = weight;
            }

            public TextView getRate() {
                return rate;
            }

            public void setRate(TextView rate) {
                this.rate = rate;
            }

            public TextView getMemo() {
                return memo;
            }

            public void setMemo(TextView memo) {
                this.memo = memo;
            }
        }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        RecordItemViewHolder holder;
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.list_item_record, parent, false);
//            holder = new RecordItemViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (RecordItemViewHolder) convertView.getTag();
//        }
//
//        Record currentRecord = records.get(position);
//
//        File sideFile = new File("/data/data/org.macho.beforeandafter/files/" + currentRecord.getFrontImagePath());
//        if (sideFile.exists()) {
//            try (InputStream is = context.openFileInput(currentRecord.getFrontImagePath())) {
//                Bitmap frontBitmap = BitmapFactory.decodeStream(is);
//                holder.getFrontImage().setImageBitmap(frontBitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        File frontFile = new File("/data/data/org.macho.beforeandafter/files/" + currentRecord.getSideImagePath());
//        if (frontFile.exists()) {
//            try (InputStream is = context.openFileInput(currentRecord.getSideImagePath())) {
//                Bitmap sideBitmap = BitmapFactory.decodeStream(is);
//                holder.getSideImage().setImageBitmap(sideBitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        holder.getDate().setText(String.format("%1$tF %1$tH:%1$tM:%1$tS", new Date(currentRecord.getDate())));
//        holder.getWeight().setText(String.format("%.2fkg",currentRecord.getWeight()));
//        holder.getRate().setText(String.format("%.2f％", currentRecord.getRate()));
//        holder.getMemo().setText(currentRecord.getMemo());
//
//        return convertView;
//    }


        private class ImageLoadTask extends AsyncTask<Void, Void, Boolean> {
            private int position;
            private int viewHeight;
            private String filePath;
            private RecyclerView.Adapter<RecordItemViewHolder> adapter;

            public ImageLoadTask(int position, String url, int viewHeight, RecyclerView.Adapter<RecordItemViewHolder> adapter) {
                this.position = position;
                this.filePath = url;
                this.viewHeight = viewHeight;
                this.adapter = adapter;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                File file = new File(filePath);

                if (!file.exists()) {
                    return false;
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 画像サイズのみ読み込む

                BitmapFactory.decodeFile(filePath, options); // 同上
                int imageHeight = options.outHeight;

                // 縮小率を計算する。1:等倍。2:は1辺が1/2になる
                // 2の累乗以外の数字を指定した場合には、その値以下の2の累乗にまとめられる
                // 3の場合には2、6の場合には4など
                int inSampleSize = 1;
                if (imageHeight > viewHeight) {
                    inSampleSize = Math.round((float) imageHeight / (float) viewHeight);
                }

                options.inJustDecodeBounds = false;
                options.inSampleSize = inSampleSize;

                Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, options);
                Bitmap orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(tempBitmap, file); // 向き対応

                imageCache.put(filePath, orientationModifiedBitmap);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result && adapter != null) {
                    adapter.notifyItemChanged(position);
                }
            }
        }
    }
}
