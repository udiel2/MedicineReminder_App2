package com.example.MedReminder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.MedReminder.addmedicine.AddMedicinePresenter;

import static org.mockito.Mockito.*;

public class AddMedicinePresenterTest {

    private AddMedicinePresenter presenter;

    @Mock
    private MedicineDataSource mockRepository;

    @Mock
    private AddMedicineContract.View mockView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new AddMedicinePresenter(
            1,
            mockRepository,
            mockView,
            false
        );
    }

    @Test
    public void testSaveMedicine() {
        MedicineAlarm mockAlarm = new MedicineAlarm();
        Pills mockPills = new Pills();
        
        presenter.saveMedicine(mockAlarm, mockPills);
        
        verify(mockRepository).saveMedicine(mockAlarm, mockPills);
    }

    @Test
    public void testIsDataMissing() {
        boolean result = presenter.isDataMissing();
        assertFalse(result);
    }

    @Test
    public void testIsMedicineExits() {
        String mockPillName = "Paracetamole";
        when(mockRepository.medicineExits(mockPillName)).thenReturn(true);
        
        boolean result = presenter.isMedicineExits(mockPillName);
        
        assertTrue(result);
    }
}
