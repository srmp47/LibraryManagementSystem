package library;

import library.controllers.CommandLineController;
import library.database.DatabaseConnection;
import library.models.Library;
import library.models.LibraryRequest;
import library.models.enums.ThreadType;
import library.threads.factories.ThreadFactory;

import java.util.concurrent.*;

public class Main {
    private static final BlockingQueue<LibraryRequest> requestQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<library.models.LibraryResult> resultQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("=== Library Management System (JDBC Version) ===");

        try {
            DatabaseConnection.getConnection();
            System.out.println("✅ Database connection established");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            System.err.println("Please check your database configuration in db.properties");
            System.exit(1);
        }

        Library library = Library.getInstance();
        CommandLineController cli = CommandLineController.getInstance(library);
        CountDownLatch countDownLatch = new CountDownLatch(3);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            Thread requestThread = ThreadFactory.createThread(ThreadType.REQUEST_HANDLER, cli, library, countDownLatch, requestQueue, resultQueue);
            Thread resultThread1 = ThreadFactory.createThread(ThreadType.RESULT_HANDLER, cli, library, countDownLatch, requestQueue, resultQueue);
            Thread resultThread2 = ThreadFactory.createThread(ThreadType.RESULT_HANDLER, cli, library, countDownLatch, requestQueue, resultQueue);
            executorService.execute(requestThread);
            executorService.execute(resultThread1);
            executorService.execute(resultThread2);
            countDownLatch.await();

        } catch (InterruptedException e) {
            System.err.println("❌ Main thread interrupted");
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdownNow();
            ThreadFactory.stopAllThreads();
            DatabaseConnection.shutdown();
        }

        cli.exitProgram();
    }

    public static BlockingQueue<LibraryRequest> getRequestQueue() {
        return requestQueue;
    }

    public static BlockingQueue<library.models.LibraryResult> getResultQueue() {
        return resultQueue;
    }
}