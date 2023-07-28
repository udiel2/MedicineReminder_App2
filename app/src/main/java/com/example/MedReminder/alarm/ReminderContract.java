package com.example.MedReminder.alarm;

import com.example.MedReminder.BasePresenter;
import com.example.MedReminder.BaseView;
import com.example.MedReminder.data.source.History;
import com.example.MedReminder.data.source.MedicineAlarm;

/**
 * 
 */

public interface ReminderContract {

    interface View extends BaseView<Presenter> {

        void showMedicine(MedicineAlarm medicineAlarm);

        void showNoData();

        boolean isActive();

        void onFinish();

    }

    interface Presenter extends BasePresenter {

        void finishActivity();

        void onStart(long id);

        void loadMedicineById(long id);

        void addPillsToHistory(History history);

    }
}
