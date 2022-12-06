import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        String tsvFileName = "categories.tsv";
        File tsvFile = new File(tsvFileName);

        String buy;
        String maxCategory;

        FinanceManager financeManager = new FinanceManager(tsvFile);

        try (ServerSocket serverSocket = new ServerSocket(8989);) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    buy = in.readLine();
                    financeManager.addBuy(buy);
                    maxCategory = financeManager.maxCategory();
                    out.println(maxCategory);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }
}
