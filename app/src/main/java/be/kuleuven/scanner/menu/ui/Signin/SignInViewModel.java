package be.kuleuven.scanner.menu.ui.Signin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignInViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public SignInViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is signin fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}