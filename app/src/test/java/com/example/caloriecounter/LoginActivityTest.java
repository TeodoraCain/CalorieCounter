package com.example.caloriecounter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK}, manifest=Config.NONE)
public class LoginActivityTest {

    @Mock
    EditText mockedEmailEditText;

    @Mock
    EditText mockedPasswordEditText;

    @Mock
    Button mockedLoginButton;

    @Mock
    LoginActivity loginActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginActivity = new LoginActivity();
        loginActivity.email = mockedEmailEditText;
        loginActivity.password = mockedPasswordEditText;
        loginActivity.loginButton = mockedLoginButton;
    }

    //severitate medie
    @Test
    public void testEmptyEmail() {
        String newText = "";
        when(mockedEmailEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(newText));
        when(mockedPasswordEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(newText));

        boolean result = loginActivity.isLoginInputCorrect();

        verify(mockedEmailEditText).setError("Please enter email!");
        verify(mockedEmailEditText).requestFocus();
        verifyNoMoreInteractions(mockedLoginButton);
    }

    //severitate medie
    @Test
    public void testInvalidEmail() {
        String emailText = "invalid_email";
        String passwordText = "";
        when(mockedEmailEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(emailText));
        when(mockedPasswordEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(passwordText));

        boolean result = loginActivity.isLoginInputCorrect();

        verify(mockedEmailEditText).setError("Email is incorrect!");
        verify(mockedEmailEditText).requestFocus();
        verifyNoMoreInteractions(mockedLoginButton);
    }

    //severitate medie
    @Test
    public void testEmptyPassword() {
        String emailText = "valid@email.com";
        String passwordText = "";
        when(mockedEmailEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(emailText));
        when(mockedPasswordEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(passwordText));

        boolean result = loginActivity.isLoginInputCorrect();

        verify(mockedPasswordEditText).setError("Please enter password!");
        verify(mockedPasswordEditText).requestFocus();
        verifyNoMoreInteractions(mockedLoginButton);
    }

    //severitate scazuta
    @Test
    public void testValidInputs() {
        String emailText = "valid@email.com";
        String passwordText = "password";
        when(mockedEmailEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(emailText));
        when(mockedPasswordEditText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(passwordText));

        boolean result = loginActivity.isLoginInputCorrect();

        verify(mockedEmailEditText, never()).setError(anyString());
        verify(mockedPasswordEditText, never()).setError(anyString());
        verify(mockedEmailEditText, never()).requestFocus();
        verify(mockedPasswordEditText, never()).requestFocus();
    }
}
