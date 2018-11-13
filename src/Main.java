import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Arrays.asList("(250+50)*(5-4)", "(50*2)-(25+5)/3", "3+4*5/6", "3/2+0.5*1.4", "(((24/0.40)/15)+((25/0.40)/15)+(0.95*15))/45")
                .stream().forEachOrdered(s -> {
                    String pf = Equation.toPostfix(s);
                    System.out.println(String.format("%s ~ %s == %s", s, pf, Equation.evaluate(pf)));
                });
    }
}
