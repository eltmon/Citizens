package com.fullwall.Citizens.Listeners;
import java.io.*;
import java.util.concurrent.Callable;

public class ConsoleInputReadTask implements Callable<String> {
  public String call() throws IOException {
   // BufferedReader br = new BufferedReader(
   //     new InputStreamReader(System.in));
    System.out.println("ConsoleInputReadTask run() called.");
    String input;
    do {
      System.out.println("Please type something: ");
      try {
        // wait until we have data to complete a readLine()
        while (!PlayerListen.brCleanUp.ready()) {
          Thread.sleep(200);
        }
        input = PlayerListen.brCleanUp.readLine();
      } catch (InterruptedException e) {
        System.out.println("ConsoleInputReadTask() cancelled");
        return null;
      }
    } while ("".equals(input));
    System.out.println("INPUT: " + input);
    //System.out.println("Thank You for providing input!");
    return input;
  }
}