package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class Manager {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        User user = User.Manager;
        new Message(NetCom.Connect, user).send(out);
        Queue<Message> storehouse = new ArrayDeque<>();
        boolean flag = true;
        while (flag) {
            Message msg = Message.take(user, inp, out);
            switch (msg.fromUser) {
                case Developer:
                    cout.printf("менеджер отправляет задание на сервер: %s\n", msg.info);
                    Message.send(user, User.Server, State.Developed, msg.info, out);
                    break;
                case Server:
                    cout.printf("менеджер кладёт готовый заказ на склад: %s\n", msg.info);
                    storehouse.offer(new Message(NetCom.Put, user, User.Client, State.Success, msg.info));
                    break;
                case Client:
                    switch (msg.state) {
                        case ReqGetResult:
                            if (!storehouse.isEmpty()) {
                                cout.printf("на складе лежат готовые заказы: %d\n", storehouse.size());
                                Message message = storehouse.poll();
                                message.send(out);
                                cout.printf("менеджер отдал готовый заказ: %s\n", message.info);
                            }
                            break;
                    }
                    break;
            }
            Thread.sleep(100);
        }
        cout.println("менеджер ушел");
        socket.close();
    }
}
