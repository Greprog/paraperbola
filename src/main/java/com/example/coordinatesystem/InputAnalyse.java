package com.example.coordinatesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputAnalyse {

    static List<Lexeme> lexemes;
    static LexemeBuffer lexemeBuffer;
    static String output;

    public static double[] inputAnalyse(String inputText) {
        lexemes = lexAnalyze(inputText);
        lexemeBuffer = new LexemeBuffer(lexemes);
        output = expr(lexemeBuffer);
        System.out.println("Output: " + output);

        lexemes = lexAnalyze(output);
        lexemeBuffer = new LexemeBuffer(lexemes);
        output = expr(lexemeBuffer);
        System.out.println("Output2: " + output);
        lexemes.add(0, lexemes.get(lexemes.size()-1));

        double[] abce = new double[] {0, 0, 0, 1};

        for (int i = lexemes.size() - 1; i >= 0; i--) {

            if (lexemes.get(i).type == LexemeType.NUMBER && lexemes.get(i).value.contains("x")) {
                String n = lexemes.get(i).value;
                Pattern pE = Pattern.compile("-?\\d+[.]?\\d*");
                Matcher mE = pE.matcher(n);
                StringBuilder sb = new StringBuilder();
                while (mE.find()) {
                    sb.append(mE.group());
                    n = String.valueOf(sb);
                }
                if (lexemes.get(i-1).type == LexemeType.OP_MINUS) {
                    n = "-" + n;
                }
                System.out.println(n);
                if (lexemes.get(i+1).type != LexemeType.EXPONENT) {
                    abce[1] = Double.parseDouble(n);
                } if (lexemes.get(i+1).type == LexemeType.EXPONENT) {
                    abce[0] = Double.parseDouble(n);
                }
            }

            if (lexemes.get(i).type == LexemeType.NUMBER && !lexemes.get(i).value.contains("x")) {
                abce[2] = Double.parseDouble(lexemes.get(i).value);
                if (lexemes.get(i-1).type == LexemeType.OP_MINUS) {
                    abce[2] = abce[2] * -1;
                }
            }

            if (lexemes.get(i).type == LexemeType.EXPONENT) {
                String n = lexemes.get(i).value;
                Pattern pE = Pattern.compile("-?\\d+[.]?\\d*");
                Matcher mE = pE.matcher(lexemes.get(i).value);
                StringBuilder sb = new StringBuilder();
                while (mE.find()) {
                    sb.append(mE.group());
                    n = String.valueOf(sb);
                }
                abce[3] = Double.parseDouble(n);
            }
        }

        if (abce[0] == 0) {
            abce[0] = abce[1];
            abce[1] = 0;
        }
        for (Double x : abce) {
            System.out.print(x + " ");
        }
        return abce;
    }
    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
        NUMBER,
        //VARIABLE,
        EXPONENT,
        EOF;
    }

    public static class Lexeme {
        LexemeType type;
        String value;

        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class LexemeBuffer {
        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    public static List<Lexeme> lexAnalyze(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case 'x':
                    lexemes.add(new Lexeme(LexemeType.NUMBER, c));
                    pos++;
                    continue;
                case '^':
                    StringBuilder sbE = new StringBuilder();
                    do {
                        sbE.append(c);
                        pos++;
                        if (pos >= expText.length()) {
                            break;
                        }
                        c = expText.charAt(pos);
                    } while (c == '-' && expText.charAt(pos - 1) == '^' || c == '.' || c <= '9' && c >= '0');
                    lexemes.add(new Lexeme(LexemeType.EXPONENT, sbE.toString()));
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0' || c == '.') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0' || c == '.' || c == 'x');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    public static String expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return "";
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static String plusminus(LexemeBuffer lexemes) {
        String value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    String factor = multdiv(lexemes);
                    value = plusminusHelper(value, factor, lexeme.value);
                    break;
                case OP_MINUS:
                    factor = multdiv(lexemes);
                    value = plusminusHelper(value, factor, lexeme.value);
                    break;
                case EOF:
                //case VARIABLE:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static String multdiv(LexemeBuffer lexemes) {
        String value = exponent(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    String factor = exponent(lexemes);
                    if (!value.contains("x") && !factor.contains("x")) {
                        value = String.valueOf(Double.parseDouble(value) * Double.parseDouble(factor));
                    } else {
                        value = multdivHelper(value, factor, lexeme.value);
                    }
                    break;
                case OP_DIV:
                    factor = exponent(lexemes);
                    if (!factor.contains("x") && Double.parseDouble(factor) == 0) {
                        throw new RuntimeException("division by zero is not possible: " + factor
                                + "at position: " + lexemes.getPos());
                    }
                    if (!value.contains("x") && !factor.contains("x")) {
                        value = String.valueOf(Double.parseDouble(value) / Double.parseDouble(factor));
                    } else {
                        value = multdivHelper(value, factor, lexeme.value);
                    }
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                case EXPONENT:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static String exponent(LexemeBuffer lexemes) {
        String value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case EXPONENT:
                    value = exponentiation(value, lexeme.value);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_MUL:
                case OP_DIV:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static String factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
                if (lexeme.value.equals("x")) {
                    lexeme.value = "1x";
                    System.out.println(lexeme.value);
                }
                return lexeme.value;
            /*case VARIABLE:
                if (lexeme.value.equals("x")) {
                    lexeme.value = "1x";
                    System.out.println(lexeme.value);
                }
                return lexeme.value;*/
            case OP_MINUS:
                lexeme = lexemes.next();
                return "-" + lexeme.value;
            case LEFT_BRACKET:
                String value = plusminus(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + "at position: " + lexemes.getPos());
        }
    }

    public static String plusminusHelper(String value, String factor, String op) {
        List<String> valueArray = new ArrayList<>();
        List<String> factorArray = new ArrayList<>();

        String val = value;
        String fac = factor;
        val = val.replaceAll("(\\^-)", "^m");
        fac = fac.replaceAll("(\\^-)", "^m");

        Pattern pVal = Pattern.compile("-?\\d*[.]?\\d*x?\\^?m?\\d*[.]?\\d*");
        Matcher mVal = pVal.matcher(val);
        while (mVal.find()) {
            String v = val.substring(mVal.start(), mVal.end());
            if (!v.equals("")) {
                valueArray.add(v);
            }
        }

        Pattern pFac = Pattern.compile("-?\\d*[.]?\\d*x?\\^?m?\\d*[.]?\\d*");
        Matcher mFac = pFac.matcher(fac);
        while (mFac.find()) {
            String f = fac.substring(mFac.start(), mFac.end());
            if (!f.equals("")) {
                factorArray.add(f);
            }
        }

        for (int i = 0; i < valueArray.size(); i++) {

            String aV = "";
            String eVal = "";
            String xVal = "";
            String nVal = "";

            /////////////получаем степень
            Pattern pEVal = Pattern.compile("\\^+m?\\d+[.]?\\d*");
            Matcher mEVal = pEVal.matcher(valueArray.get(i));
            while (mEVal.find()) {
                eVal = valueArray.get(i).substring(mEVal.start(), mEVal.end());
            }
            if (!eVal.equals("")) {
                aV = valueArray.get(i).replaceAll("\\^+m?\\d+[.]?\\d*", "n");
            }

            //////////////////получаем х
            Pattern pXVal = Pattern.compile("x+");
            Matcher mXVal = pXVal.matcher(valueArray.get(i));
            while (mXVal.find()) {
                xVal = valueArray.get(i).substring(mXVal.start(), mXVal.end());
            }

            ///////////////////получаем число
            if (aV.equals(""))
                aV = valueArray.get(i);
            Pattern pNVal = Pattern.compile("-?\\d*[.]?\\d*");
            Matcher mNVal = pNVal.matcher(aV);
            while (mNVal.find()) {
                if (!valueArray.get(i).substring(mNVal.start(), mNVal.end()).equals("")) {
                    nVal = valueArray.get(i).substring(mNVal.start(), mNVal.end());
                }
            }
            if (nVal.equals("") || nVal.equals("-")) {
                nVal = nVal + "1";
            }

            for (int m = 0; m < factorArray.size(); m++) {
                String bFac = "";
                String eFac = "";
                String xFac = "";
                String nFac = "";

                ///////////////получаем степень
                Pattern pEFac = Pattern.compile("\\^+m?\\d+[.]?\\d*");
                Matcher mEFac = pEFac.matcher(factorArray.get(m));
                while (mEFac.find()) {
                    eFac = factorArray.get(m).substring(mEFac.start(), mEFac.end());
                }
                if (!eFac.equals("")) {
                    bFac = factorArray.get(m).replaceAll("\\^+m?\\d+[.]?\\d*", "n");
                }

                //////////////////получаем х
                Pattern pXFac = Pattern.compile("x+");
                Matcher mXFac = pXFac.matcher(factorArray.get(m));
                while (mXFac.find()) {
                    xFac = factorArray.get(m).substring(mXFac.start(), mXFac.end());
                }

                ///////////////////получаем число
                if (bFac.equals(""))
                    bFac = factorArray.get(m);
                Pattern pNFac = Pattern.compile("-?\\d*[.]?\\d*");
                Matcher mNFac = pNFac.matcher(bFac);
                while (mNFac.find()) {
                    if (!factorArray.get(m).substring(mNFac.start(), mNFac.end()).equals("")) {
                        nFac = factorArray.get(m).substring(mNFac.start(), mNFac.end());
                    }
                }
                if (nFac.equals("") || nFac.equals("-")) {
                    nFac = nFac + "1";
                }

                if (eVal.equals(eFac) && xVal.equals(xFac)) {
                    String n = "";
                    switch (op) {
                        case "+":
                            n = String.valueOf(Double.parseDouble(nVal) + Double.parseDouble(nFac));
                            break;
                        case "-":
                            n = String.valueOf(Double.parseDouble(nVal) - Double.parseDouble(nFac));
                            break;
                    }
                    aV = "";
                    aV += n;
                    if (!xVal.equals("")) {
                        aV += xVal;
                    } if (!eVal.equals("")) {
                        aV += eVal;
                    }

                    valueArray.set(i, aV);
                    factorArray.remove(m);
                }
            }
        }

        String end = "";

        for (String x : valueArray) {
            if (!x.startsWith("-") && !x.equals("0.0")) {
                end += "+";
            }
            if (!x.equals("0.0")) {
                end += x;
            }
        }

        if (factorArray.size() > 0 && op.equals("-")) {
            if (!factorArray.get(0).startsWith("-")) {
                String minus = factorArray.get(0);
                minus = "-" + minus;
                factorArray.set(0, minus);
            }
        }

        for (String x : factorArray) {
            if (!x.startsWith("-") && !x.equals("0.0")) {
                end += "+";
            }
            if (!x.equals("0.0")) {
                end += x;
            }
        }

        if (end.startsWith("+")) {
            end = end.substring(1);
        }
        end = end.replaceAll("\\^m", "^-");
        return end;
    }

    public static String multdivHelper(String value, String factor, String operator) {
        List<String> valueArray = new ArrayList<>();
        List<String> factorArray = new ArrayList<>();
        List<String> resultArray = new ArrayList<>();

        String op = operator;
        String val = value;
        String fac = factor;
        val = val.replaceAll("(\\^-)", "^m");
        fac = fac.replaceAll("(\\^-)", "^m");

        Pattern pVal = Pattern.compile("-?\\d*[.]?\\d*x?\\^?m?\\d*[.]?\\d*");
        Matcher mVal = pVal.matcher(val);
        while (mVal.find()) {
            String v = val.substring(mVal.start(), mVal.end());
            if (!v.equals("")) {
                valueArray.add(v);
            }
        }

        Pattern pFac = Pattern.compile("-?\\d*[.]?\\d*x?\\^?m?\\d*[.]?\\d*");
        Matcher mFac = pFac.matcher(fac);
        while (mFac.find()) {
            String f = fac.substring(mFac.start(), mFac.end());
            if (!f.equals("")) {
                factorArray.add(f);
            }
        }

        for (int i = 0; i < valueArray.size(); i++) {

            String aV = "";
            String eVal = "";
            String xVal = "";
            String nVal = "";

            /////////////получаем степень
            Pattern pEVal = Pattern.compile("\\^+m?\\d+[.]?\\d*");
            Matcher mEVal = pEVal.matcher(valueArray.get(i));
            while (mEVal.find()) {
                eVal = valueArray.get(i).substring(mEVal.start(), mEVal.end());
            }
            if (!eVal.equals("")) {
                aV = valueArray.get(i).replaceAll("\\^+m?\\d+[.]?\\d*", "n");
                eVal = eVal.replaceAll("\\^m", "^-");
                eVal = eVal.replaceAll("\\^", "");
            } else {
                eVal = "0";
            }


            //////////////////получаем х
            Pattern pXVal = Pattern.compile("x+");
            Matcher mXVal = pXVal.matcher(valueArray.get(i));
            while (mXVal.find()) {
                xVal = valueArray.get(i).substring(mXVal.start(), mXVal.end());
            }
            if (xVal.equals("x") && eVal.equals("0")) {
                eVal = "1";
            }

            ///////////////////получаем число
            if (aV.equals(""))
                aV = valueArray.get(i);
            Pattern pNVal = Pattern.compile("-?\\d*[.]?\\d*");
            Matcher mNVal = pNVal.matcher(aV);
            while (mNVal.find()) {
                if (!valueArray.get(i).substring(mNVal.start(), mNVal.end()).equals("")) {
                    nVal = valueArray.get(i).substring(mNVal.start(), mNVal.end());
                }
            }
            if (nVal.equals("") || nVal.equals("-")) {
                nVal = nVal + "1";
            }

            for (int m = 0; m < factorArray.size(); m++) {
                String bFac = "";
                String eFac = "";
                String xFac = "";
                String nFac = "";

                ///////////////получаем степень
                Pattern pEFac = Pattern.compile("\\^+m?\\d+[.]?\\d*");
                Matcher mEFac = pEFac.matcher(factorArray.get(m));
                while (mEFac.find()) {
                    eFac = factorArray.get(m).substring(mEFac.start(), mEFac.end());
                }
                if (!eFac.equals("")) {
                    bFac = factorArray.get(m).replaceAll("\\^+m?\\d+[.]?\\d*", "n");
                    eFac = eFac.replaceAll("\\^m", "^-");
                    eFac = eFac.replaceAll("\\^", "");
                } else {
                    eFac = "0";
                }

                //////////////////получаем х
                Pattern pXFac = Pattern.compile("x+");
                Matcher mXFac = pXFac.matcher(factorArray.get(m));
                while (mXFac.find()) {
                    xFac = factorArray.get(m).substring(mXFac.start(), mXFac.end());
                }
                if (xFac.equals("x") && eFac.equals("0")) {
                    eFac = "1";
                }

                ///////////////////получаем число
                if (bFac.equals(""))
                    bFac = factorArray.get(m);
                Pattern pNFac = Pattern.compile("-?\\d*[.]?\\d*");
                Matcher mNFac = pNFac.matcher(bFac);
                while (mNFac.find()) {
                    if (!factorArray.get(m).substring(mNFac.start(), mNFac.end()).equals("")) {
                        nFac = factorArray.get(m).substring(mNFac.start(), mNFac.end());
                    }
                }
                if (nFac.equals("") || nFac.equals("-")) {
                    nFac = nFac + "1";
                }

                String nRes = "";
                String eRes = "";

                switch (op) {
                    case "*":
                        nRes = String.valueOf(Double.parseDouble(nVal) * Double.parseDouble(nFac));
                        break;
                    case "/":
                        nRes = String.valueOf(Double.parseDouble(nVal) / Double.parseDouble(nFac));
                        break;
                }

                if (Double.parseDouble(nRes) != 0) {
                    aV = "";
                    if (!nRes.equals("1")/* && (xVal.equals("") || xFac.equals(""))*/) {
                        aV += nRes;
                    }
                    switch (op) {
                        case "*":
                            eRes = String.valueOf(Double.parseDouble(eVal) + Double.parseDouble(eFac));
                            break;
                        case "/":
                            eRes = String.valueOf(Double.parseDouble(eVal) - Double.parseDouble(eFac));
                            break;
                    }
                    if (Double.parseDouble(eRes) == 1) {
                        aV += "x";
                    }
                    if (Double.parseDouble(eRes) != 1 && Double.parseDouble(eRes) != 0) {
                        aV += "x" + "^" + eRes;
                    }
                }
                resultArray.add(aV);
            }
        }
        String end = "";
        for (String x : resultArray) {
            if (!x.startsWith("-") && !x.equals("0.0")) {
                end += "+";
            }
            if (!x.equals("0.0")) {
                end += x;
            }
        }

        if (end.startsWith("+")) {
            end = end.substring(1);
        }
        end = end.replaceAll("\\^m", "^-");
        return end;
    }

    public static String exponentiation(String value, String exponent) {
        String exp = exponent.replaceAll("\\^", "");
        String val = value;
        List<String> valueArray = new ArrayList<>();
        val = val.replaceAll("(\\^-)", "^m");

        Pattern pVal = Pattern.compile("-?\\d*[.]?\\d*x?\\^?m?\\d*[.]?\\d*");
        Matcher mVal = pVal.matcher(val);
        while (mVal.find()) {
            String v = val.substring(mVal.start(), mVal.end());
            if (!v.equals("")) {
                valueArray.add(v);
            }
        }

        String a = "";
        String b = "";
        String c = "";
        String end = "";

        for (int i = 0; i < valueArray.size(); i++) {

            String aV = "";
            String eVal = "";
            String xVal = "";
            String nVal = "";

            /////////////получаем степень
            Pattern pEVal = Pattern.compile("\\^+m?\\d+[.]?\\d*");
            Matcher mEVal = pEVal.matcher(valueArray.get(i));
            while (mEVal.find()) {
                eVal = valueArray.get(i).substring(mEVal.start(), mEVal.end());
            }
            if (!eVal.equals("")) {
                aV = valueArray.get(i).replaceAll("\\^+m?\\d+[.]?\\d*", "n");
                eVal = eVal.replaceAll("\\^m", "^-");
                eVal = eVal.replaceAll("\\^", "");
            } else {
                eVal = "0";
            }

            //////////////////получаем х
            Pattern pXVal = Pattern.compile("x+");
            Matcher mXVal = pXVal.matcher(valueArray.get(i));
            while (mXVal.find()) {
                xVal = valueArray.get(i).substring(mXVal.start(), mXVal.end());
            }
            if (xVal.equals("x") && eVal.equals("0")) {
                eVal = "1";
            }

            ///////////////////получаем число
            if (aV.equals(""))
                aV = valueArray.get(i);
            Pattern pNVal = Pattern.compile("-?\\d*[.]?\\d*");
            Matcher mNVal = pNVal.matcher(aV);
            while (mNVal.find()) {
                if (!valueArray.get(i).substring(mNVal.start(), mNVal.end()).equals("")) {
                    nVal = valueArray.get(i).substring(mNVal.start(), mNVal.end());
                }
            }
            if (nVal.equals("") || nVal.equals("-")) {
                nVal = nVal + "1";
            }

            if (valueArray.size() == 2 && Double.parseDouble(exp) == 2) {
                String nVale = String.valueOf(Math.pow(Double.parseDouble(nVal), Double.parseDouble(exp)));
                if (b == "") {
                    b = nVal;
                } else {
                    b = String.valueOf(Double.parseDouble(b) * Double.parseDouble(nVal));
                }
                if (xVal.equals("")) {
                    c = nVale;
                }
                if (!eVal.equals("0")) {
                    if (Double.parseDouble(nVale) != 1) {
                        a += nVale;
                    }
                    a +="x" + "^" + "2";
                }
            }
            if (valueArray.size() == 1) {
                aV = "";
                if (!xVal.contains("x")) {
                    nVal = String.valueOf(Math.pow(Double.parseDouble(nVal), Double.parseDouble(exp)));
                    aV += nVal;
                }
                if (xVal.contains("x") && (eVal.equals("0") || eVal.equals("1"))) {
                    aV += nVal + "x" + "^" + exp;
                }
                if (!eVal.equals("0") && !eVal.equals("1")) {
                    eVal = String.valueOf(Double.parseDouble(eVal) * Double.parseDouble(exp));
                    aV += nVal + "x" + "^" + eVal;
                }
                end = aV;
                return  end;
            }
        }
        if (!b.equals("")) {
            b = (Double.parseDouble(b) * 2) + "x";
        }
        if (!b.startsWith("-")) {
            b = "+" + b;
        }
        String quadratic = a + b + "+" + c;
        return quadratic;
    }

    public static double[] parabolaEquation (double[] abce) {
        double discX = Math.pow(abce[1], 2)  - (4 * abce[0] * abce[2]);
        double x1 = ((-abce[1]) + Math.sqrt(discX)) / (2 * abce[0]);
        double x2 = ((-abce[1]) - Math.sqrt(discX)) / (2 * abce[0]);

        double bParabola = abce[1] / abce[0] / 2;
        double cParabola = abce[0] * (-Math.pow(bParabola, 2) + (abce[2] / abce[0]));

        return new double[] {abce[0], bParabola, cParabola, abce[3], discX, x1, x2};
    }

}
