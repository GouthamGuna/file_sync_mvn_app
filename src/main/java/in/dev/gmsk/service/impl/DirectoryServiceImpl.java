package in.dev.gmsk.service.impl;

import com.sun.net.httpserver.HttpExchange;
import in.dev.gmsk.model.DirectoryModel;
import in.dev.gmsk.service.DirectoryService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryServiceImpl implements DirectoryService {

    @Override
    public CompletableFuture<List<DirectoryModel>> asCompletableFutureOfDirectoryEntries(File directory) throws RuntimeException {
        try (Stream<Path> entries = Files.walk(directory.toPath())) {
            return CompletableFuture.completedFuture(entries.filter(Files::isRegularFile)
                    .map(entry -> new DirectoryModel(removeFileExtension(entry.getFileName().toString()), entry.toAbsolutePath().toString(), generateRandomUrl(entry.getFileName().toString(), entry.toAbsolutePath().toString()).get()))
                    .filter(DirectoryServiceImpl::isSafeToProcess) // Additional security check here
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to list directory entries: " + directory, e);
        }
    }

    private static String removeFileExtension(String FileName) {
        return FileName.split("\\.")[0];
    }

    private static Supplier<String> generateRandomUrl(String fileName, String filePath) {
        return () -> "http://127.0.0.1:2427/files?fileName=" + Base64.getUrlEncoder()
                .encodeToString(fileName.getBytes(StandardCharsets.UTF_8)) + "&data=" + Base64.getUrlEncoder()
                .encodeToString(filePath.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean isSafeToProcess(DirectoryModel entry) {
        String fileName = entry.getName();
        return !fileName.contains(".exe") && !fileName.endsWith(".sh") && !fileName.endsWith(".class");
    }

    @Override
    public void FileUploadHandler(HttpExchange exchange) {
        if ("POST".equals(exchange.getRequestMethod())) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Parse the request
                    InputStream inputStream = exchange.getRequestBody();
                    // You'll need to write a method to parse the multipart request and extract the file
                    File file = parseMultipartRequest(inputStream);

                    // Process the file as needed

                    // Send a response
                    String response = "File uploaded successfully...";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            try {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File parseMultipartRequest(InputStream inputStream) {
        // Implement parsing logic here
        System.out.println("inputStream = " + inputStream);
        return null;
    }
}
