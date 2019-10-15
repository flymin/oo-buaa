import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author gaoruiyuan
 */
public class Poly extends Util {
    private HashMap<PolyItem, PolyItem> itemMap;

    Poly(String polyString) {
        super();
        if (polyString.length() == 0) {
            Factor.exit("LENGTH=0");
        }
        this.setInput(polyString);
        this.itemMap = new HashMap<>();
        this.setIndex(0);
        while (!this.hitEnd()) {
            String item = this.nextItem(true);
            if (item == null) {
                System.out.println("WRONG FORMAT!");
                System.exit(0);
            }
            this.addItem(item);
        }
    }

    public int getItemNum() {
        return this.itemMap.values().toArray().length;
    }

    public Factor onlyFactor() {
        if (this.getItemNum() == 0) {
            return new Const("0");
        } else if (this.getItemNum() != 1) {
            return null;
        } else {
            PolyItem item = this.itemMap.values().iterator().next();
            return item.onlyFactor();
        }
    }

    public HashMap<PolyItem, PolyItem> getItemMap() {
        return itemMap;
    }

    @Override
    public String hashString() {
        String result = "";
        LinkedList<String> strs = new LinkedList<>();
        for (PolyItem item : this.itemMap.values()) {
            result = item.getConFac().toString() +
                        item.hashString();
            this.sortedInsert(result, strs);
        }
        result = "";
        for (String str : strs) {
            result += str;
            result += ".";
        }
        return result;
    }

    void addItem(final String item) {
        PolyItem polyItem = new PolyItem(item);
        if (polyItem.getConFac().equals(BigInteger.ZERO)) {
            polyItem = PolyItem.Zero();
        }
        findAdd(polyItem);
    }

    void findAdd(final PolyItem item) {
        if (this.itemMap.containsKey(item)) {
            PolyItem find = this.itemMap.get(item);
            this.itemMap.remove(find);
            find.combine(item);
            if (find.getConFac().equals(BigInteger.ZERO)) {
                find = PolyItem.Zero();
            }
            this.itemMap.put(find, find);
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

    @Override protected Object clone() {
        Poly result = new Poly(this.getInput());
        return result;
    }

    /**
     * 化简表达式
     */
    /*
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
    */

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
