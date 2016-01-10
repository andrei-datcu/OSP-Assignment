package ro.lupii.assignment.services;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;

import ro.lupii.assignment.data.OSPSocket;

/**
 * Created by andrei on 1/10/16.
 */
public class SocketThread extends Thread {

    public interface OnMessageArrivedListener {
        void onMessageArrived(String message);
    }

    private class MessageWaitLock {
        public boolean somebodyWaiting = false;
        public String theActualMessage;
    }

    private OSPSocket socket;
    final private ConcurrentLinkedQueue<String> inputQueue = new ConcurrentLinkedQueue<>();
    final private MessageWaitLock mMessageWaitLock = new MessageWaitLock();
    private OnMessageArrivedListener mOnMessageArrivedListener;
    private Exception raisedException = null;

    public SocketThread(OnMessageArrivedListener mOnMessageArrivedListener) {
        this.mOnMessageArrivedListener = mOnMessageArrivedListener;
    }

    private void createSocket() throws IOException{
        //TODO(John) Create socket here
    }

    @Override
    public void run() {
        try {
            createSocket();
        } catch (IOException e) {
            raisedException = e;
            return;
        }
        try {
            for (; ; ) {
                try {
                    final String message = socket.readAll();
                    synchronized (mMessageWaitLock) {
                        if (mMessageWaitLock.somebodyWaiting) {
                            mMessageWaitLock.theActualMessage = message;
                            mMessageWaitLock.somebodyWaiting = false;
                            mMessageWaitLock.notify();
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mOnMessageArrivedListener.onMessageArrived(message);
                                }
                            });
                        }
                    }
                } catch (SocketTimeoutException e) {
                    if (isInterrupted())
                        return;
                    String sendMsg = inputQueue.poll();
                    if (sendMsg != null)
                        socket.writeString(sendMsg);
                } catch (IOException e) {
                    raisedException = e;
                    return;
                }
            }
        } finally {
            //TODO (John) close socket here;
        }
    }

    /**
     * Block and wait for a message to arrive
     * WARNING!!! This HAS to be called outside this thread's context
     * @return Received message
     * @throws Exception
     */
    public String readMessage() throws Exception {
        if (raisedException != null)
            throw raisedException;
        synchronized (mMessageWaitLock) {
            mMessageWaitLock.somebodyWaiting = true;
            mMessageWaitLock.wait();
            return mMessageWaitLock.theActualMessage;
        }
    }

    /**
     * Send a new message
     * WARNING!!! This MUST be called outside this thread's context
     * @param message The message to be sent
     */
    public void sendMessage(String message) {
        inputQueue.add(message);
    }
}
