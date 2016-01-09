package ro.lupii.assignment.data;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ro.lupii.assignment.R;

/**
 * Created by root on 1/9/16.
 */
public class OSPSocket {
    private int port;
    private String host;

    private Socket socket;

    //private BufferedOutputStream outputStream;
    private PrintWriter pw;
    private BufferedReader br;


    public OSPSocket() throws IOException {
        InputStreamReader iSR;
        BufferedOutputStream bOS;

        /* FIXME */
        this.port = R.integer.server_port;
        this.host = "192.168.42.193";//R.string.server_host;
        /**/

        this.socket = new Socket(this.host, this.port);


        iSR = new InputStreamReader(this.socket.getInputStream());
        this.br = new BufferedReader(iSR);

        bOS = new BufferedOutputStream(this.socket.getOutputStream());
        this.pw = new PrintWriter(bOS, true);
    }

    public  String readLine() throws IOException {
        return this.br.readLine();
    }

    public void writeString(String s) {
        this.pw.write(s);
    }

}