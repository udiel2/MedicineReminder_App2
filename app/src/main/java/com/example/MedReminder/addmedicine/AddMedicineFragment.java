package com.example.MedReminder.addmedicine;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.MedReminder.alarm.ReminderReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.MedReminder.R;
import com.example.MedReminder.alarm.ReminderActivity;
import com.example.MedReminder.alarm.ReminderFragment;
import com.example.MedReminder.data.source.MedicineAlarm;
import com.example.MedReminder.data.source.Pills;
import com.example.MedReminder.views.DayViewCheckBox;
import com.example.MedReminder.views.RobotoBoldTextView;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import static android.content.Context.ALARM_SERVICE;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 */

public class AddMedicineFragment extends Fragment implements AddMedicineContract.View {

    public static final String ARGUMENT_EDIT_MEDICINE_ID = "ARGUMENT_EDIT_MEDICINE_ID";
    private static final String CHANNEL_ID = "MedReminder_Channel";

    public static final String ARGUMENT_EDIT_MEDICINE_NAME = "ARGUMENT_EDIT_MEDICINE_NAME";

    private AddMedicineContract.Presenter mPresenter;
    @BindView(R.id.edit_med_name)
    EditText editMedName;

    private String name;

    @BindView(R.id.searchb)
    Button searchb;

    @BindView(R.id.every_day)
    AppCompatCheckBox everyDay;

    @BindView(R.id.dv_sunday)
    DayViewCheckBox dvSunday;

    @BindView(R.id.dv_monday)
    DayViewCheckBox dvMonday;

    @BindView(R.id.dv_tuesday)
    DayViewCheckBox dvTuesday;

    @BindView(R.id.dv_wednesday)
    DayViewCheckBox dvWednesday;

    @BindView(R.id.dv_thursday)
    DayViewCheckBox dvThursday;

    @BindView(R.id.dv_friday)
    DayViewCheckBox dvFriday;

    @BindView(R.id.dv_saturday)
    DayViewCheckBox dvSaturday;

    @BindView(R.id.checkbox_layout)
    LinearLayout checkboxLayout;

    @BindView(R.id.tv_medicine_time)
    RobotoBoldTextView tvMedicineTime;

    @BindView(R.id.tv_dose_quantity)
    EditText tvDoseQuantity;

    @BindView(R.id.spinner_dose_units)
    AppCompatSpinner spinnerDoseUnits;

    private List<String> doseUnitList;

    private boolean[] dayOfWeekList = new boolean[7];

    private int hour, minute;

    Unbinder unbinder;
    private MyAdapter adapter;

    private AlertDialog.Builder builder;
    private AlertDialog dialog ;

    private ArrayList<String> data=new ArrayList<>();
    private ArrayList<String> data_herf=new ArrayList<>();



    private View rootView;

    private String doseUnit;

    static AddMedicineFragment newInstance() {
        Bundle args = new Bundle();
        AddMedicineFragment fragment = new AddMedicineFragment();
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(setClickListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_medicine, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setCurrentTime();
        setSpinnerDoseUnits();
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_edit_task_done);
//        fab.setImageResource(R.drawable.ic_done);
//        fab.setOnClickListener(setClickListener);
        fab.setVisibility(View.VISIBLE);
        Bundle args = getArguments();
        if (args != null) {
            String medName = args.getString("medName2");
            if(medName==null){
                return rootView;
            }
            String unitName = args.getString("unitName2");
            System.out.println("_____Back: "+medName);
            System.out.println("_____Back: "+unitName);
            editMedName.setText(medName.toString());
            name=medName;
            for(int i=0;i<doseUnitList.size() ;i++){

            }
            // עשה משהו עם הערך שנשלח, לדוגמה, הצג אותו בכמהוית
        }
        return rootView;
    }

