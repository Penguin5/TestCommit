package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //Declares two text fields (e1 is the IP address and e2 is the message to send)
    EditText e1, e2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //constructs the variable
        e1 = (EditText)findViewById(R.id.editText);
        e2 = (EditText)findViewById(R.id.editText2);

        //Creates and starts a new thread to manage the server socket
        Thread myThread = new Thread(new MyServer());
        myThread.start();
    }

    //method when the button is clicked. When clicked, the app constructs and runs a new AsyncTask
    public void button_click(View V)
    {
        BackgroundTask b = new BackgroundTask();
        b.execute(e1.getText().toString(), e2.getText().toString());
    }

    class BackgroundTask extends AsyncTask<String, Void, String>
    {
        //declare variables
        Socket s;
        DataOutputStream dos;
        String ip, message;

        @Override
        protected String doInBackground(String... params)
        {
            //constructs variables based on parameters passed in the execute method
            ip = params[0];
            message = params[1];

            try
            {
                //creates a new socket and sends a message to a specified port and IP
                s = new Socket(ip, 9700);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);

                dos.close();
                s.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    class MyServer implements Runnable
    {
        //declares variables
        ServerSocket ss;
        Socket mySocket;
        DataInputStream dis;
        String message;
        Handler handler = new Handler();

        @Override
        public void run() {
            try
            {
                //creates a new server socket
                ss = new ServerSocket(9700);
                while (true){
                    //reads input from another device
                    mySocket = ss.accept();
                    dis = new DataInputStream(mySocket.getInputStream());
                    message = dis.readUTF();
                    //displays a message on the android tablet
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "message recieved from client: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch(IOException e){
                e.printStackTrace();
            }


        }
    }
}
