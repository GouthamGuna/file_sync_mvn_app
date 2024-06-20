package in.dev.gmsk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import in.dev.gmsk.model.DirectoryModel;
import in.dev.gmsk.response.InvalidFilePath;
import in.dev.gmsk.service.DirectoryService;
import in.dev.gmsk.service.impl.DirectoryServiceImpl;
import in.dev.gmsk.util.HTTP_Status_Codes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class DirectoryController {

    private static final DirectoryService DIRECTORY_SERVICE = new DirectoryServiceImpl();

    public static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Welcome to File Sync Application...";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class fetchFiles implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            String fetchURL = t.getRequestURI().toString();

            if (fetchURL.length() <= 14) {
                String response = new ObjectMapper().writeValueAsString(new InvalidFilePath("Resource not found", "/fetchUserPath?root=<Args - Missing>", "Bad Request"));
                t.sendResponseHeaders(HTTP_Status_Codes.httpStatus("Bad Request"), response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            CompletableFuture<List<DirectoryModel>> completableFutureOfDirectoryEntries =
                    DIRECTORY_SERVICE.asCompletableFutureOfDirectoryEntries(new File(fetchURL.split("=")[1]));

            List<DirectoryModel> directoryModels;
            try {
                directoryModels = completableFutureOfDirectoryEntries.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            String response = new ObjectMapper().writeValueAsString(directoryModels);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class fileDownloader implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                String URL = exchange.getRequestURI().toString();

                String s = URL.split("\\?")[1];

                /*System.out.println("s = " + s);

                String fileName = s.split("&")[0];
                String filePath = s.split("&")[1];

                System.out.println("fileName = " + fileName);
                System.out.println("filePath = " + filePath);

                System.out.println("split fileName = " + s.split("&")[0].substring(9));
                System.out.println("split filePath = " + s.split("&")[1].substring(5));*/

                String fileName = s.split("&")[0].substring(9);
                String filePath = s.split("&")[1].substring(5);

                //System.out.printf("ss = %s : fileName = %s : URL = %s%n", s.split("&")[0], s.split("&")[0].substring(9), Arrays.toString(Base64.getUrlDecoder().decode(fileName)));
                //System.out.println("filePath = " + filePath);
                // encodeToString(originalURI.getBytes());
                byte[] decodedBytesFileName = Base64.getUrlDecoder().decode(fileName);
                byte[] decodedBytesFilePath = Base64.getUrlDecoder().decode(filePath);

                //System.out.println("decodedBytesFileName = " + Arrays.toString(decodedBytesFileName));
                //System.out.println("decodedBytesFilePath = " + Arrays.toString(decodedBytesFilePath));

                String decodedUrlFN = new String(decodedBytesFileName);
                String decodedUrlFP = new String(decodedBytesFilePath);

                //System.out.println("decodedUrlFN = " + decodedUrlFN);
                //System.out.println("decodedUrlFP = " + decodedUrlFP);

                File file = new File(decodedUrlFP);
                //byte[] fileContent = readFileToByteArray(file);

                // Serve the byte array as a downloadable file
                //serveFileAsDownload(fileContent, decodedUrlFN, exchange);

                CompletableFuture<Void> cf = CompletableFuture.runAsync(
                        () -> fileDownloadHandler(exchange, file, decodedUrlFN));

                cf.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

   /* private static byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            return buffer;
        }
    }

    private static void serveFileAsDownload(byte[] content, String fileName, HttpExchange response)
            throws IOException {

        response.sendResponseHeaders(200, content.length);
        OutputStream os = response.getResponseBody();
        os.write(content);
        os.close();
    }*/

    private static void fileDownloadHandler(HttpExchange exchange, File file, String filename) {
        try {
            if (file.exists()) {
                exchange.getResponseHeaders().add("Content-Type", "application/octet-stream");
                exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                exchange.sendResponseHeaders(HTTP_Status_Codes.httpStatus("OK"), file.length());

                try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = fis.read(buffer)) > 0) {
                        os.write(buffer, 0, count);
                    }
                }
            } else {
                String response = "File not found.";
                exchange.sendResponseHeaders(HTTP_Status_Codes.httpStatus("Not Found"), response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        exchange.close();
    }

    public static class handleUploadedFiles implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            DIRECTORY_SERVICE.FileUploadHandler(t);
        }
    }
}
