package UC1;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;

public class SimpleHttpServer {
    private HttpServer server;

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped");
        }
    }


    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello from the server!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Add more methods or functionalities as needed
    // For example, to add a new context:
    public void addContext(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            // Use the dynamically allocated port
            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return -1;
        }
    }

}

