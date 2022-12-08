import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String tsvFileName = "categories.tsv";
        String binFileName = "data.bin";
        File tsvFile = new File(tsvFileName);
        File binFile = new File(binFileName);

        FinanceManager financeManager;

        if (binFile.exists()) {
            financeManager = FinanceManager.loadFromBin(binFile);
        } else {
            financeManager = new FinanceManager(tsvFile);
        }

        try (ServerSocket serverSocket = new ServerSocket(8989);) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    String buy = in.readLine();
                    financeManager.addBuy(buy);
                    String maxCategory = financeManager.maxCategory();
                    out.println(maxCategory);
                    financeManager.saveBin(binFile);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }
}
