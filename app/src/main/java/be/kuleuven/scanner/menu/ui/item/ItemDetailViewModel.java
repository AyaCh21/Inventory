package be.kuleuven.scanner.menu.ui.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemDetailViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ItemDetailViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is cart fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}