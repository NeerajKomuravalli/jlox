package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  static boolean hadError = false;
  public static void main(String[] args) throws IOException {
      Expr expression = new Expr.Binary(
            new Expr.Unary(
                    new Token(TokenType.MINUS, "-", null, 1),
                    new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(
                    new Expr.Literal(45.67)));

      System.out.println(new AstPrinter().print(expression));

      if (args.length > 1) {
          System.out.println("Usage: jlox [script]");
          System.exit(64);
      } else if (args.length == 1) {
          runFile(args[0]);
      } else {
          runPrompt();
      }
  }
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    // Indicate an error in the exit code.
    if (hadError) System.exit(65);
  }
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    // Run forever
    for (;;) { 
      System.out.print("> ");
      run(reader.readLine());
      // Even if the error occurs we don't want the command line interface to not run so we will revert back the status
      hadError = false;
    }
  }
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // For now, just print the tokens.
    for (Token token : tokens) {
      System.out.println(token);
    }
  }
  static void error(int line, String message) {
    report(line, "", message);
  }
  private static void report(int line, String where, String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }
}