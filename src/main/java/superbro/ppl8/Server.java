package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        User user = User.Server;
        new Message(NetCom.Connect, user).send(out);
        boolean flag = true;
        while (flag) {
            Message msg = Message.take(user, inp, out);
            switch (msg.fromUser) {
                case Manager:
                    switch (msg.state) {
                        case Developed: {
                            cout.printf("сервер принимает задание: %s\n", msg.info);
                            Message.send(user, User.Machine, State.Developed, msg.info, out);
                        }break;
                        case MachineOff: {
                            cout.println("сервер отключает станок");
                            Message.send(user, User.Machine, State.MachineOff, out);
                            flag = false;
                        }break;
                    }
                    break;
                case Machine:
                    cout.printf("сервер оповещает менеджера о готовности: %s\n", msg.info);
                    Message.send(user, User.Manager, State.Success, msg.info, out);
                    break;
            }
            Thread.sleep(10);
        }
        cout.println("сервер остановлен");
        socket.close();
    }
}
