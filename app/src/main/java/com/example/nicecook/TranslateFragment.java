package com.example.nicecook;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText txtInput;
    private TextView txtResult, txtLanguage1, txtLanguage2;
    private OkHttpClient client = new OkHttpClient();
    private ImageButton btnTranslate, btnSpeechToText;
    private ImageView btnSpeak1, btnSpeak2, btnCopy1, btnCopy2;
    private int REQUEST_CODE_INPUT = 100;


    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TranslateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TranslateFragment newInstance(String param1, String param2) {
        TranslateFragment fragment = new TranslateFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        txtInput = view.findViewById(R.id.txtInput);
        txtResult = view.findViewById(R.id.txtResult);
        btnTranslate = view.findViewById(R.id.btnTranslate);
        txtLanguage1 = view.findViewById(R.id.txtLanguage1);
        txtLanguage2 = view.findViewById(R.id.txtLanguage2);
        btnSpeak1 = view.findViewById(R.id.btnSpeak1);
        btnSpeak2 = view.findViewById(R.id.btnSpeak2);
        btnCopy1 = view.findViewById(R.id.btnCopy1);
        btnCopy2 = view.findViewById(R.id.btnCopy2);
        btnSpeechToText = view.findViewById(R.id.btnSpeechToText);

        TextToSpeech textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String language1 = txtLanguage1.getText().toString();
                String language2 = txtLanguage2.getText().toString();
                String input = txtInput.getText().toString();
                if(language1.equals("")) {
                    Toast.makeText(getContext(), "Enter a language to translate from.", Toast.LENGTH_SHORT).show();
                } else if(language2.equals("")) {
                    Toast.makeText(getContext(), "Enter a language to translate to.", Toast.LENGTH_SHORT).show();
                } else if(input.equals("")) {
                    Toast.makeText(getContext(), "Enter a text to translate.", Toast.LENGTH_SHORT).show();
                } else {
                    callApi(language1, language2, input);
                }
            }
        });

        btnSpeak1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txtInput.getText().toString();
                if(message.equals("")) {
                    Toast.makeText(getContext(), "There's nothing to say.", Toast.LENGTH_SHORT).show();
                } else {
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        btnSpeak2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txtResult.getText().toString();
                if(message.equals("")) {
                    Toast.makeText(getContext(), "There's nothing to say.", Toast.LENGTH_SHORT).show();
                } else {
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        btnCopy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txtInput.getText().toString();
                if(text.equals("")) {
                    Toast.makeText(getContext(), "There's nothing to copy.", Toast.LENGTH_SHORT).show();
                } else {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("URL", text);
                    clipboard.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), "Link copied to your clipboard!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCopy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txtResult.getText().toString();
                if(text.equals("")) {
                    Toast.makeText(getContext(), "There's nothing to copy.", Toast.LENGTH_SHORT).show();
                } else {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("URL", text);
                    clipboard.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), "Link copied to your clipboard!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
                try {
                    startActivityForResult(intent, REQUEST_CODE_INPUT);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtInput.setText(arrayList.get(0));
            }
        }
    }

    private void addResponse(String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                txtResult.setText(msg);
            }
        });
    }

    private void callApi(String lang1, String lang2, String message) {
        JSONObject jsonBody = new JSONObject();
        StringBuilder sb = new StringBuilder();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", sb.append("Translate this text from ").append(lang1).append(" to ").append(lang2).append(", but if either one of the languages is not a language respond with 'That's not a language please try again.': ").append(message).toString());
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        String apiKey = "";
        try {
            ApplicationInfo applicationInfo = getActivity().getApplication().getPackageManager()
                    .getApplicationInfo(getActivity().getApplication().getPackageName(), PackageManager.GET_META_DATA);
            apiKey = applicationInfo.metaData.getString("OPENAI_KEY");
            // Use the apiKey as needed
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("hello");
                    }

                } else {
                    addResponse("Failed to load response due to " + response.body().toString());
                }
            }
        });
    }
}