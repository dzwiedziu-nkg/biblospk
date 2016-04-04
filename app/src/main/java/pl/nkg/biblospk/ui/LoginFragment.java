package pl.nkg.biblospk.ui;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import pl.nkg.biblospk.R;

public class LoginFragment extends Fragment {

    private static final String ARG_LOGIN = "login";
    private static final String ARG_PASSWORD = "password";

    private OnFragmentInteractionListener mListener;
    @Bind(R.id.input_login) EditText mLoginEditText;
    @Bind(R.id.input_password) EditText mPasswordEditText;
    @Bind(R.id.msg_error) TextView mErrorTextView;
    @Bind(R.id.btn_login) AppCompatButton mLoginButton;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;

    public static LoginFragment newInstance(String login, String password) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOGIN, login);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        mLoginEditText.setText(getArguments().getString(ARG_LOGIN));
        mPasswordEditText.setText(getArguments().getString(ARG_PASSWORD));
        return view;
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        setError(null);
        if (mListener != null && validate()) {
            mListener.onLoginClick(getLogin(), getPassword());
        }
    }

    @OnEditorAction(R.id.input_password)
    boolean onPasswordEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            onLoginClick();
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean validate() {
        boolean valid = true;

        String login = getLogin();
        String password = getPassword();

        if (login.isEmpty()) {
            mLoginEditText.setError(getText(R.string.error_empty_login));
            valid = false;
        } else {
            mLoginEditText.setError(null);
        }

        if (password.isEmpty()) {
            mPasswordEditText.setError(getText(R.string.error_empty_password));
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    public void setError(CharSequence errorMessage) {
        mErrorTextView.setText(errorMessage);
    }

    public void setRunning(boolean running) {
        mLoginButton.setVisibility(running ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        mLoginEditText.setEnabled(!running);
        mPasswordEditText.setEnabled(!running);

    }

    public String getLogin() {
        return StringUtils.trim(mLoginEditText.getText().toString());
    }

    public String getPassword() {
        return mPasswordEditText.getText().toString();
    }

    public interface OnFragmentInteractionListener {
        void onLoginClick(String login, String password);
    }
}
