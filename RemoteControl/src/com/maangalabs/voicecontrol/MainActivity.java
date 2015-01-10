package com.maangalabs.voicecontrol;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "bluetooth1";

	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;

	private TextView txtSpeechInput, t1, t2;
	private ImageButton btnSpeak;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	// SPP UUID service
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805f9b34fb");

	// MAC-address of Bluetooth module
	private static String address = "00:13:01:11:22:50";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		 * This will create the socket- btSocket
		 */
		tryToConnect();

		txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
		t1 = (TextView) findViewById(R.id.textView2);
		t2 = (TextView) findViewById(R.id.textView1);
		Typeface font = Typeface.createFromAsset(getAssets(), "demo.otf");
		t1.setTypeface(font);
		t2.setTypeface(font);
		txtSpeechInput.setTypeface(font);

		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
			}
		});

		// hide the action bar
		// getActionBar().hide();
	}
	
	public void up(View v)
	{
		Toast.makeText(getApplicationContext(), "bot moving up",
				Toast.LENGTH_SHORT).show();
		sendData("1");
	}
	public void right(View v)
	{
		Toast.makeText(getApplicationContext(), "bot moving right",
				Toast.LENGTH_SHORT).show();
		sendData("3");
	}
	public void left(View v)
	{
		Toast.makeText(getApplicationContext(), "bot moving left",
				Toast.LENGTH_SHORT).show();
		sendData("4");	
	}
	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				txtSpeechInput.setText(result.get(0));
				if (result.get(0).contains("up")) {
					Toast.makeText(getApplicationContext(), "bot moving up",
							Toast.LENGTH_SHORT).show();
					sendData("1");
				} else if (result.get(0).contains("back")) {
					Toast.makeText(getApplicationContext(), "bot moving down",
							Toast.LENGTH_SHORT).show();
					sendData("2");
				} else if (result.get(0).contains("right")) {
					Toast.makeText(getApplicationContext(), "bot moving right",
							Toast.LENGTH_SHORT).show();
					sendData("3");
				} else if (result.get(0).contains("left")) {
					Toast.makeText(getApplicationContext(), "bot moving left",
							Toast.LENGTH_SHORT).show();
					sendData("4");
				} else {
					Toast.makeText(getApplicationContext(), "bot confused!",
							Toast.LENGTH_SHORT).show();
					sendData("0");
				}
			}
			break;
		}

		}
	}

	/*
	 * code to send data to socket bluetooth
	 */

	private void sendData(String message) {
		byte[] msgBuffer = message.getBytes();
		Log.d(TAG, "...Send data: " + message + "...");
		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {

			Toast.makeText(getApplicationContext(), "cannot" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * start of code for bluetooth socket creation
	 */
	private void checkBTState() {

		if (btAdapter == null) {
			errorExit("Fatal Error", "Bluetooth not support");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}

	private void errorExit(String title, String message) {
		Toast.makeText(getBaseContext(), title + " - " + message,
				Toast.LENGTH_LONG).show();
		finish();
	}

	private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
			throws IOException {
		// for higher versions the bluetooth stack is
		// different so use this code for higher versions
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				final Method m = device.getClass().getMethod(
						"createInsecureRfcommSocketToServiceRecord",
						new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection", e);
			}
		}
		return device.createRfcommSocketToServiceRecord(MY_UUID);
	}

	public void tryToConnect() {

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		checkBTState();

		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		try {
			btSocket = createBluetoothSocket(device);
		} catch (IOException e1) {
			errorExit("Fatal Error", "In onResume() and socket create failed: "
					+ e1.getMessage() + ".");
		}
		// socket connecting
		btAdapter.cancelDiscovery();
		Log.d(TAG, "...Connecting...");
		try {
			btSocket.connect();
			Log.d(TAG, "...Connection ok...");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}

		Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_LONG).show();
		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");

		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			errorExit(
					"Fatal Error",
					"In onResume() and output stream creation failed:"
							+ e.getMessage() + ".");
		}
	}

	/*
	 * end of the bluetooth socket creation
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