    @Override
    public void setPresenter(AddMedicineContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showEmptyMedicineError() {
        // Snackbar.make(mTitle, getString(R.string.empty_task_message),
        // Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showMedicineList() {
        Objects.requireNonNull(getActivity()).setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @OnClick({R.id.searchb})
    public  void On_Search_Click(){

        RecyclerView recyclerView;

        builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.serach_dialog, null);
        builder.setView(dialogView);
        SearchView searchView=dialogView.findViewById(R.id.searchview);
        searchView.setIconified(false);
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
                AddMedicineFragment.FetchRecipeTask task = new AddMedicineFragment.FetchRecipeTask(new AddMedicineFragment.Callback() {
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

        recyclerView = dialogView.findViewById(R.id.recyclerView_sreach);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // הוספת נתונים נוספים לרשימה
        adapter = new MyAdapter(getContext(), data);
        recyclerView.setAdapter(adapter);

        dialog = builder.create();
        dialog.show();
        adapter.setOnItemClickedListener(position -> {
            System.out.println("----------------------Click---------------------");
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            String url= URLDecoder.decode(data_herf.get(position),"UTF8");
            System.out.println(url);
            String message="";
            AddMedicineFragment.FetchRecipeTask task = new AddMedicineFragment.FetchRecipeTask(new AddMedicineFragment.Callback() {
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

    }
    private String formatHebrewText(String input) {
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            if (i == 2 ){
                result.append(": ");
            }
            String word = words[i];
            if (isHebrewWord(word)) {
                result.append(word);
            }
        }


        return result.toString();
    }

    // פונקציה לבדיקה האם מילה מכילה רק תווים בשפת העברית
    private boolean isHebrewWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if (Character.UnicodeBlock.of(currentChar) != Character.UnicodeBlock.HEBREW) {
                return false;
            }
        }
        return true;
    }
    private void showAlertDialog(ArrayList<String> context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(context.get(1) + "\n" + context.get(2) + "\n" + context.get(3));
        builder.setTitle(context.get(0));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog2, which) -> {
            editMedName.setText(context.get(0).replace("'",""));
            dialog2.cancel();
            dialog.cancel();


        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private ArrayList<String> SelectMedecine(Document document){
        ArrayList<String> data2=new ArrayList<>();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String message="";
        System.out.println("----------------------DocumentReady---------------------");
        String unit=document.select("#primary > div:nth-child(1) > div:nth-child(2) > div > div.card-body > div:nth-child(1)").text();
        unit= formatHebrewText(unit);
        System.out.println(unit);
        String active_Ingredient=document.select("#ing1 > p > a").text();
        String active_Ingredient_amount=document.select("#primary > div:nth-child(2) > div:nth-child(1) > div > div:nth-child(2) > div > div.card-body > div:nth-child(2) > div > p").text();
        System.out.println(active_Ingredient);
        System.out.println(active_Ingredient_amount);
        String name=document.select("#primary > div:nth-child(1) > div:nth-child(1) > div > div.card-body > div:nth-child(1) > div.row.justify-content-between.align-items-start > p:nth-child(1)").text();

        data2.add(name);
        data2.add(active_Ingredient);
        data2.add(active_Ingredient_amount);
        data2.add(unit);

        return data2;

    }
    @OnClick({ R.id.every_day, R.id.dv_monday, R.id.dv_tuesday, R.id.dv_wednesday,
            R.id.dv_thursday, R.id.dv_friday, R.id.dv_saturday, R.id.dv_sunday })
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        /** Checking which checkbox was clicked */
        switch (view.getId()) {
            case R.id.dv_monday:
                if (checked) {
                    dayOfWeekList[1] = true;
                } else {
                    dayOfWeekList[1] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.dv_tuesday:
                if (checked) {
                    dayOfWeekList[2] = true;
                } else {
                    dayOfWeekList[2] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.dv_wednesday:
                if (checked) {
                    dayOfWeekList[3] = true;
                } else {
                    dayOfWeekList[3] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.dv_thursday:
                if (checked) {
                    dayOfWeekList[4] = true;
                } else {
                    dayOfWeekList[4] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.dv_friday:
                if (checked) {
                    dayOfWeekList[5] = true;
                } else {
                    dayOfWeekList[5] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.dv_saturday:
                if (checked) {
                    dayOfWeekList[6] = true;
                } else {
                    dayOfWeekList[6] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.dv_sunday:
                if (checked) {
                    dayOfWeekList[0] = true;
                } else {
                    dayOfWeekList[0] = false;
                    everyDay.setChecked(false);
                }
                break;
            case R.id.every_day:
                LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.checkbox_layout);
                for (int i = 0; i < ll.getChildCount(); i++) {
                    View v = ll.getChildAt(i);
                    ((DayViewCheckBox) v).setChecked(checked);
                    onCheckboxClicked(v);
                }
                break;
        }
    }

    @OnClick(R.id.tv_medicine_time)
    void onMedicineTimeClick() {
        showTimePicker();
    }



    private void showTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                tvMedicineTime.setText(String.format(Locale.getDefault(), "%d:%d", selectedHour, selectedMinute));
            }
        }, hour, minute, false);// No 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void setCurrentTime() {
        Calendar mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mCurrentTime.get(Calendar.MINUTE);
        int day=mCurrentTime.get(Calendar.DAY_OF_WEEK);
        dayOfWeekList[day]=true;
        System.out.println("Day : "+day);
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.checkbox_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View v = ll.getChildAt(i);
            if(i==day-1){
                ((DayViewCheckBox) v).setChecked(true);
                onCheckboxClicked(v);
            }

        }
        tvMedicineTime.setText(String.format(Locale.getDefault(), "%d:%d", hour, minute));
    }

    private void setSpinnerDoseUnits() {
        doseUnitList = Arrays.asList(getResources().getStringArray(R.array.medications_shape_array));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_dropdown_item_1line, doseUnitList);
        spinnerDoseUnits.setAdapter(adapter);
    }

    @OnItemSelected(R.id.spinner_dose_units)
    void onSpinnerItemSelected(int position) {
        if (doseUnitList == null || doseUnitList.isEmpty()) {
            return;
        }

        doseUnit = doseUnitList.get(position);
        Log.d("TAG", doseUnit);
    }

    private View.OnClickListener setClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int checkBoxCounter = 0;

            String pill_name = editMedName.getText().toString();
            String doseQuantity = tvDoseQuantity.getText().toString();

            Calendar takeTime = Calendar.getInstance();
            Date date = takeTime.getTime();
            String dateString = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date);

            /** Updating model */
            MedicineAlarm alarm = new MedicineAlarm();
            int alarmId = new Random().nextInt(100);

            /** If Pill does not already exist */
            if (!mPresenter.isMedicineExits(pill_name)) {
                Pills pill = new Pills();
                pill.setPillName(pill_name);
                alarm.setDateString(dateString);
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setPillName(pill_name);
                alarm.setDayOfWeek(dayOfWeekList);
                alarm.setDoseUnit(doseUnit);
                alarm.setDoseQuantity(doseQuantity);
                alarm.setAlarmId(alarmId);
                pill.addAlarm(alarm);
                long pillId = mPresenter.addPills(pill);
                pill.setPillId(pillId);
                mPresenter.saveMedicine(alarm, pill);
            } else { // If Pill already exists
                Pills pill = mPresenter.getPillsByName(pill_name);
                alarm.setDateString(dateString);
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setPillName(pill_name);
                alarm.setDayOfWeek(dayOfWeekList);
                alarm.setDoseUnit(doseUnit);
                alarm.setDoseQuantity(doseQuantity);
                alarm.setAlarmId(alarmId);
                pill.addAlarm(alarm);
                mPresenter.saveMedicine(alarm, pill);
            }

            List<Long> ids = new LinkedList<>();
            try {
                List<MedicineAlarm> alarms = mPresenter.getMedicineByPillName(pill_name);
                for (MedicineAlarm tempAlarm : alarms) {
                    if (tempAlarm.getHour() == hour && tempAlarm.getMinute() == minute) {
                        ids = tempAlarm.getIds();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 7; i++) {
                if (dayOfWeekList[i] && pill_name.length() != 0) {

                    int dayOfWeek = i + 1;
                    long _id = ids.get(checkBoxCounter);
                    int id = (int) _id;
                    checkBoxCounter++;

                    /**
                     * This intent invokes the activity ReminderActivity, which in turn opens the
                     * AlertAlarm window
                     */
                    Intent intent = new Intent(getActivity(), ReminderActivity.class);
                    intent.putExtra(ReminderFragment.EXTRA_ID, _id);

                    PendingIntent operation = PendingIntent.getActivity(getActivity(), id, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    /** Getting a reference to the System Service ALARM_SERVICE */
                    AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity())
                            .getSystemService(ALARM_SERVICE);

                    /**
                     * Creating a calendar object corresponding to the date and time set by the user
                     */
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                    /** Converting the date and time in to milliseconds elapsed since epoch */
                    long alarm_time = calendar.getTimeInMillis();

                    if (calendar.before(Calendar.getInstance()))
                        alarm_time += AlarmManager.INTERVAL_DAY * 7;

                    assert alarmManager != null;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                            AlarmManager.INTERVAL_DAY * 7, operation);
                }
            }
            // קריאה לפונקציה שתיצור את הערוץ במקרה והוא עדיין לא קיים
            createNotificationChannel();

// בניית ההתראה
            Notification.Builder builder = null; // להוסיף כיצד תרצה שהאייקון של ההתראה יראה
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Calendar calendar = Calendar.getInstance();
                builder = new Notification.Builder(getActivity(), CHANNEL_ID)
                        .setContentTitle("Medicine Reminder")
                        .setContentText("Reminder for " + pill_name + "\n"+"Time " + calendar.getTime())
                        .setSmallIcon(R.drawable.icon_blister);
            }
            // הפעלת השירות בזמן שאתה שומר תזכורת
            Intent serviceIntent = new Intent(getActivity(), ReminderReceiver.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // עבור גרסאות אנדרואיד 8.0 ומעלה יש צורך להתחיל שירות עם startForegroundService
                getActivity().startForegroundService(serviceIntent);
            } else {
                getActivity().startService(serviceIntent);
            }

// כדי לפתוח את האפליקציה כאשר לוחצים על ההתראה, צריך להוסיף Intent ו-PendingIntent
            Intent intent = new Intent(getActivity(), ReminderActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
// הצגת ההתראה בזמן שנבחר על ידי המשתמש
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//
// יצירת התאריך והשעה בהתאם לזמן שנבחר על ידי המשתמש
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

// כאשר התאריך והשעה בהתאם לזמן שנבחר עברו, התראה תוצג
            notificationManager.notify(alarmId, builder.build());

            Toast.makeText(getContext(), "Alarm for " + pill_name + " is set successfully", Toast.LENGTH_SHORT).show();
            showMedicineList();
        }
    };
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MedReminder Channel";
            String description = "MedReminder Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public interface Callback {
        void onDocumentReady(Document document);
        void onError(Exception e);
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, Void> {

        private AddMedicineFragment.Callback callback;

        @Override
        protected void onPostExecute(Void unused) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(unused);
        }

        public FetchRecipeTask(AddMedicineFragment.Callback callback) {
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


}
