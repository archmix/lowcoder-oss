package lowcoder.common.interfaces;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import lowcoder.promise.interfaces.PromiseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
    private final ClassLoader classLoader;

    private final FileSystem fileSystem;

    ResourceLoader() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.fileSystem = Vertx.currentContext().owner().fileSystem();
    }

    public static ResourceLoader create() {
        return new ResourceLoader();
    }

    public void openBuffer(String path, PromiseHandler<Buffer> handler) {
        this.openResourceBuffer(path, resourceHandler -> {
            if (resourceHandler.succeeded()) {
                handler.handle(resourceHandler);
                return;
            }

            this.openFileBuffer(path, fileHandler -> {
                handler.handle(fileHandler);
            });
        });
    }

    public void openFileBuffer(String path, PromiseHandler<Buffer> handler) {
        File file = new File(path);
        if (file.exists()) {
            fileSystem.readFile(path, handler);
            return;
        }

        handler.handle(Future.failedFuture(new FileNotFoundException(path)));
    }

    public void openResourceBuffer(String path, PromiseHandler<Buffer> handler) {
        try (InputStream inputStream = this.openResourceStream(path)) {
            if (inputStream == null) {
                handler.handle(Future.failedFuture(new FileNotFoundException(path)));
                return;
            }

            Buffer buffer = Buffer.buffer();
            byte[] available = new byte[inputStream.available()];
            inputStream.read(available);
            buffer.appendBytes(available);
            handler.handle(Future.succeededFuture(buffer));
        } catch (IOException e) {
            handler.handle(Future.failedFuture(e));
        }
    }

    public InputStream openResourceStream(String path) {
        return this.classLoader.getResourceAsStream(path);
    }
}
