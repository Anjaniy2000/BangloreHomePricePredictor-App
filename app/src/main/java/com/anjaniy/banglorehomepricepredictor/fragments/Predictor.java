package com.anjaniy.banglorehomepricepredictor.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.anjaniy.banglorehomepricepredictor.R;
import com.anjaniy.banglorehomepricepredictor.models.Prediction;
import com.anjaniy.banglorehomepricepredictor.singleton.MySingleTon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Predictor extends Fragment {

    private EditText sqft;
    private Spinner bhk;
    private Spinner bath;
    private Spinner balcony;
    private Spinner locations;
    private Button estimatePrice;

    private String[] BHK_Array = new String[8];
    private String[] BATH_Array = new String[8];
    private String[] BALCONY_Array = new String[8];
    private String[] LOCATION_Names = new String[229];

    private String sqftSelected = "";
    private String bhkSelected = "";
    private String bathSelected = "";
    private String  balconySelected = "";
    private String locationSelected = "";

    private View view;
    private ProgressDialog dialogLocations;
    private ProgressDialog dialogPrediction;

    private String Result = "";

    private final String LOCATIONS_URL = "https://bhpp-backend.herokuapp.com/get_location_names";
    private final String ESTIMATE_PRICE_URL = "https://bhpp-backend.herokuapp.com/predict_home_price";

    private FirebaseAuth auth;
    private FirebaseFirestore database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (inflater.inflate(R.layout.fragment_predictor,container,false));
        widgetSetup();
        spinnerSetup();
        getLocations();

        bhk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bhkSelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bathSelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        balcony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                balconySelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                locationSelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        estimatePrice.setOnClickListener(v -> {
            showProgressDialogPrediction();
            sqftSelected = sqft.getText().toString().trim();

            if(sqftSelected.isEmpty()){
                sqft.setError("Square Foot Area Is Required!");
                sqft.requestFocus();
                dismissDialogPrediction();
                return;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ESTIMATE_PRICE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    parseResponse(response);

                }
            }, error -> Log.d("tag", "onErrorResponse: " + error.getMessage())){

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    params.put("total_sqft",sqftSelected);
                    params.put("location",locationSelected);
                    params.put("bhk",bhkSelected);
                    params.put("bath",bathSelected);
                    params.put("balcony",balconySelected);

                    return params;
                }
            };
            MySingleTon.getInstance(getActivity()).addToRequestQueue(stringRequest);
        });
        return view;
    }

    private void getLocations() {

        showProgressDialogLocations();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LOCATIONS_URL, null, response -> {

            try {

                JSONArray jsonArray = response.getJSONArray("locations");
                int length = jsonArray.length();
                for(int i = 0 ; i < length ; i++){
                    LOCATION_Names[i] = jsonArray.getString(i).toUpperCase();

                    ArrayAdapter<String> spinnerArrayAdapter_LOCATIONS = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, LOCATION_Names);
                    spinnerArrayAdapter_LOCATIONS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    locations.setAdapter(spinnerArrayAdapter_LOCATIONS);
                    dismissDialogLocations();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("tag", "onErrorResponse: " + error.getMessage()));

        MySingleTon.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private void spinnerSetup() {
        BHK_Array = getResources().getStringArray(R.array.bhk_array);
        BATH_Array =  getResources().getStringArray(R.array.bath_array);
        BALCONY_Array = getResources().getStringArray(R.array.balcony_array);

        ArrayAdapter<String> spinnerArrayAdapter_BHK = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, BHK_Array);
        spinnerArrayAdapter_BHK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        bhk.setAdapter(spinnerArrayAdapter_BHK);

        ArrayAdapter<String> spinnerArrayAdapter_BATH = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, BATH_Array);
        spinnerArrayAdapter_BATH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        bath.setAdapter(spinnerArrayAdapter_BATH);

        ArrayAdapter<String> spinnerArrayAdapter_BALCONY = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, BALCONY_Array);
        spinnerArrayAdapter_BALCONY.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        balcony.setAdapter(spinnerArrayAdapter_BALCONY);
    }

    private void widgetSetup() {
        sqft = view.findViewById(R.id.sqft);
        bhk = view.findViewById(R.id.bhk);
        bath = view.findViewById(R.id.bath);
        balcony = view.findViewById(R.id.balcony);
        locations = view.findViewById(R.id.location_names);
        estimatePrice = view.findViewById(R.id.estimate_price_btn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    private void showProgressDialogLocations() {
        dialogLocations = new ProgressDialog(getActivity());
        dialogLocations.show();
        dialogLocations.setContentView(R.layout.progress_dialog_locations);
        dialogLocations.setCanceledOnTouchOutside(false);
        dialogLocations.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismissDialogLocations() {
        dialogLocations.dismiss();
    }

    private void showProgressDialogPrediction() {
        dialogPrediction = new ProgressDialog(getActivity());
        dialogPrediction.show();
        dialogPrediction.setContentView(R.layout.progress_dialog_prediction);
        dialogPrediction.setCanceledOnTouchOutside(false);
        dialogPrediction.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismissDialogPrediction() {
        dialogPrediction.dismiss();
    }

    private void parseResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);

            Result = jsonObject.getString("estimated_price");

            dismissDialogPrediction();

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);

            builder.setMessage(Result + " " + "Lakhs")

                    .setCancelable(false)

                    //CODE FOR POSITIVE(YES) BUTTON: -
                    .setPositiveButton("Save Prediction", (dialog, which) -> {
                        //ACTION FOR "YES" BUTTON: -
                        Prediction prediction = new Prediction();
                        prediction.setEmailAddress(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                        prediction.setPrice(Result + "Lakhs");
                        prediction.setLocation(locationSelected);
                        prediction.setBalcony(balconySelected);
                        prediction.setSqft(sqftSelected);
                        prediction.setBath(bathSelected);
                        prediction.setBhk(bhkSelected);
                        prediction.setUuid(UUID.randomUUID().toString());

                        database
                                .collection("Predictions")
                                .document(prediction.getUuid())
                                .set(prediction).addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Successfully Saved!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show());

                    })

                    //CODE FOR NEGATIVE(NO) BUTTON: -
                    .setNegativeButton("Ok", (dialog, which) -> {
                        //ACTION FOR "NO" BUTTON: -
                        dialog.cancel();

                    });

            //CREATING A DIALOG-BOX: -
            AlertDialog alertDialog = builder.create();
            //SET TITLE MAUALLY: -
            alertDialog.setTitle("Estimated Price");
            alertDialog.show();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
