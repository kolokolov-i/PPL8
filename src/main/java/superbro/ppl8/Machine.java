package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Machine {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        User user = User.Machine;
        new Message(NetCom.Connect, user).send(out);
        boolean flag = true;
        while (flag) {
            Message msg = Message.take(user, inp, out);
            if (msg.fromUser == User.Server) {
                switch (msg.state) {
                    case Developed: {
                        String res = msg.info.toUpperCase();
                        Thread.sleep(10);
                        Message.send(user, User.Server, State.Success, res, out);
                        cout.printf("деталь изготовлена: %s\n", res);
                    }break;
                    case MachineOff: {
                        cout.println("принят сигнал о выключении");
                        flag = false;
                    }break;
                }
            }
            Thread.sleep(100);
        }
        cout.println("станок остановлен");
        socket.close();
    }
}
