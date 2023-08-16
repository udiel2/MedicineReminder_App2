package com.example.MedReminder.addmedicine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MedReminder.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchMedicineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchMedicineFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SearchView searchView;
    RecyclerView recyclerView;

    private MyAdapter adapter;
    private ArrayList<String> data=new ArrayList<>();
    private ArrayList<String> data_herf=new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchMedicineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchMedicineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchMedicineFragment newInstance(String param1, String param2) {
        SearchMedicineFragment fragment = new SearchMedicineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                data.clear();
                data_herf.clear();
                String html="https://drug.co.il/?s=";
                html=html+newText;
                SearchMedicineFragment.FetchRecipeTask task = new SearchMedicineFragment.FetchRecipeTask(new Callback() {
                    @Override
                    public void onDocumentReady(Document document) {
                        onPostExecute1(document);

                    }
                    @Override
                    public void onError(Exception e) {

                    }
                });
                task.execute(html);
                return true;

            }
        });
        recyclerView = view.findViewById(R.id.recyclerView_sreach);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // הוספת נתונים נוספים לרשימה
        adapter = new MyAdapter(getContext(), data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(position -> {
            System.out.println("----------------------Click---------------------");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String url= URLDecoder.decode(data_herf.get(position),"UTF8");
            System.out.println(url);
            String message="";
            SearchMedicineFragment.FetchRecipeTask task = new SearchMedicineFragment.FetchRecipeTask(new Callback() {
                @Override
                public void onDocumentReady(Document document) {
                    ArrayList<String> data2= SelectMedecine(document);
                    getActivity().runOnUiThread(() -> {
                        showAlertDialog(data2); // כאן אתה מציג את ה-AlertDialog
                    });

                }
                @Override
                public void onError(Exception e) {

                }
            });
            task.execute(url);
            // Set the message show for the Alert time
        });

        // הגדרת ה-OnItemClickedListener כאן
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search_medicine, container, false);


        return view;
    }
    public interface Callback {
        void onDocumentReady(Document document);
        void onError(Exception e);
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, Void> {

        private SearchMedicineFragment.Callback callback;

        @Override
        protected void onPostExecute(Void unused) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(unused);
        }

        public FetchRecipeTask(SearchMedicineFragment.Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                String url = urls[0];
                Document document = Jsoup.connect(url).get();
                if (callback != null) {
                    callback.onDocumentReady(document);
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
            return null;
        }

    }
    private void onPostExecute1(Document document) {
//        System.out.println(document);
        if (document != null) {
///#primary
            String p_number = document.select("#searchResults_count_label").text();
            Elements elements = document.select("#primary");
            for (Element li : document.select("article[id]")) {
                String name=li.select("h2").text();
                data.add(name);
                data_herf.add(li.select("a").attr("href"));
                System.out.println(li.select("a").attr("href"));
                System.out.println(li.select("h2").text());
            }

//        callback.onAllProductsReady();
        }
    }
    private ArrayList<String> SelectMedecine(Document document){
        ArrayList<String> data2=new ArrayList<>();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String message="";
        System.out.println("----------------------DocumentReady---------------------");
        String unit=document.select("#primary > div:nth-child(1) > div:nth-child(2) > div > div.card-body > div:nth-child(1)").text();
        System.out.println(unit);
        String active_Ingredient=document.select("#ing1 > p > a").text();
        String active_Ingredient_amount=document.select("#primary > div:nth-child(2) > div:nth-child(1) > div > div:nth-child(2) > div > div.card-body > div:nth-child(2) > div > p").text();
        System.out.println(active_Ingredient);
        System.out.println(active_Ingredient_amount);
        String name=document.select("#primary > div:nth-child(1) > div:nth-child(1) > div > div.card-body > div:nth-child(1) > div.row.justify-content-between.align-items-start > p:nth-child(1)").text();
//
        data2.add(name);
        data2.add(active_Ingredient);
        data2.add(active_Ingredient_amount);
        data2.add(unit);
//
//        builder.setMessage(active_Ingredient+active_Ingredient_amount);
//
//        // Set Alert Title
//        builder.setTitle(name);
//
//        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
//        builder.setCancelable(false);
//
//        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
//        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//            // When the user click yes button then app will close
//            dialog.cancel();
//            AddMedicineFragment addMedicineFragment = new AddMedicineFragment();
//            Bundle args = new Bundle();
//            args.putString("medName", "medNameFromSearchFragment"); // הוספת הערך שאתה רוצה לשלוח
//// הוספת ערכים נוספים...
//            addMedicineFragment.setArguments(args);
//
//// במידה ואתה משתמש בקונסטרקטור ניתן להעביר את ה- Bundle כפרמטר
//// AddMedicineFragment addMedicineFragment = AddMedicineFragment.newInstance(args);
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.contentFrame, addMedicineFragment);
//            fragmentTransaction.commit();
//
//        });
//
//        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
//        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
//            // If user click no then dialog box is canceled.
//            dialog.cancel();
//        });
//
//        // Create the Alert dialog
//        AlertDialog alertDialog = builder.create();
//        // Show the Alert Dialog box
//        alertDialog.show();
        return data2;

    }
    private void showAlertDialog(ArrayList<String> context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(context.get(1) + "\n" + context.get(2) + "\n" + context.get(3));
        builder.setTitle(context.get(0));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.cancel();

            // קוד להעברה לפרגמנט האב ושליחת מידע
            AddMedicineFragment addMedicineFragment = new AddMedicineFragment();
            Bundle args = new Bundle();
            args.putString("medName2", context.get(0)); // לדוגמה, שליחת שם תרופה
            // הוספת ערכים נוספים ל-Bundle...
            addMedicineFragment.setArguments(args);

            // הסרת הפרגמנט הילד וחזרה לפרגמנט האב
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contentFrame, addMedicineFragment).commit();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}