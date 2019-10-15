import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * @author gaoruiyuan
 */
public class PolyItem extends Util {
    private boolean hasSin2;
    private boolean hasCos2;
    private static final Pattern CON_FAC =
        Pattern.compile("[+-]?\\d+([+\\-*]|$)");
    private static final String ADD = "+";
    private static final String SUB = "-";
    private HashMap<FacType, HashMap<Factor, Factor>> typeFactor;

    PolyItem() {
        this.setIndex(0);
        this.typeFactor = new HashMap<>();
        this.initMap();
    }

    PolyItem(final String itemString) {
        // Input: string of item, not perfectly checked
        this.typeFactor = new HashMap<>();
        this.initMap();
        this.setIndex(0);
        this.setInput(itemString);
        Factor factor;
        boolean first = true;
        while (!this.hitEnd()) {
            String factorString = this.nextItem(false);
            if (factorString.startsWith("*")) {
                factorString = factorString.substring(1);
            }
            if (factorString == null) {
                System.out.println("WRONG FORMAT!");
                System.exit(0);
            }
            // System.out.println(item);
            if (CON_FAC.matcher(factorString).lookingAt()) {
                factor = new Const(factorString);
                first = false;
                addFactor(factor);
            } else {
                if (first) {
                    if (factorString.startsWith(ADD) || factorString
                        .startsWith(SUB)) {
                        if (factorString.startsWith(SUB)) {
                            factor = new Const("-1");
                            this.addFactor(factor);
                        }
                        factorString = factorString.substring(1);
                    }
                    first = false;
                }
                factor = Factor.typeDet(factorString);
                if (factor.getType().equals(FacType.EXPR) &&
                    factor.getValue().equals(BigInteger.ONE)) {
                    apartExpr((Expr) factor);
                } else {
                    addFactor(factor);
                }
            }
        }
    }

    private void apartExpr(Expr expr) {
        Poly poly = expr.getExpression();
        PolyItem item = poly.getItemMap().values().iterator().next();
        ArrayList<Factor> factors;
        for (HashMap<Factor, Factor> factorMap : item.typeFactor.values()) {
            for (Factor factor : factorMap.values()) {
                this.addFactor(factor);
            }
        }
    }

    public static PolyItem Zero() {
        PolyItem result = new PolyItem();
        result.addFactor(new Const("0"));
        return result;
    }

    void initMap() {
        this.typeFactor.put(FacType.CONST, new HashMap<>());
        this.typeFactor.put(FacType.TRIG_SIN, new HashMap<>());
        this.typeFactor.put(FacType.TRIG_COS, new HashMap<>());
        this.typeFactor.put(FacType.X_TERM, new HashMap<>());
        this.typeFactor.put(FacType.EXPR, new HashMap<>());
    }

    /**
     * 同类项合并：const值相加生成新的factor，组合进item中
     * @param sim
     */
    public void combine(PolyItem sim) {
        BigInteger valueA;
        BigInteger valueB;
        HashMap<Factor, Factor> constFactor;
        valueA = sim.getConFac();
        constFactor =  this.typeFactor.get(FacType.CONST);
        if (!constFactor.isEmpty()) {
            Factor thisConst = constFactor.values().iterator().next();
            valueB = thisConst.getValue();
            constFactor.remove(thisConst);
        } else {
            valueB = BigInteger.ONE;
        }
        this.addFactor(new Const(valueA.add(valueB).toString()));
    }

    /*
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
    */

    /**
     * 向item中添加因子，相同的指数相加，系数相乘
     * @param factor
     */
    public void addFactor(final Factor factor) {
        HashMap<Factor, Factor> simMap;
        Factor simFactor;
        FacType type = factor.getType();
        simMap = this.typeFactor.get(type);
        if (simMap.containsKey(factor)) {
            simFactor = simMap.get(factor);
            simMap.remove(factor);
            simFactor = simFactor.combine(factor);
        } else {
            simFactor = factor;
        }
        if (simFactor != null) {
            simMap = this.typeFactor.get(simFactor.getType());
            simMap.put(simFactor, simFactor);
        }
    }

