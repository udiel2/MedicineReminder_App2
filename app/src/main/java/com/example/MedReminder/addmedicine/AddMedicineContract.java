package com.example.MedReminder.addmedicine;

import com.example.MedReminder.BasePresenter;
import com.example.MedReminder.BaseView;
import com.example.MedReminder.data.source.MedicineAlarm;
import com.example.MedReminder.data.source.Pills;

import java.util.List;

/**
 * 
 */

public interface AddMedicineContract {

    interface View extends BaseView<Presenter> {

        void showEmptyMedicineError();

        void showMedicineList();

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void saveMedicine(MedicineAlarm alarm, Pills pills);

        boolean isDataMissing();

        boolean isMedicineExits(String pillName);

        long addPills(Pills pills);

        Pills getPillsByName(String pillName);

        List<MedicineAlarm> getMedicineByPillName(String pillName);

        List<Long> tempIds();

        void deleteMedicineAlarm(long alarmId);

    }
}
