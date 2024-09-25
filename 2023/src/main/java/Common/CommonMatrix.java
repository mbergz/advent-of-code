package Common;

import java.util.ArrayList;
import java.util.List;

public final class CommonMatrix {
    public static List<String> getGridAsColumns(List<String> inputGrid) {
        List<String> columns = new ArrayList<>();
        for (int i = 0; i < inputGrid.get(0).length(); i++) {
            StringBuilder sb = new StringBuilder();
            for (String currentString : inputGrid) {
                sb.append(currentString.charAt(i));
            }
            columns.add(sb.toString());
        }
        return columns;
    }
}

