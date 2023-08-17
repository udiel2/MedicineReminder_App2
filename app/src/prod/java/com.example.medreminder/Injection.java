package com.example.MedReminder;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.MedReminder.data.source.MedicineRepository;
import com.example.MedReminder.data.source.local.MedicinesLocalDataSource;

/**
 * 
 */

public class Injection {

    public static MedicineRepository provideMedicineRepository(@NonNull Context context) {
        return MedicineRepository.getInstance(MedicinesLocalDataSource.getInstance(context));
    }
}