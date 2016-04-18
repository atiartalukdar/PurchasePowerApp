package com.cliffordbooth.swajan.purchasepowerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class SettingActivity extends AppCompatActivity {

    EditText a_income,p_taxRate,h_insRate,m_insRate,inslmentRevDebt,multiplier;
    TextView monthly_p_taxTV,monthly_h_insTV,monthly_m_insTV;
    String[] a = new String[6];
    String DEFAULT = ""+0.0;
    String temp = DEFAULT,radioText="";
    int tempInt = 0;
    private RadioGroup radioGroup;
    RadioButton radioButtonYes,radioButtonNo;
    int count=0;
    String intent_data="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Enabling the back button on the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Initialize Radio Group and attach click handler */
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtonYes = (RadioButton) findViewById(R.id.radioButtonYes);
        radioButtonNo = (RadioButton) findViewById(R.id.radioButtonNo);
        radioGroup.clearCheck();


        //Initialize TextView
        monthly_p_taxTV = (TextView) findViewById(R.id.monthlyPropertyTax);
        monthly_h_insTV = (TextView) findViewById(R.id.monthlyHomeownersIns);
        monthly_m_insTV = (TextView) findViewById(R.id.monthlyMortgageIns);

        //Initialize EditText
        a_income = (EditText) findViewById(R.id.annualIncome);
        p_taxRate = (EditText) findViewById(R.id.propertyTaxRate);
        h_insRate = (EditText) findViewById(R.id.homeownerInsuranceRate);
        m_insRate = (EditText) findViewById(R.id.mortgageInsuranceRate);
        inslmentRevDebt = (EditText) findViewById(R.id.installmentRevolvingDebt);
        multiplier = (EditText) findViewById(R.id.multiplierTV);

        //setting default radioButton on startUp
        radioText = readSharedPreference("rSP","rSPk");
        if(radioText.equals("Yes")){
            radioButtonYes.setChecked(true);
            radioButtonNo.setChecked(false);
            m_insRate.setEnabled(true);
            double d = Double.parseDouble(readSharedPreference("BB"+3, "KK"+3));
            m_insRate.setText("" + d);
        }
        else{
            radioButtonYes.setChecked(false);
            radioButtonNo.setChecked(true);
            m_insRate.setEnabled(false);
            m_insRate.setText("Not Required");
        }

        /* Attach CheckedChangeListener to radio group */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                radioText = rb.getText().toString();
                writeSharedPreference(""+radioText,"rSP","rSPk");
                if(null!=rb && checkedId > -1){
                    Toast.makeText(getApplicationContext(),radioText,Toast.LENGTH_SHORT).show();
                    if (radioText.equals("Yes")){
                        m_insRate.setEnabled(true);
                        double d = Double.parseDouble(readSharedPreference("BB"+3, "KK"+3));
                        if(d==0.0){
                            m_insRate.setText(""+0.0085);
                        }else{
                            m_insRate.setText("" + d);
                        }
                    }
                    else{
                        m_insRate.setText("Not Required");
                        m_insRate.setEnabled(false);
                    }
                }

            }
        });




        for(int j = 0;j<6;j++){
            a[j]=readSharedPreference("BB"+j,"KK"+j);
        }
        a_income.setText(a[0]);
        p_taxRate.setText(a[1]);
        h_insRate.setText(a[2]);
        m_insRate.setText(a[3]);
        inslmentRevDebt.setText(a[4]);
        multiplier.setText(a[5]);
        settingCalculation();

        //Check the app state that it is in the first stage or not
        count = readSharedPreferenceInt("cntSPs", "cntKeys");
        if(count==0){
            reset();
            count++;
            writeSharedPreference(count,"cntSPs","cntKeys");
            showMessage("Please set Your Income first");
        }
        intent_data = getIntent().getExtras().getString("key");
        if(intent_data.equals("reset")){
            reset();
            showMessage("Please set Your Income first");
        }
    }
    public void showMessage(String title)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.setMessage(message);
        builder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    public void resetBtn(View view){
        reset();
    }

    public void reset(){
        a_income.setText("" + 0);
        p_taxRate.setText("" + 0.0114);
        h_insRate.setText("" + 0.0047);
        m_insRate.setText("Not Required");
        inslmentRevDebt.setText("" + 0.0);
        multiplier.setText("" + 5.0);
        writeSharedPreference("" + 0.0, "BB" + 0, "KK" + 0);
        writeSharedPreference("" + 0.0114, "BB" + 1, "KK" + 1);
        writeSharedPreference("" + 0.0047, "BB" + 2, "KK" + 2);
        writeSharedPreference("" + 0.0085, "BB" + 3, "KK" + 3);
        // radioGroup.check(-1);0.0085
        radioButtonYes.setChecked(false);
        radioButtonNo.setChecked(true);
        writeSharedPreference("" + 0.0, "BB" + 4, "KK" + 4);
        writeSharedPreference("" + 5.0, "BB" + 5, "KK" + 5);
        settingCalculation();
    }

    public void upDateBtn(View view){
        a[0] = a_income.getText().toString().trim();
        a[1] = p_taxRate.getText().toString().trim();
        a[2] = h_insRate.getText().toString().trim();
        a[3] = m_insRate.getText().toString().trim();
        a[4] = inslmentRevDebt.getText().toString().trim();
        a[5] = multiplier.getText().toString().trim();

        for (int i=0;i<6;i++){
            if(a[i].equals("Not Required")){
                writeSharedPreference(a[i],"NR"+i,"RN"+i);
            }else{
                writeSharedPreference(a[i],"BB"+i,"KK"+i);
            }

        }

        settingCalculation();
        Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();

    }

    protected void settingCalculation(){
        double a_income=0.0,p_txRate=0.0,h_txRate=0.0,m_txRate=0.0,monthly_p_tx=0.0,monthly_h_ins=0.0,monthly_m_ins=0.0,mult;
        a_income = Double.parseDouble(readSharedPreference("BB"+0,"KK"+0));
        p_txRate = Double.parseDouble(readSharedPreference("BB"+1,"KK"+1));
        h_txRate = Double.parseDouble(readSharedPreference("BB" + 2, "KK" + 2));
        m_txRate = Double.parseDouble(readSharedPreference("BB" + 3, "KK" + 3));
        mult = Double.parseDouble(readSharedPreference("BB" + 5, "KK" + 5));

        monthly_p_tx = ((a_income*mult*p_txRate)/12);
        monthly_p_taxTV.setText("Monthly Property Taxes : " + new DecimalFormat("##.##").format(monthly_p_tx));
        writeSharedPreference("" + monthly_p_tx, "MM" + 0, "NN" + 0);

        monthly_h_ins = ((a_income*mult*h_txRate)/12);
        monthly_h_insTV.setText("Monthly Homeowners Insurance : " + new DecimalFormat("##.##").format(monthly_h_ins));
        writeSharedPreference("" + monthly_h_ins, "MM" + 1, "NN" + 1);

        monthly_m_ins = ((a_income*mult*m_txRate)/12);
        if(radioText.equals("Yes")){
            monthly_m_insTV.setText("Monthly Mortgage Insurance : " + new DecimalFormat("##.##").format(monthly_m_ins));
            writeSharedPreference("" + monthly_m_ins, "MM" + 2, "NN" + 2);
        }else{
            monthly_m_insTV.setText("Monthly Mortgage Insurance : Not Required");
            writeSharedPreference("" + 0.0, "MM" + 2, "NN" + 2);
        }
    }






    //write Shared Preferences in String
    public void writeSharedPreference(String string,String spName,String key ){

        //income = Integer.parseInt(15);
        SharedPreferences sharedPreferences = getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, string);
        editor.commit();
    }


    //Read the shareadPreference data in String
    public String readSharedPreference(String spName,String key){
        SharedPreferences sharedPreferences = getSharedPreferences(spName, Context.MODE_PRIVATE);
        return temp = sharedPreferences.getString(key, DEFAULT);
    }
    public int readSharedPreferenceInt(String spName,String key){
        SharedPreferences sharedPreferences = getSharedPreferences(spName, Context.MODE_PRIVATE);
        return tempInt = sharedPreferences.getInt(key, 0);
    }

    //write shared preferences in integer
    public void writeSharedPreference(int ammount,String spName,String key ){

        //income = Integer.parseInt(ammount);
        SharedPreferences sharedPreferences = getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, ammount);
        editor.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
