package compiler;


import compiler.lexer.Lexer;
import compiler.lexer.LexerException;
import compiler.node.Start;
import compiler.parser.Parser;
import compiler.parser.ParserException;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
	public static void main(String[] args) throws FileNotFoundException {
        Start tree = null;
        FileReader input= new FileReader(args[0]);


		PushbackReader reader = new PushbackReader(input);


		try {
			Parser p = new Parser(new Lexer(reader));
			tree = p.parse();
			//System.out.println(tree.getPProgram());
		} catch (LexerException e) {
			System.err.printf("Lexing error: %s\n", e.getMessage());
		} catch (IOException e) {
			System.err.printf("I/O error: %s\n", e.getMessage());
			e.printStackTrace();
		} catch (ParserException e) {
			System.err.printf("Parsing error: %s\n", e.getMessage());
		}
		tree.apply(new GenericsVisitor());
	}
}