package library.threads.factories;

import library.Main;
import library.controllers.CommandLineController;
import library.models.Library;
import library.models.LibraryRequest;
import library.models.LibraryResult;
import library.models.enums.ThreadType;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static library.models.enums.RequestType.EXIT;

public class ThreadFactory {

    private static volatile boolean running = true;


    public static Thread createRequestHandlerThread(
            CommandLineController cli,
            CountDownLatch countDownLatch,
            BlockingQueue<LibraryResult> resultQueue) {

        Runnable requestHandlerTask = () -> {

            while (running) {
                try {
                    processResults(resultQueue);
                    cli.printMenu();
                    String choice = cli.getUserInput();
                    LibraryRequest request = cli.handleChoice(choice);

                    if (request != null) {
                        if (request.getRequestType() == EXIT) {
                            stopAllThreads();
                            break;
                        }
                        Main.getRequestQueue().add(request);
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

            countDownLatch.countDown();
        };

        return new Thread(requestHandlerTask);
    }


    public static Thread createResultHandlerThread(
            CommandLineController cli,
            Library library,
            CountDownLatch countDownLatch,
            BlockingQueue<LibraryRequest> requestQueue) {

        Runnable resultHandlerTask = () -> {

            while (running || !requestQueue.isEmpty()) {
                try {
                    LibraryRequest request = requestQueue.poll();
                    if (request != null) {
                        LibraryResult result = cli.processRequest(request, library);
                        Main.getResultQueue().add(result);
                    }

                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Error in handler thread: " + e.getMessage());
                    LibraryResult errorResult = new LibraryResult(false, "Error: " + e.getMessage());
                    Main.getResultQueue().add(errorResult);
                }
            }

            countDownLatch.countDown();
        };

        return new Thread(resultHandlerTask);
    }


    public static Thread createThread(
            ThreadType type,
            CommandLineController cli,
            Library library,
            CountDownLatch countDownLatch,
            BlockingQueue<LibraryRequest> requestQueue,
            BlockingQueue<LibraryResult> resultQueue) {

        return switch (type) {
            case REQUEST_HANDLER -> createRequestHandlerThread(cli, countDownLatch, resultQueue);
            case RESULT_HANDLER -> createResultHandlerThread(cli, library, countDownLatch, requestQueue);
        };
    }


    private static void processResults(BlockingQueue<LibraryResult> resultQueue) {
        List<LibraryResult> results = new java.util.ArrayList<>();
        resultQueue.drainTo(results);
        for (LibraryResult result : results) {
            System.out.println(result);
        }
    }

    public static void stopAllThreads() {
        running = false;
    }

}