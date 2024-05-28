package ro.pub.cs.systems.eim.practicaltest02.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button startButton;
    private EditText cuvantEditText;
    private EditText clientPortEditText;
    private Button getButton;

    private TextView resultText;


    private ServerThread serverThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private class ConnectButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private final GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String cuvant = cuvantEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (cuvant.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Cuvant field should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            resultText.setText(Constants.EMPTY_STRING);

            ClientThread clientThread = new ClientThread(
                    "127.0.0.1", Integer.parseInt(clientPort), cuvant, resultText
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "onCreate() callback method was invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.editTextPortServer);
        serverPortEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                serverPortEditText.setText("");
            }
        });
        startButton = (Button)findViewById(R.id.buttonStart);
        startButton.setOnClickListener(connectButtonClickListener);
        cuvantEditText = (EditText)findViewById(R.id.editTextCuvant);
        cuvantEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                cuvantEditText.setText("");
            }
        });
        clientPortEditText = (EditText)findViewById(R.id.editTextPortClient);
        clientPortEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                clientPortEditText.setText("");
            }
        });
        getButton = (Button)findViewById(R.id.buttonGet);
        getButton.setOnClickListener(getWeatherForecastButtonClickListener);
        resultText = (TextView)findViewById(R.id.resultText);

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "onDestroy() callback method was invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}