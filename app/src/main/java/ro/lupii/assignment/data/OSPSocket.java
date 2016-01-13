package ro.lupii.assignment.data;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import ro.lupii.assignment.R;

/**
 * Created by jon on 1/9/16.
 */
public class OSPSocket {
    static private final int port=4000;

    /* !!! Modify me !!! */
    static private final String host="10.0.2.1";

 //   private int port;
   // private String host;

    private Socket socket;

    //private BufferedOutputStream outputStream;
    private PrintWriter pw;
    private BufferedReader br;


    public OSPSocket() throws IOException {
        InputStreamReader iSR;
        BufferedOutputStream bOS;

        System.out.println("xxx");
        this.socket = new Socket(this.host, this.port);
        System.out.println("xxx");


        iSR = new InputStreamReader(this.socket.getInputStream());
        this.br = new BufferedReader(iSR);

        bOS = new BufferedOutputStream(this.socket.getOutputStream());
        this.pw = new PrintWriter(bOS);

    }

    public  String readAll() throws IOException {
        /* FIXME more efficient please */
        char c;
        String res="";

        if (this.br == null) {
            Log.e("OSP", "Can't read!BufferedReader is null!");
            return null;
        }

        while ((c=(char)this.br.read()) !='\0')
            res += c;

        return res;
    }

    public void writeString(String s) {
        if (this.pw == null) {
            Log.e("OSP", "Can't write!PrintWriter is null!");
            return;
        }

        this.pw.write(s+'\0');
        this.pw.flush();
    }

    public void setSockTimeout(int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public boolean isNull() {
        return this.socket == null;
    }

}
