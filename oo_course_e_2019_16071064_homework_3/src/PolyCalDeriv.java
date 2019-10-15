import java.util.Scanner;

/**
 * @author gaoruiyuan
 */
public class PolyCalDeriv {

    public static void main(final String[] args) {
        Scanner scan = new Scanner(System.in);
        String polyString = "1+ \\v 2*x+2*x^+12+x^-12-2+x";
        if (scan.hasNextLine()) {
            polyString = scan.nextLine();
        } else {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
        FormChecker polyChecker = new FormChecker(polyString);
        polyString = polyChecker.getPoly();
        if (polyString == null) {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
        Poly poly = new Poly(polyString);
        //System.out.println(poly.toString());
        poly.calDeriv();
        //poly.simplify();
        System.out.println(poly.toString());

    }
}
