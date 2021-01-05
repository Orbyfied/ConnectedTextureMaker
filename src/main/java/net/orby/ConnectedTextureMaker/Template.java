package net.orby.ConnectedTextureMaker;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Template {

    public static final int ELEMENTTYPE_LINE = 0;
    public static final int ELEMENTTYPE_CORNER = 1;

    // Size = elementsize * resolution/14 + sizeadd
    public static final Integer[] ELEMENTSIZE_CORNER = new Integer[]{1, 1};
    public static final Integer[] ELEMENTSIZE_LINE = new Integer[]{14, 1};

    // A line takes the corners as well

    // All of the blocks, if something is in the string it will not be replaced.
    // ElementCoords = x1, y1, x2, y2

    // all = everything
    // ac = all corners
    // tlc = top left corner
    // trc = top right corner
    // blc = bottom left corner
    // brc = bottom right corner
    // b = bottom line
    // l = left line
    // r = right line
    // t = top line
    public static String[] template = new String[]{
            "all", // 0
            "l t b", // 1
            "t b", // 2
            "r t b", // 3
            "l t brc", // 4
            "r t blc", // 5
            "l brc trc", // 6
            "t blc brc", // 7
            "tlc blc brc", // 8
            "tlc trc blc", // 9
            "trc brc", // 10
            "blc brc", // 11
            "l t r", // 12
            "l t", // 13
            "t", // 14
            "t r", // 15
            "l b trc", // 16
            "r b tlc", // 17
            "b tlc trc", // 18
            "r tlc blc", // 19
            "blc brc trc", // 20
            "tlc trc brc", // 21
            "tlc trc", // 22
            "tlc blc", // 23
            "l r", // 24
            "l", // 25
            "", // 26    F U L L    B L O C K
            "r", // 27
            "l trc", // 28
            "t brc", // 29
            "l brc", // 30
            "t blc", // 31
            "brc", // 32
            "blc", // 33
            "tlc brc", // 34
            "blc trc", // 35
            "l b r", // 36
            "l b", // 37
            "b", // 38
            "b r", // 39
            "b tlc", // 40
            "blc r", // 41
            "b trc", // 42
            "r tlc", // 43
            "trc", // 44
            "tlc", // 45
            "blc trc tlc brc" // 46
    };

    public static List<Integer> elementTypes;
    public static List<String> elements;
    public static List<String> borderQualifiers = Arrays.asList("b", "l", "r", "t");

    public static List<String> parse(int index){
        return parse(template[index]);
    }

    public static List<String> parse(String index){
        if (index.contains(" "))
            return Arrays.asList(index.split(" "));
        else if (!index.equals(""))
            return Collections.singletonList(index);
        else
            return new ArrayList<>();
    }

    public static List<Integer[]> parseCoords(List<String> strings){
        List<Integer[]> ints = new ArrayList<>();
//        System.out.println(strings);
        int type = 0;
        elements = new ArrayList<>();
        elementTypes = new ArrayList<>();
        for (String element : strings) {
            type = 0;

            // new format: [x,y,sizeX,sizeY]

//            System.out.println(element);

            // type = 1: corner
            // type = 2: border (line)
            // type = 3: all corners
            // type = 4: all

            if (element.contains("c")) {
                type = 1;
                element = element.replace("c", "");
                switch (element) {
                    case "tl":
                        elementTypes.add(ELEMENTTYPE_CORNER);
                        ints.add(new Integer[]{0, 0, 1, 1});
                        break;
                    case "tr":
                        elementTypes.add(ELEMENTTYPE_CORNER);
                        ints.add(new Integer[]{16, 0, -1, 1});
                        break;
                    case "bl":
                        elementTypes.add(ELEMENTTYPE_CORNER);
                        ints.add(new Integer[]{0, 16, 1, -1});
                        break;
                    case "br":
                        elementTypes.add(ELEMENTTYPE_CORNER);
                        ints.add(new Integer[]{16, 16, -1, -1});
                        break;
                }
                elements.add(element);
            } else if (borderQualifiers.contains(element)) {
                type = 2;
                switch (element) {
                    case "l":
                        elementTypes.add(ELEMENTTYPE_LINE);
                        ints.add(new Integer[]{0, 0, 1, 16});
                        break;
                    case "t":
                        elementTypes.add(ELEMENTTYPE_LINE);
                        ints.add(new Integer[]{0, 0, 16, 1});
                        break;
                    case "r":
                        elementTypes.add(ELEMENTTYPE_LINE);
                        ints.add(new Integer[]{16, 16, -1, -16});
                        break;
                    case "b":
                        elementTypes.add(ELEMENTTYPE_LINE);
                        ints.add(new Integer[]{16, 16, -16, -1});
                        break;
                }
                elements.add(element);
            } else if (element.equals("ac")) {
                type = 3;
                ints.add(new Integer[]{0, 0, 1, 1}); // tl
                ints.add(new Integer[]{0, 16, -1, -1}); // tr
                ints.add(new Integer[]{0, 16, -1, -1}); // bl
                ints.add(new Integer[]{16, 16, -1, -1}); // br
            } else if (element.equals("all")) {
                type = 4;
                elementTypes.add(ELEMENTTYPE_LINE);
                elementTypes.add(ELEMENTTYPE_LINE);
                elementTypes.add(ELEMENTTYPE_LINE);
                elementTypes.add(ELEMENTTYPE_LINE);
                elements.add("l");
                elements.add("t");
                elements.add("r");
                elements.add("b");
                ints.add(new Integer[]{0, 0, 1, 16}); // l
                ints.add(new Integer[]{0, 0, 16, 1}); // t
                ints.add(new Integer[]{16, 16, -1, -16}); // r
                ints.add(new Integer[]{16, 16, -16, -1}); // b
            } else
                throw new IllegalArgumentException("unknown mask element.");
        }

        return ints;
    }

    public static List<Integer[]> parseCoords(int index){
        return parseCoords(parse(index));
    }

    public static Integer[] processRect(Integer[] integers, String element, float stretchx, float stretchy, int res){
        Integer[] ints = integers.clone();
        res = res / 16;
        switch (element){
            case "l":
                ints[3] = 16*res;
                break;
            case "r":
                ints[3] = -16*res;
                break;
            case "t":
                ints[2] = 16*res;
                break;
            case "b":
                ints[2] = -16*res;
                break;
        }
        if (ints[2] < 0){
            ints[2] = -ints[2];
            ints[0] = ints[0] - ints[2];
        }
        if (ints[3] < 0){
            ints[3] = -ints[3];
            ints[1] = ints[1] - ints[3];
        }
        return ints;
    }

    public static List<Integer[]> createMask(List<Integer[]> coords, int resolution, float stretchx, float stretchy){
        List<Integer[]> list = new ArrayList<>();
        Integer[] c;
        float r = resolution / 16f;
        int e = 0;
        for (Integer[] c1 : coords){
            c = new Integer[4];
            c[0] = (int) Math.floor((c1[0] * r)); // X1
            c[1] = (int) Math.floor((c1[1] * r)); // Y1
            c[2] = (int) Math.floor((c1[2] * r * stretchx)); // sizeX
            c[3] = (int) Math.floor((c1[3] * r * stretchy)); // sizeY
            list.add(processRect(c, elements.get(e), stretchx, stretchy, resolution));
            e++;
        }
        return list;
    }
}
