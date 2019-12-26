package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerRoutine implements Runnable {

    Socket socket;
    DataInputStream inStream;
    DataOutputStream outStream;

    public ServerRoutine(Socket socket) throws IOException {
        this.socket = socket;
        inStream = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            User myUser;
            boolean flag = true;
            while (flag) {
                Message msg = new Message(inStream);
                switch (msg.com) {
                    case Connect:
                        LabServer.postMap.put(msg.fromUser, new ArrayBlockingQueue<>(100));
                        myUser = msg.fromUser;
                        break;
                    case Put:
                        try {
                            if(!LabServer.postMap.containsKey(msg.toUser)){
                                LabServer.postMap.put(msg.toUser, new ArrayBlockingQueue<>(100));
                            }
                            LabServer.postMap.get(msg.toUser).put(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Get:
                        try {
                            Message message = LabServer.postMap.get(msg.fromUser).take();
                            message.send(outStream);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Disconnect:
                        LabServer.postMap.remove(msg.fromUser);
                        flag = false;
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
