// QuizServer.java

import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 1234;
    private static final List<Question> questions = Arrays.asList(
            new Question("What is the capital of France?", "Paris"),
            new Question("What is 2 + 2?", "4"),
            new Question("What is the capital of South Korea?", "Seoul"));

    public static void main(String[] args) {
        System.out.println("Starting the Quiz Server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private int score = 0;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                out.println("Welcome to the Quiz Game!");

                for (Question question : questions) {
                    out.println("QUESTION: " + question.getQuestion());
                    String answer = in.readLine();

                    if (answer != null && answer.equalsIgnoreCase(question.getAnswer())) {
                        out.println("Correct!");
                        score++;
                    } else {
                        out.println("Incorrect! The correct answer was: " + question.getAnswer());
                    }
                }

                out.println("Quiz complete! Your final score is: " + score + "/" + questions.size());
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    private static class Question {
        private final String question;
        private final String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
