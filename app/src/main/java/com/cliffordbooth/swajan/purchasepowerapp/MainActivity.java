package com.cliffordbooth.swajan.purchasepowerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner spinner,spinner1,spinner2;
    private String[] spnr;
    private String[] spnr1;
    private String[] spnr2;
    TextView monthly_taxInsTV,monthly_total_taxInsTV,monthly_principalInsTV,aTV,bTV,cTV;
    String[] a = new String[3];
    String DEFAULT = ""+0.0;
    String temp = DEFAULT;
    int tempInt = 0;
    double sum=0.0,pv;
    int count=0;
    SettingActivity sa = new SettingActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = readSharedPreferenceInt("cntSP","cntKey");
        if(count==0){
            Toast.makeText(getApplicationContext(),"You are opening this first time. So plese set your annual income first",Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SettingActivity.class);
            intent.putExtra("key","start");
            startActivity(intent);
            count++;
            writeSharedPreference(count,"cntSP","cntKey");
        }

        // Spinner listener
        initialize();
        spinner();

        //Initialize TextView
        monthly_taxInsTV = (TextView) findViewById(R.id.monthlyTaxesIns);
        monthly_total_taxInsTV = (TextView) findViewById(R.id.totalTaxesIns);
        monthly_principalInsTV = (TextView) findViewById(R.id.principleInterest);
        aTV = (TextView) findViewById(R.id.monthlyPropertyTax1);
        bTV = (TextView) findViewById(R.id.monthlyHomeownersIns1);
        cTV = (TextView) findViewById(R.id.monthlyMortgageIns1);

        calculation();

    }

    public void onSetting(View view){
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        intent.putExtra("key","setting");
        startActivity(intent);
    }

    public void onReset(View view){
        Toast.makeText(getApplicationContext(),"Reset Done",Toast.LENGTH_SHORT).show();
        goInitialState();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Do you want to reset the full App?");
        //builder.setMessage(message);
        builder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        intent.putExtra("key","reset");
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("No", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
    public void onCheck(View view){
        calculation();
        showMessage("Present Value ",""+pv);

    }

    public void goInitialState() {
        spinner.setSelection(0);
        spinner1.setSelection(0);
        spinner2.setSelection(0);
        /*for(int i=0;i<3;i++){
            writeSharedPreference(""+0.0,"sp"+i,"sp"+i+"k");
        }*/
        calculation();


    }
    public void calculation(){
        sum = 0;
        for(int i=0;i<3;i++){
            a[i]=readSharedPreference("MM"+i,"NN"+i);
            sum += Double.parseDouble(a[i]);
        }
        monthly_taxInsTV.setText("" + new DecimalFormat("##.##").format(sum));
        double s = sum+Double.parseDouble(readSharedPreference("BB"+4,"KK"+4));
        monthly_total_taxInsTV.setText(""+(new DecimalFormat("##.##").format(s)));
        double a,b,c,annualIncome,spnr,spnr1,spnr2;  //a = Property,b=Homeowners, c=Mortgage, spnr = Probable Max DTI, spnr1 = Interest Rate,
        a = Double.parseDouble(readSharedPreference("MM" + 0, "NN" + 0));
        b = Double.parseDouble(readSharedPreference("MM" + 1, "NN" + 1));
        c = Double.parseDouble(readSharedPreference("MM" + 2, "NN" + 2));
        annualIncome = Double.parseDouble(readSharedPreference("BB"+0,"KK"+0));
        spnr = Double.parseDouble(readSharedPreference("sp0","sp0k"));
        spnr1 = Double.parseDouble(readSharedPreference("sp1","sp1k"));
        spnr2 = Double.parseDouble(readSharedPreference("sp2","sp2k"));

        double calc = (((annualIncome/12)*spnr)-s);
        monthly_principalInsTV.setText(""+(new DecimalFormat("##.##").format(calc)));

        aTV.setText("Monthly Property Taxes : " + new DecimalFormat("##.##").format(a));
        bTV.setText("Monthly Homeowners Insurance : " + new DecimalFormat("##.##").format(b));

        if(c==0.0){
            cTV.setText("Monthly Mortgage Insurance : Not Required");
        }else{
            cTV.setText("Monthly Mortgage Insurance : "+new DecimalFormat("##.##").format(c));
        }

        pv = (double) Math.round(presentValue(calc,spnr1,spnr2)*100.0)/100.0;
    }
    public double presentValue(double principal, double yearlyRate, double termYears)
    {
        //http://www.dreamincode.net/forums/topic/31408-present-value-formula-is-not-working/
        // Do math and return result
        double pValue = principal * (((1- Math.pow(1 + (yearlyRate/12), -(termYears*12)))/ (yearlyRate/12)));
        return pValue;
    }

    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
    //work with the spinners
    public void spinner(){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                              @Override
                                              public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {

                                                  writeSharedPreference(arg0.getSelectedItem().toString(),"sp0","sp0k");
                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> arg0) {
                                              }
                                          });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> arg0, View view,int arg2, long arg3) {

                                                   writeSharedPreference(arg0.getSelectedItem().toString(),"sp1","sp1k");
                                               }
                                               @Override
                                               public void onNothingSelected(AdapterView<?> arg0) {
                                               }
                                           });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                @Override
                                                public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {

                                                    writeSharedPreference(arg0.getSelectedItem().toString(),"sp2","sp2k");
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> arg0) {

                                                }
                                            });
    }


    public void initialize(){
        //initialize view
        spinner = (Spinner) findViewById(R.id.spnrPorbableMaxDTI);
        spinner1 = (Spinner) findViewById(R.id.spnrInterestRate);
        spinner2 = (Spinner) findViewById(R.id.spnrAmortizationTerm);

        //initialize data source
        spnr = getResources().getStringArray(R.array.Examination);
        spnr1 = getResources().getStringArray(R.array.YearArray);
        spnr2 = getResources().getStringArray(R.array.Board);



        //initialize view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spnr);
        ArrayAdapter<String> adapter1= new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spnr1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spnr2);


        //bind adapter and view
        spinner.setAdapter(adapter);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
    }





    @Override
    protected void onStart() {
        calculation();
        super.onStart();
    }

    @Override
    protected void onRestart() {
       calculation();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        calculation();
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


}
