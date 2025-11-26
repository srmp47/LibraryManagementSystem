package library;

import library.controllers.CommandLineController;
import library.models.*;
import library.models.enums.RequestType;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static volatile boolean running = true;
    private static final BlockingQueue<LibraryRequest> requestQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<LibraryResult> resultQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        Library library = new Library();
        CommandLineController cli = new CommandLineController(library);

        System.out.println("=== Library Management System ===");
        Thread requestThread = createRequestHandlerThread(cli);
        Thread handlerThread = createResultHandlerThread(cli, library);

        requestThread.start();
        handlerThread.start();
        try {
            requestThread.join();
            handlerThread.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted");
        }

        cli.exitProgram();
    }

    private static Thread createRequestHandlerThread(CommandLineController cli) {
        return new Thread(() -> {
            while (running) {
                try {
                    List<LibraryResult> results = new ArrayList<>();
                    resultQueue.drainTo(results);
                    for (LibraryResult result : results) {
                        System.out.println(result);
                    }
                    cli.printMenu();
                    String choice = cli.getUserInput();
                    LibraryRequest request = cli.handleChoice(choice);

                    if (request != null) {
                        if (request.getRequestType() == RequestType.EXIT) {
                            running = false;
                            break;
                        }
                        requestQueue.add(request);
                        System.out.println("✅ Request submitted to queue!");
                    }

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Error in request thread: " + e.getMessage());
                }
            }
        });
    }

    private static Thread createResultHandlerThread(CommandLineController cli, Library library) {
        return new Thread(() -> {
            while (running || !requestQueue.isEmpty()) {
                try {
                    LibraryRequest request = null;
                    if (!requestQueue.isEmpty()) {
                        request = requestQueue.poll();
                    }

                    if (request != null) {
                        LibraryResult result = cli.processRequest(request, library);
                        resultQueue.add(result);
                    }

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Error in handler thread: " + e.getMessage());
                    LibraryResult errorResult = new LibraryResult(false, "Error: " + e.getMessage());
                    resultQueue.add(errorResult);
                }
            }
        });
    }
}