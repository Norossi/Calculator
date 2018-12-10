import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
	    String s = "5^2-((4/2+9/3)/5)";
        System.out.println(Calculator.calculate(s));
    }
}
