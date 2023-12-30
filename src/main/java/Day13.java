import Common.CommonMatrix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day13 {

    public static void main(String[] args) throws Exception {
        solve(); //34772, 35554
    }

    private static void solve() throws Exception {
        List<List<String>> patterns = new ArrayList<>();
        List<String> pattern = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Day13.class.getResourceAsStream("day13.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    pattern.add(line);
                } else {
                    patterns.add(pattern);
                    pattern = new ArrayList<>();
                }
            }
            patterns.add(pattern);
        }
        int scorePart1 = patterns
                .stream()
                .map(p -> calculateScoreForPattern(p, false))
                .reduce(Integer::sum)
                .orElse(0);
        System.out.println(scorePart1);

        int scorePart2 = patterns
                .stream()
                .map(p -> calculateScoreForPattern(p, true))
                .reduce(Integer::sum)
                .orElse(0);
        System.out.println(scorePart2);
    }

    private static int calculateScore(List<String> pattern) {
        String lastRow;
        for (int i = 1; i < pattern.size(); i++) {
            lastRow = pattern.get(i - 1);
            if (lastRow.equals(pattern.get(i))) {
                if (expandAndTest(pattern, i))
                    return i;
            }
        }
        return 0;
    }

    private static int calculateScorePart2(List<String> pattern) {
        String lastRow;
        for (int i = 1; i < pattern.size(); i++) {
            lastRow = pattern.get(i - 1);
            if (lastRow.equals(pattern.get(i))) {
                if (expandAndTestPart2(pattern, i, false))
                    return i;
            } else if (areAlmostIdentical(lastRow, pattern.get(i))) {
                if (expandAndTestPart2(pattern, i, true))
                    return i;
            }

        }
        return 0;
    }

    private static boolean areAlmostIdentical(String str1, String str2) {
        int diffCount = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i) && ++diffCount > 1) {
                return false;
            }
        }
        return diffCount == 1;
    }

    private static boolean expandAndTestPart2(List<String> pattern, int i, boolean hasReplacedPrior) {
        if (i - 2 < 0 || i + 1 >= pattern.size()) {
            return hasReplacedPrior;
        }
        int expandBack = i - 2;
        int expandForward = i + 1;
        boolean hasReplacedNow = false;
        while (true) {
            String back = pattern.get(expandBack);
            String forward = pattern.get(expandForward);
            expandBack--;
            expandForward++;
            if (!back.equals(forward)) {
                if (!hasReplacedPrior && !hasReplacedNow) {
                    if (areAlmostIdentical(back, forward)) {
                        hasReplacedNow = true;
                    }
                } else {
                    break;
                }
            }
            if (expandBack < 0 || expandForward == pattern.size()) {
                if (!hasReplacedPrior && hasReplacedNow) {
                    return true;
                } else return hasReplacedPrior;
            }
        }
        return false;
    }

    private static boolean expandAndTest(List<String> pattern, int i) {
        if (i - 2 < 0 || i + 1 >= pattern.size()) {
            return true;
        }
        int expandBack = i - 2;
        int expandForward = i + 1;
        while (true) {
            String back = pattern.get(expandBack);
            String forward = pattern.get(expandForward);
            expandBack--;
            expandForward++;
            if (!back.equals(forward)) {
                break;
            }
            if (expandBack < 0 || expandForward == pattern.size()) {
                return true;
            }
        }
        return false;
    }


    private static int calculateScoreForPattern(List<String> pattern, boolean part2) {
        int horizontalScore = part2 ? calculateScorePart2(pattern) : calculateScore(pattern);
        if (horizontalScore != 0) {
            return 100 * horizontalScore;
        }
        List<String> columns = CommonMatrix.getGridAsColumns(pattern);
        return part2 ? calculateScorePart2(columns) : calculateScore(columns);
    }

}
