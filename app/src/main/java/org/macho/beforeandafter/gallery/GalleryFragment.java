package org.macho.beforeandafter.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;
import org.macho.beforeandafter.record.ImageCache;
import org.macho.beforeandafter.record.Record;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class GalleryFragment extends Fragment {
    private TabHost tabHost;
//    private GridView frontGridView;
//    private GridView sideGridView;
    private RecyclerView frontGridView;
    private RecyclerView sideGridView;
    private GridAdapter frontGridAdapter;
    private GridAdapter sideGridAdapter;
    private List<String> frontImagePaths = new ArrayList<>();
    private List<String> sideImagePaths = new ArrayList<>();

    private ImageCache imageCache = new ImageCache();
//    private AdapterView.OnItemClickListener onItemSelectedListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent intent = new Intent(getContext(), PhotoActivity.class);
//            intent.putExtra("INDEX", i);
//            intent.putExtra("PATHS", frontImagePaths.toArray(new String[frontImagePaths.size()]));
//            startActivityForResult(intent, 97);
//            startActivity(intent);
//        }
//    };
//    private AdapterView.OnItemClickListener onItemSelectedListener2 = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent intent = new Intent(getContext(), PhotoActivity.class);
//            intent.putExtra("INDEX", i);
//            intent.putExtra("PATHS", sideImagePaths.toArray(new String[sideImagePaths.size()]));
//            startActivityForResult(intent, 97);
//            startActivity(intent);
//        }
//    };
    public static Fragment getInstance() {
        Fragment fragment = new GalleryFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_gallery, container, false);

//        tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
//        tabHost.setup();
//        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
//        tab1.setIndicator(getResources().getString(R.string.front));
//        tab1.setContent(R.id.tab1);
//        tabHost.addTab(tab1);
//        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
//        tab2.setIndicator(getResources().getString(R.string.side));
//        tab2.setContent(R.id.tab2);
//        tabHost.addTab(tab2);
//
//        for (Record record : RecordDao.getInstance().findAll()) {
//            frontImagePaths.add(record.getFrontImagePath());
//            sideImagePaths.add(record.getSideImagePath());
//        }
//
//        frontGridView = (GridView) view.findViewById(R.id.front_grid_view);
//        GridAdapter frontAdapter = new GridAdapter(getContext(), R.layout.grid_item, frontImagePaths);
//        frontGridView.setAdapter(frontAdapter);
//        frontGridView.setOnItemClickListener(onItemSelectedListener);
//        sideGridView = (GridView) view.findViewById(R.id.side_grid_view);
//        GridAdapter sideAdapter = new GridAdapter(getContext(), R.layout.grid_item, sideImagePaths);
//        sideGridView.setAdapter(sideAdapter);
//        sideGridView.setOnItemClickListener(onItemSelectedListener2);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        tab1.setIndicator(getResources().getString(R.string.front));
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        tab2.setIndicator(getResources().getString(R.string.side));
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);

        frontGridView = (RecyclerView) view.findViewById(R.id.front_grid_view);
        frontGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        frontGridView.setHasFixedSize(true);
        sideGridView = (RecyclerView) view.findViewById(R.id.side_grid_view);
        sideGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        sideGridView.setHasFixedSize(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageCache.clear();
        getLoaderManager().destroyLoader(2);
        getLoaderManager().destroyLoader(3);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().restartLoader(2, null, loaderCallback);
        getLoaderManager().restartLoader(3, null, loaderCallback2);
    }

    private final LoaderManager.LoaderCallbacks<List<String>> loaderCallback = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            ImagePathLoader loader = new ImagePathLoader(getContext());
            loader.forceLoad();
            return loader;
        }
        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            frontGridAdapter = new GridAdapter(GalleryFragment.this, data, imageCache);
            frontGridView.setAdapter(frontGridAdapter);
        }
        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            frontGridAdapter.clear();
        }
    };

    private final LoaderManager.LoaderCallbacks<List<String>> loaderCallback2 = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            ImagePathLoader2 loader = new ImagePathLoader2(getContext());
            loader.forceLoad();
            return loader;
        }
        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            sideGridAdapter = new GridAdapter(GalleryFragment.this, data, imageCache);
            sideGridView.setAdapter(sideGridAdapter);
        }
        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            sideGridAdapter.clear();
        }
    };

    private static class ImagePathLoader extends AsyncTaskLoader<List<String>> {
        public ImagePathLoader(Context context) {
            super(context);
        }
        @Override
        public List<String> loadInBackground() {
            List<String> paths = new ArrayList<>();
            List<Record> results = RecordDao.getInstance().findAll();
            for (Record record : results) {
                paths.add(record.getFrontImagePath());
            }
            return paths;
        }
    }

    private static class ImagePathLoader2 extends AsyncTaskLoader<List<String>> {
        public ImagePathLoader2(Context context) {
            super(context);
        }
        @Override
        public List<String> loadInBackground() {
            List<String> paths = new ArrayList<>();
            List<Record> results = RecordDao.getInstance().findAll();
            for (Record record : results) {
                paths.add(record.getSideImagePath());
            }
            return paths;
        }
    }



}
