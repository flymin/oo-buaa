import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author gaoruiyuan
 */
public class Poly {

    private Map<BigInteger, PolyItem> itemMap;

    Poly() {
        this.itemMap = new HashMap<>();
    }

    void addItem(final String item) {
        PolyItem polyItem = new PolyItem(item);
        PolyItem find = this.itemMap.get(polyItem.getExp());
        if (find != null) {
            find.addCoe(polyItem.getCoe());
        } else {
            this.itemMap.put(polyItem.getExp(), polyItem);
        }
    }

    void calDeriv() {
        Iterator it = this.itemMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            PolyItem item = (PolyItem) entry.getValue();
            item.calDeriv();
        }
    }

    @SuppressWarnings("AlibabaStringConcat")
    @Override
    public String toString() {
        StringBuilder polyString = new StringBuilder();
        Iterator it = this.itemMap.entrySet().iterator();
        boolean first = true;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            PolyItem item = (PolyItem) entry.getValue();
            if (item.getCoe().equals(BigInteger.ZERO)) {
                continue;
            }
            if (first) {
                first = false;
                polyString.append(item.toString());
            } else {
                if (item.getCoe().compareTo(BigInteger.ZERO) > 0) {
                    // noinspection AlibabaStringConcat
                    polyString.append("+").append(item.toString());
                } else if (item.getCoe().compareTo(BigInteger.ZERO) < 0) {
                    polyString.append(item.toString());
                }
            }

        }
        if (polyString.length() == 0) {
            polyString.append("0");
        }
        return polyString.toString();
    }
}
