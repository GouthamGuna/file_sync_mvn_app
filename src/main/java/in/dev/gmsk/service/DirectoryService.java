package in.dev.gmsk.service;

import com.sun.net.httpserver.HttpExchange;
import in.dev.gmsk.model.DirectoryModel;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DirectoryService {

    CompletableFuture<List<DirectoryModel>> asCompletableFutureOfDirectoryEntries(File directory);

    void FileUploadHandler(HttpExchange exchange);
}
