package com.oppous.anarko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.oppous.anarko.AlertView.AlertView;
import com.oppous.anarko.AlertView.OnItemClickListener;
import com.oppous.anarko.CreditCard.CreditCardForm;
import com.oppous.anarko.classes.AKProgressBar;
import com.oppous.anarko.classes.AppManager;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayCardActivity extends AppCompatActivity {

    private EditText txtEmail, txtName, txtAddress, txtCity, txtZip, txtCountry;
    private CreditCardForm creditCardForm;

    private FrameLayout layoutProgress;
    private AKProgressBar   progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_card);

        layoutProgress = (FrameLayout) findViewById(R.id.activity_payment_card_layoutProgress);
        layoutProgress.setVisibility(View.GONE);
        progress = (AKProgressBar) findViewById(R.id.activity_payment_card_progress);

        txtEmail = (EditText) findViewById(R.id.activity_payment_card_txtEmail);
        txtName = (EditText) findViewById(R.id.activity_payment_card_txtName);
        txtAddress = (EditText) findViewById(R.id.activity_payment_card_txtAddress);
        txtCity = (EditText) findViewById(R.id.activity_payment_card_txtCity);
        txtZip = (EditText) findViewById(R.id.activity_payment_card_txtZipCode);
        txtCountry = (EditText) findViewById(R.id.activity_payment_card_txtCountry);

        creditCardForm = (CreditCardForm) findViewById(R.id.activity_payment_cardForm);

        FrameLayout layoutCard = (FrameLayout) findViewById(R.id.activity_payment_card_layout);
        layoutCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    AppManager.hideKeyboard(PayCardActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

    // Private Methods
    public void saveCreditCard() {

        /*String cardNumber = creditCardForm.getCreditCard().getCardNumber();
        Integer expMonth = creditCardForm.getCreditCard().getExpMonth();
        Integer expYear = creditCardForm.getCreditCard().getExpYear();
        String cvcNumber = creditCardForm.getCreditCard().getSecurityCode();

        String name = txtName.getText().toString();
        String address = txtAddress.getText().toString();
        String city = txtCity.getText().toString();
        String zipCode = txtZip.getText().toString();
        String country = txtCountry.getText().toString();

        Card card = new Card(
                cardNumber,
                expMonth,
                expYear,
                cvcNumber,
                name,
                address,
                "",
                city,
                "",
                zipCode,
                country,
                "",
                "",
                "",
                country);
        card.setCurrency("usd");

        boolean validation = card.validateCard();
        if (validation) {
            startProgress();
            new Stripe().createToken(
                    card,
                    AKConstants.PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            String endingIn = getResources().getString(R.string.endingIn);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("last4", endingIn + " " + token.getCard().getLast4());
                            map.put("tokenId", token.getId());
                            map.put("type",creditCardForm.getCreditCard().getCardType().toString());
                            AppManager.getInstance().addToken(PayCardActivity.this, map, "stripeToken", "token");
                            finishProgress();
                        }
                        public void onError(Exception error) {
                            handleError(error.getLocalizedMessage());
                            finishProgress();
                        }
                    });

        } else if (!card.validateNumber()) {
            handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            handleError("The CVC code that you entered is invalid");
        } else {
            handleError("The card details that you entered are invalid");
        }*/
    }

    private void startProgress() {
        layoutProgress.setVisibility(View.VISIBLE);
        progress.startSpinning();
    }

    private void finishProgress(){
        layoutProgress.setVisibility(View.GONE);
        progress.stopSpinning();

        // TODO: 9/28/2016 Dismiss Activity with created Card
        finish();
    }

    private void handleError(String error) {
        Toast.makeText(this, "Error" + error, Toast.LENGTH_SHORT);
    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    // Button Event
    protected void onBack(View v) {
        AppManager.getInstance().hideKeyboard(this);
        finish();
    }

    protected void onDone(View v){

        AppManager.getInstance().hideKeyboard(this);

        boolean isValid = creditCardForm.isCreditCardValid();
        if (isValid){
            if (isEmailValid(txtEmail.getText().toString())){
                saveCreditCard();
            }else{
                txtEmail.setError(getResources().getString(R.string.payment_card_error_email));
            }

        }else {
            AlertView mAlertView = new AlertView("Warning", "Wrong Credit Card.", "Try Again", null, null, this, AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {

                }
            });

            mAlertView.setCancelable(true);
            mAlertView.show();
        }
    }

}
