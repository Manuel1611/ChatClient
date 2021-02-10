package com.example.chatclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText etTexto;
    private Button btEnviar, btSalir;
    private TextView tvTexto;

    private boolean run = true;
    private boolean dialogBool;
    private Thread listeningThread;
    private Socket client;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private String texto;
    private Dialog nombreDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {

        etTexto = findViewById(R.id.etTexto);
        btEnviar = findViewById(R.id.btEnviar);
        btSalir = findViewById(R.id.btSalir);
        tvTexto = findViewById(R.id.tvTexto);

        if(savedInstanceState != null) {

            tvTexto.setText(savedInstanceState.getString("stringTexto"));
            etTexto.setText(savedInstanceState.getString("stringEditText"));
            dialogBool = savedInstanceState.getBoolean("dialogBool");

        }

        if(!dialogBool) {

            dialogNombre();

        }

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = etTexto.getText().toString();

                new Thread() {

                    @Override
                    public void run() {
                        sendText(text);
                    }
                }.start();

                etTexto.setText("");

            }
        });

        btSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {

                    @Override
                    public void run() {
                        salirApp();
                    }
                }.start();

            }
        });

        Thread thread = new Thread() {

            @Override
            public void run() {

                startClient("10.0.2.2", 5000);

            }
        };
        thread.start();

    }

    private void sendText(String text) {

        try {

            if(!(text.compareTo("") == 0 || text.compareTo(" ") == 0)) {

                flujoS.writeUTF(text);

            }

        } catch (IOException ex) {

        }

    }

    private void salirApp() {

        try{

            flujoS.writeUTF("sdfjbsidfhjbfuidhbsdfbuigusrfiuwu9wqsdhbasklbndeuwyvfosdfaujqw3ebibwefiyasnjkasbpfue");

        } catch (IOException e) {

        }
        System.exit(0);

    }

    public void startClient(String host, int port) {

        try {

            client = new Socket(host, port);
            flujoE = new DataInputStream(client.getInputStream());
            flujoS = new DataOutputStream(client.getOutputStream());

            listeningThread = new Thread() {

                @Override
                public void run() {

                    while (run) {

                        try {

                            texto = flujoE.readUTF();
                            tvTexto.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvTexto.append(texto + "\n");
                                }
                            });

                        } catch (IOException e) {

                        }

                    }

                }
            };
            listeningThread.start();

        } catch (IOException e) {

        }

    }

    private void dialogNombre() {

        nombreDialog = new Dialog(MainActivity.this);
        nombreDialog.setContentView(R.layout.nombre_dialog);
        nombreDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = nombreDialog.getWindow();
        window.setGravity(Gravity.CENTER);

        Button btSalirDialog = nombreDialog.findViewById(R.id.btSalirDialog);
        Button btAceptarDialog = nombreDialog.findViewById(R.id.btEntrarDialog);
        EditText etNombre = nombreDialog.findViewById(R.id.etNombreDialog);
        TextView tvAlerta = nombreDialog.findViewById(R.id.tvAlertaDialog);

        btSalirDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nombreDialog.dismiss();
                System.exit(0);

            }
        });

        btAceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = etNombre.getText().toString();

                if(nombre.isEmpty()) {
                    tvAlerta.setText("Debes introducir un nombre");
                } else {
                    new Thread() {

                        @Override
                        public void run() {
                            sendText(nombre);
                        }
                    }.start();
                    dialogBool = true;
                    nombreDialog.dismiss();

                }

            }
        });

        nombreDialog.setCancelable(true);
        nombreDialog.setCanceledOnTouchOutside(false);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        nombreDialog.show();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("stringTexto", tvTexto.getText().toString());
        outState.putString("stringEditText", etTexto.getText().toString());
        outState.putBoolean("dialogBool", dialogBool);

        super.onSaveInstanceState(outState);
    }

}