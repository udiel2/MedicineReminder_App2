package com.example.MedReminder;

import org.junit.Test;
import com.example.MedReminder.R;

import static org.junit.Assert.*;

public class MedicineAlarmTest {

    @Test
    public void testCompareTo_EarlierAlarmShouldBeLessThanLaterAlarm() {
        MedicineAlarm earlierAlarm = new MedicineAlarm(1, 10, 30, "Paracetamole", "1", "mg", 1);
        MedicineAlarm laterAlarm = new MedicineAlarm(2, 12, 0, "Concerta", "2", "mg", 2);

        int result = earlierAlarm.compareTo(laterAlarm);

        assertTrue(result < 0);
    }

    @Test
    public void testCompareTo_LaterAlarmShouldBeGreaterThanEarlierAlarm() {
        MedicineAlarm earlierAlarm = new MedicineAlarm(1, 10, 30, "Paracetamole", "1", "mg", 1);
        MedicineAlarm laterAlarm = new MedicineAlarm(2, 12, 0, "Concerta", "2", "mg", 2);

        int result = laterAlarm.compareTo(earlierAlarm);

        assertTrue(result > 0);
    }

    @Test
    public void testCompareTo_SameTimeAlarmsShouldBeEqual() {
        MedicineAlarm alarm1 = new MedicineAlarm(1, 10, 30, "Paracetamole", "1", "mg", 1);
        MedicineAlarm alarm2 = new MedicineAlarm(2, 10, 30, "Concerta", "2", "mg", 2);

        int result = alarm1.compareTo(alarm2);

        assertEquals(0, result);
    }

    @Test
    public void testGetStringTime_FormatIsCorrect() {
        MedicineAlarm alarm = new MedicineAlarm(1, 14, 45, "Dexamole", "3", "mg", 3);

        String result = alarm.getStringTime();

        assertEquals("2:45 pm", result);
    }
}

