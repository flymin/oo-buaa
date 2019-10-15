import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author gaoruiyuan
 */
public class PolyItem {
    private boolean hasSin2;
    private boolean hasCos2;
    private static final Pattern CON_FAC =
        Pattern.compile("[+-]?\\d+([+\\-*]|$)");
    private static final String ADD = "+";
    private static final String SUB = "-";
    private HashMap<FacType, PolyFactor> typeFactor;

    PolyItem() {
        typeFactor = new HashMap<>();
    }

    PolyItem(final String itemString) {
        // Input: string of item, not perfectly checked
        typeFactor = new HashMap<>();
        String[] strings = itemString.split("\\*");
        PolyFactor factor;
        boolean first = true;
        for (String factorString : strings) {
            if (CON_FAC.matcher(factorString).lookingAt()) {
                factor = new PolyFactor(new BigInteger(factorString));
                first = false;
            } else {
                if (first) {
                    if (factorString.startsWith(ADD) || factorString
                        .startsWith(SUB)) {
                        if (factorString.startsWith(SUB)) {
                            this.typeFactor.put(FacType.CONST,
                                new PolyFactor(new BigInteger("-1")));
                        }
                        factorString = factorString.substring(1);
                    }
                    first = false;
                }
                factor = new PolyFactor(factorString);
            }
            addFactor(factor);
        }
    }

    /**
     * 同类项合并：const值相加生成新的factor，组合进item中
     * @param sim
     */
    public void combine(PolyItem sim) {
        BigInteger valueA;
        BigInteger valueB;
        if (sim.typeFactor.containsKey(FacType.CONST)) {
            valueA = sim.typeFactor.get(FacType.CONST).getValue();
        } else {
            valueA = BigInteger.ONE;
        }
        if (this.typeFactor.containsKey(FacType.CONST)) {
            valueB = this.typeFactor.get(FacType.CONST).getValue();
        } else {
            valueB = BigInteger.ONE;
        }
        this.typeFactor.remove(FacType.CONST);
        this.addFactor(new PolyFactor(valueA.add(valueB)));
    }

    private PolyItem genSinNgCos() {
        PolyFactor sinFactor =
            this.typeFactor.get(FacType.TRIG_COS).genSinAsCos();
        PolyItem result = new PolyItem();
        result.typeFactor.put(FacType.TRIG_SIN, sinFactor);
        result.typeFactor.put(FacType.CONST,
            new PolyFactor(this.getConFac().negate()));
        return result;
    }

    public ArrayList<PolyItem> sinCombine(PolyItem cosItem) {
        ArrayList<PolyItem> results = new ArrayList<>();

        // res一定不是空项，至少是1
        PolyItem res = (PolyItem) cosItem.clone();
        res.typeFactor.remove(FacType.TRIG_COS);
        if (res.typeFactor.isEmpty()) {
            res.typeFactor.put(FacType.CONST, new PolyFactor(BigInteger.ONE));
        }
        results.add(res);

        this.combine(cosItem.genSinNgCos());
        if (!this.getConFac().equals(BigInteger.ZERO)) {
            results.add(this);
        }
        return results;
    }

    /**
     * 向item中添加项，相同的指数相加，系数相乘
     * @param factor
     */
    public void addFactor(final PolyFactor factor) {
        PolyFactor simFactor;
        if (this.typeFactor.containsKey(factor.getType())) {
            simFactor = this.typeFactor.get(factor.getType());
            this.typeFactor.remove(factor.getType());
            simFactor = simFactor.combine(factor);
        } else {
            simFactor = factor;
        }
        if (simFactor != null) {
            this.typeFactor.put(simFactor.getType(), simFactor);
        }
    }

    public ArrayList<PolyItem> calDeriv() {
        ArrayList<PolyItem> result = new ArrayList<PolyItem>();
        PolyItem copy;
        ArrayList<PolyFactor> factors;
        for (PolyFactor factor : this.typeFactor.values()) {
            copy = (PolyItem)this.clone();
            factors = factor.calDeriv();
            copy.typeFactor.remove(factor.getType());
            for (PolyFactor resultFactor : factors) {
                copy.addFactor(resultFactor);
            }
            if (!copy.typeFactor.containsKey(FacType.CONST) || !copy.typeFactor
                .get(FacType.CONST).getValue().equals(BigInteger.ZERO)) {
                result.add(copy);
            }
        }
        return result;
    }

    public BigInteger getConFac() {
        if (this.typeFactor.containsKey(FacType.CONST)) {
            return this.typeFactor.get(FacType.CONST).getValue();
        } else {
            return BigInteger.ONE;
        }
    }

    @Override
    public boolean equals(Object obj) {
        PolyItem item = (PolyItem)obj;
        return this.hashString().equals(item.hashString());
    }

    public String hashString() {
        String string = "";
        PolyFactor factor;
        if (this.typeFactor.containsKey(FacType.TRIG_COS)) {
            factor = this.typeFactor.get(FacType.TRIG_COS);
            string += factor.getValue().toString();
        } else {
            string += "0";
        }
        if (this.typeFactor.containsKey(FacType.TRIG_SIN)) {
            factor = this.typeFactor.get(FacType.TRIG_SIN);
            string += factor.getValue().toString();
        } else {
            string += "0";
        }
        if (this.typeFactor.containsKey(FacType.X_TERM)) {
            factor = this.typeFactor.get(FacType.X_TERM);
            string += factor.getValue().toString();
        } else {
            string += "0";
        }
        return string;
    }

    @Override
    public int hashCode() {
        return this.hashString().hashCode();
    }

    @Override
    public Object clone() {
        PolyItem item = new PolyItem();
        item.typeFactor =
            (HashMap<FacType, PolyFactor>)this.typeFactor.clone();
        return item;
    }

    @Override
    public String toString() {
        String result = "";
        boolean first = true;
        if (this.typeFactor.containsKey(FacType.CONST)) {
            if (!this.typeFactor.get(FacType.CONST).
                getValue().equals(BigInteger.ONE)) {
                if (!this.typeFactor.get(FacType.CONST).
                    getValue().equals(new BigInteger("-1"))) {
                    first = false;
                    result += this.typeFactor.get(FacType.CONST).toString();
                } else if (this.typeFactor.size() == 1) {
                    result += "-1";
                } else {
                    result += "-";
                }
            } else if (this.typeFactor.size() == 1) {
                result += "1";
            }
        }
        for (PolyFactor factor : this.typeFactor.values()) {
            if (factor.getType().equals(FacType.CONST)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                result += "*";
            }
            result += factor.toString();
        }
        return result;
    }
}
