package org.techtown.soolzipinjejuu;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoolZipFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoolZipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoolZipFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SoolZipFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SoolZipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoolZipFragment newInstance(String param1, String param2) {
        SoolZipFragment fragment = new SoolZipFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayAdapter typeAdapter;
    private Spinner typeSpinner;
    private ArrayAdapter areaAdapter;
    private Spinner areaSpinner;
    private ArrayAdapter soolzipAdapter;
    private Spinner soolzipSpinner;


    private String courseWhere ="";
    private String courseType ="";
    private String courseArea ="";

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        final RadioGroup soolzipGroup = (RadioGroup) getView().findViewById(R.id.soolzipGroup);
        typeSpinner = (Spinner) getView().findViewById(R.id.typeSpinner);
        areaSpinner = (Spinner) getView().findViewById(R.id.areaSpinner);
        soolzipSpinner = (Spinner) getView().findViewById(R.id.soolzipSpinner);

        soolzipGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

           @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               RadioButton courseButton = (RadioButton) getView().findViewById(i);
               courseWhere = courseButton.getText().toString();

               typeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.type, android.R.layout.simple_spinner_dropdown_item);
               typeSpinner.setAdapter(typeAdapter);

               if(courseWhere.equals("제주시"))
               {
                   areaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.jejusiArea, android.R.layout.simple_spinner_dropdown_item);
                   areaSpinner.setAdapter(areaAdapter);

                   soolzipAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sichungZip, android.R.layout.simple_spinner_dropdown_item);
                   soolzipSpinner.setAdapter(soolzipAdapter);
               } else if(courseWhere.equals("서귀포시"))
               {
                   areaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.seogwipoArea, android.R.layout.simple_spinner_dropdown_item);
                   areaSpinner.setAdapter(areaAdapter);

                   soolzipAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.seogwipoZip, android.R.layout.simple_spinner_dropdown_item);
                   soolzipSpinner.setAdapter(soolzipAdapter);
               }
           }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(areaSpinner.getSelectedItem().equals("시청"))
                {
                    soolzipAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sichungZip, android.R.layout.simple_spinner_dropdown_item);
                    soolzipSpinner.setAdapter(soolzipAdapter);
                } else if(areaSpinner.getSelectedItem().equals("제주지역2"))
                {
                    soolzipAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.jeju2Zip, android.R.layout.simple_spinner_dropdown_item);
                    soolzipSpinner.setAdapter(soolzipAdapter);
                } else if(areaSpinner.getSelectedItem().equals("서귀포지역1"))
                {
                    soolzipAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.seogwipoZip, android.R.layout.simple_spinner_dropdown_item);
                    soolzipSpinner.setAdapter(soolzipAdapter);
                } else if(areaSpinner.getSelectedItem().equals("서귀포지역2"))
                {
                    soolzipAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.seogwipo2Zip, android.R.layout.simple_spinner_dropdown_item);
                    soolzipSpinner.setAdapter(soolzipAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button searchButton = (Button) getView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sool_zip, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            try{
                target = "http://lmh5920.cafe24.com/SoolzipList.php?courseWhere=" + URLEncoder.encode(courseWhere, "UTF-8") +
                        "&courseArea=" + URLEncoder.encode(areaSpinner.getSelectedItem().toString(), "UTF-8") +
                        "&courseType=" + URLEncoder.encode(typeSpinner.getSelectedItem().toString(), "UTF-8");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) !=null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try{
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(SoolZipFragment.this.getContext());
                dialog = builder.setMessage(result)
                        .setPositiveButton("확인", null)
                        .create();
                dialog.show();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
