package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Developer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        User user = User.Developer;
        new Message(NetCom.Connect, user).send(out);
        boolean flag = true;
        while (flag) {
            Message msg = Message.take(user, inp, out);
            String order = msg.info;
            cout.printf("инженер получил заказ: %s\n", order);
            if (order.length() > 40) {
                cout.printf("заказ отклонен, слишком большая деталь: %s\n", order);
                Message.send(user, User.Client, State.Reject, order, out);
            }
            else {
                cout.printf("заказ принят: %s\n", order);
                Message.send(user, User.Client, State.Accept, order, out);
                Message.send(user, User.Manager, State.Developed, order, out);
                cout.println("заказ передан менеджеру");
            }
            Thread.sleep(200);
        }
        cout.println("инженер ушел");
        socket.close();
    }
}
