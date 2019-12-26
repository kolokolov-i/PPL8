package superbro.ppl8;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LabServer {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public static HashMap<User, BlockingQueue<Message>> postMap;

    public static void main(String[] args) {
        postMap = new HashMap<>();
        try (ServerSocket serverSocket = new ServerSocket(3232)) {
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    ServerRoutine routine = new ServerRoutine(socket);
                    threadPool.execute(routine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
