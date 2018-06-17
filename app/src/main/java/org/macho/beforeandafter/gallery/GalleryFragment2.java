package org.macho.beforeandafter.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;
import org.macho.beforeandafter.record.Record;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment2 extends Fragment {
    private TabHost tabHost;
    private RecyclerView frontGridView;
    private RecyclerView sideGridView;
    private GridAdapter2 frontGridAdapter;
    private GridAdapter2 sideGridAdapter;
    private List<String> frontImagePaths = new ArrayList<>();
    private List<String> sideImagePaths = new ArrayList<>();

    public static Fragment getInstance() {
        Fragment fragment = new GalleryFragment2();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_gallery, container, false);
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
    public void onStart() {
        super.onStart();
        System.out.println("*** GalleryFragment.onStart ***");
        RecordDao dao = RecordDao.getInstance();
        List<Record> records = dao.findAll();
        for (Record record : records) {
            frontImagePaths.add(record.getFrontImagePath());
            sideImagePaths.add(record.getSideImagePath());
        }
        frontGridAdapter = new GridAdapter2(this, frontImagePaths);
        frontGridView.setAdapter(frontGridAdapter);
        sideGridAdapter = new GridAdapter2(this, sideImagePaths);
        sideGridView.setAdapter(sideGridAdapter);
    }
}
