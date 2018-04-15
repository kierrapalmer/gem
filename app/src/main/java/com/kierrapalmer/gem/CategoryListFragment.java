//package com.kierrapalmer.gem;
//
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.kierrapalmer.gem.Fragments.CreateAccountFragment;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class CategoryListFragment extends Fragment implements View.OnClickListener  {
//
//    private View rootview;
//    public OnCategorySelected mCallback;
//
//    public CategoryListFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        rootview =  inflater.inflate(R.layout.fragment_category_list, container, false);
//
//        Button btnAll = (Button) rootview.findViewById(R.id.btnAll);
//        btnAll.setOnClickListener(this);
//        Button btnElectronics = (Button) rootview.findViewById(R.id.btnElectronics);
//        btnElectronics.setOnClickListener(this);
//        Button btnFashion = (Button) rootview.findViewById(R.id.btnFashion);
//        btnFashion.setOnClickListener(this);
//        Button btnAutomobile = (Button) rootview.findViewById(R.id.btnAutomobile);
//        btnAutomobile.setOnClickListener(this);
//        Button btnHome = (Button) rootview.findViewById(R.id.btnHome);
//        btnHome.setOnClickListener(this);
//
//        Button btnFurniture = (Button) rootview.findViewById(R.id.btnFurniture);
//        btnFurniture.setOnClickListener(this);
//        Button btnAppliances = (Button) rootview.findViewById(R.id.btnAppliances);
//        btnAppliances.setOnClickListener(this);
//        Button btnKids = (Button) rootview.findViewById(R.id.btnKids);
//        btnKids.setOnClickListener(this);
//        Button btnOthers = (Button) rootview.findViewById(R.id.btnOthers);
//        btnOthers.setOnClickListener(this);
//
//        return rootview;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.btnAll:
//                mCallback.displayCategoryListItems(null);
//                break;
//            case R.id.btnElectronics:
//                mCallback.displayCategoryListItems("Electronics");
//                break;
//            case R.id.btnFashion:
//                mCallback.displayCategoryListItems("Fashion");
//                break;
//            case R.id.btnAutomobile:
//                mCallback.displayCategoryListItems("Automobile");
//                break;
//            case R.id.btnFurniture:
//                mCallback.displayCategoryListItems("Furniture");
//                break;
//
//            case R.id.btnHome:
//                mCallback.displayCategoryListItems("Home &amp; Decor");
//                break;
//            case R.id.btnAppliances:
//                mCallback.displayCategoryListItems("Appliances");
//                break;
//            case R.id.btnKids:
//                mCallback.displayCategoryListItems("Kids");
//                break;
//            case R.id.btnOthers:
//                mCallback.displayCategoryListItems("Others");
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    public interface OnCategorySelected    {
//        public void displayCategoryListItems(String category);
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        try {
//            mCallback = (OnCategorySelected) activity;
//        } catch (ClassCastException e)        {
//            throw new ClassCastException(activity.toString() +
//                    "must implement OnCategorySelected.");
//        }
//    }
//
//}
