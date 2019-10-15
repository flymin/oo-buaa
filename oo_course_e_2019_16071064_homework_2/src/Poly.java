import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author gaoruiyuan
 */
public class Poly {

    private HashMap<PolyItem, PolyItem> itemMap;

    Poly() {
        this.itemMap = new HashMap<>();
    }

    void addItem(final String item) {
        PolyItem polyItem = new PolyItem(item);
        findAdd(polyItem);
    }

    void findAdd(final PolyItem item) {
        if (this.itemMap.containsKey(item)) {
            PolyItem find = this.itemMap.get(item);
            find.combine(item);
        } else {
            this.itemMap.put(item, item);
        }
    }

    void calDeriv() {
        ArrayList<PolyItem> items;
        HashMap<PolyItem, PolyItem> copy = (HashMap)itemMap.clone();
        this.itemMap.clear();
        for (PolyItem originIt : copy.values()) {
            items = originIt.calDeriv();
            for (PolyItem newIt : items) {
                //已经判断了item系数不为零
                this.findAdd(newIt);
            }
        }
    }

    /**
     * 化简表达式
     */
    public void simplify() {
        PolyItem sin2Item;
        PolyItem cos2Item;
        Character reserve;
        do {
            sin2Item = null;
            cos2Item = null;
            for (PolyItem itemSin : this.itemMap.values()) {
                if (itemSin.hashString().startsWith("0")
                    && itemSin.hashString().charAt(1) == '2') {
                    reserve = itemSin.hashString().charAt(2);
                    for (PolyItem itemCos : this.itemMap.values()) {
                        if (itemCos.hashString().charAt(0) == '2'
                            && itemCos.hashString().charAt(1) == '0'
                            && reserve == itemCos.hashString().charAt(2)) {
                            cos2Item = itemCos;
                            sin2Item = itemSin;
                            break;
                        }
                    }
                    if (sin2Item != null && cos2Item != null) {
                        this.itemMap.remove(sin2Item);
                        this.itemMap.remove(cos2Item);
                        ArrayList<PolyItem> results =
                            sin2Item.sinCombine(cos2Item);
                        for (PolyItem item : results) {
                            this.findAdd(item);
                        }
                        break;
                    }
                }
            }
        } while (sin2Item != null && cos2Item != null);
    }

    @Override public String toString() {
        String string = "";
        String itemString;
        LinkedList<String> itemStrs = new LinkedList<>();
        boolean first = true;
        if (this.itemMap.isEmpty()) {
            return "0";
        }
        for (PolyItem item : this.itemMap.values()) {
            itemString = item.toString();
            if (itemString.startsWith("-")) {
                itemStrs.addLast(itemString);
            } else {
                itemStrs.addFirst("+" + itemString);
            }
        }
        for (String item : itemStrs) {
            string += item;
        }
        if (string.startsWith("+")) {
            string = string.substring(1);
        }
        return string;
    }
}
