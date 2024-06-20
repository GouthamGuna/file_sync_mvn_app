package in.dev.gmsk;

import com.sun.net.httpserver.*;
// import in.dev.gmsk.controller.DirectoryController.HomeHandler;
import in.dev.gmsk.controller.DirectoryController;
//import in.dev.gmsk.service.impl.AuthenticatorImpl;
//import in.dev.gmsk.service.impl.DirectoryServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FileSyncApplication {

    public static void main(String[] args) throws IOException {
        int port = 2427;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new DirectoryController.HomeHandler());
        server.createContext("/fetchUserPath", new DirectoryController.fetchFiles());
        server.createContext("/files", new DirectoryController.fileDownloader());
        server.createContext("/upload", new DirectoryController.handleUploadedFiles());
        //nHttpContext context =  //ew AuthenticatorImpl().authenticateUserRequest(context);

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port http://127.0.0.1:" + port);
    }
}