    public ArrayList<PolyItem> calDeriv() {
        ArrayList<PolyItem> result = new ArrayList<>();
        PolyItem copy;
        ArrayList<Factor> factors;
        for (HashMap<Factor, Factor> factorMap : this.typeFactor.values()) {
            for (Factor factor : factorMap.values()) {
                copy = (PolyItem)this.clone();
                copy.typeFactor.get(factor.getType()).
                    remove(factor);
                factors = factor.calDeriv();
                for (Factor resultFactor : factors) {
                    copy.addFactor(resultFactor);
                }
                if (!copy.getConFac().equals(BigInteger.ZERO)) {
                    result.add(copy);
                }
            }
        }
        return result;
    }

    public BigInteger getConFac() {
        HashMap<Factor, Factor> constFactor =
            this.typeFactor.get(FacType.CONST);
        if (!constFactor.isEmpty()) {
            return constFactor.values().iterator().next().getValue();
        } else {
            return BigInteger.ONE;
        }
    }

    public Factor onlyFactor() {
        Factor result = null;
        for (FacType type : this.typeFactor.keySet()) {
            if (this.typeFactor.get(type).values().toArray().length == 1) {
                if (result == null) {
                    result = this.typeFactor.get(type).
                        values().iterator().next();
                } else {
                    return null;
                }
            }
        }
        return result;
    }

    private boolean constItem() {
        FacType[] list = {FacType.X_TERM, FacType.TRIG_SIN,
            FacType.TRIG_COS, FacType.EXPR};
        for (FacType type : list) {
            if (!this.typeFactor.get(type).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String hashString() {
        String string = "";
        String result = "";
        HashMap<Factor, Factor> factorMap;
        FacType[] list = {FacType.X_TERM, FacType.TRIG_SIN,
            FacType.TRIG_COS, FacType.EXPR};
        for (FacType type : list) {
            factorMap = this.typeFactor.get(type);
            LinkedList<String> strs = new LinkedList<>();
            for (Factor factor : factorMap.values()) {
                // factor特征不包含指数，加入指数
                string = factor.getValue().toString();
                string += factor.hashString();
                this.sortedInsert(string, strs);
            }
            for (String str : strs) {
                // 相同类型的factor保证互异（合并）
                result += str;
            }
            // 不同类型之间的factor加逗号
            result += ",";
        }
        return result;
    }

    @Override
    public Object clone() {
        PolyItem item = new PolyItem();
        item.typeFactor = new HashMap<>();
        for (FacType type : this.typeFactor.keySet()) {
            item.typeFactor.put(type,
                (HashMap) this.typeFactor.get(type).clone());
        }
        item.setInput(this.getInput());
        item.setInput(this.getInput());
        return item;
    }

    @Override
    public String toString() {
        String result = "";
        boolean first = true;
        FacType[] list = {FacType.X_TERM, FacType.TRIG_SIN,
            FacType.TRIG_COS, FacType.EXPR};
        HashMap<Factor, Factor> factorMap;
        factorMap = this.typeFactor.get(FacType.CONST);
        if (!factorMap.isEmpty()) {
            if (!factorMap.values().iterator().next().
                getValue().equals(BigInteger.ONE)) {
                if (!factorMap.values().iterator().next().
                    getValue().equals(BigInteger.valueOf(-1))) {
                    first = false;
                    result += this.typeFactor.get(FacType.CONST).
                        values().iterator().next().toString();
                } else if (this.constItem()) {
                    result += "-1";
                } else {
                    result += "-";
                }
            } else if (this.constItem()) {
                result += "1";
            }
        }
        for (FacType type : list) {
            factorMap = this.typeFactor.get(type);
            for (Factor factor : factorMap.values()) {
                if (first) {
                    first = false;
                } else {
                    result += "*";
                }
                result += factor.toString();
            }
        }
        return result;
    }
}
