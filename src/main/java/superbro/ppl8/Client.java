package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

public class Client {

    private static final int ORDER_COUNT = 10;
    private static Random rand = new Random(0);

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean flag = true;
        int orderN = 0;
        int orderF = 0;
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        User user = User.Client;
        Message.connect(user, out);
        while (flag) {
            if (orderN < ORDER_COUNT) {
                String order = generateOrder();
                Message.send(user, User.Developer, State.New, order, out);
                orderN++;
                cout.printf("клиент сделал заказ: %s\n", order);
            }
            if (orderF == ORDER_COUNT) {
                cout.println("все заказы выполнены");
                flag = false;
                continue;
            }
            Message msg = Message.take(user, inp, out);
            switch (msg.fromUser) {
                case Developer:
                    switch (msg.state) {
                        case Accept:
                            cout.printf("пришло подтверждение заказа: %s\n", msg.info);
                            break;
                        case Reject:
                            cout.printf("пришел отказ заказа: %s\n", msg.info);
                            orderF++;
                            break;
                    }
                    break;
                case Manager:
                    if (msg.state == State.Success) {
                        cout.printf("клиент получил готовый заказ: %s\n", msg.info);
                        orderF++;
                    }
                    break;
            }
            if (orderF < ORDER_COUNT) {
                cout.printf("клиент спрашивает про готовые заказы\n");
                Message.send(user, User.Manager, State.ReqGetResult, out);
            }
            Thread.sleep(500);
        }
        socket.close();
    }

    private static String generateOrder() {
        char c = (char) ('a' + (rand.nextInt(26)));
        int len = rand.nextInt(60) + 1;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < len; j++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
