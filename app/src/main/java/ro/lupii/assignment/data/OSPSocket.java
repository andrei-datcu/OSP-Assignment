package ro.lupii.assignment.data;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ro.lupii.assignment.R;

/**
 * Created by jon on 1/9/16.
 */
public class OSPSocket {
    static private final int port=4000;

    /* !!! Modify me !!! */
    static private final String host="localhost";

 //   private int port;
   // private String host;

    private Socket socket;

    //private BufferedOutputStream outputStream;
    private PrintWriter pw;
    private BufferedReader br;


    public OSPSocket() throws IOException {
        InputStreamReader iSR;
        BufferedOutputStream bOS;

        this.socket = new Socket(this.host, this.port);


        iSR = new InputStreamReader(this.socket.getInputStream());
        this.br = new BufferedReader(iSR);

        bOS = new BufferedOutputStream(this.socket.getOutputStream());
        this.pw = new PrintWriter(bOS, true);
    }

    public  String readAll() throws IOException {
        /* FIXME more efficient please */
        char c;
        String res="";
        while ((c=(char)this.br.read()) !='\0')
            res += c;

        return res;
    }

    public void writeString(String s) {
        this.pw.write(s+'\0');
        this.pw.flush();
    }

}
