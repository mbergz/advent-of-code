import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Day7 {
    private static final List<Character> CARD_LABEL_ORDER_PART1 =
            List.of('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');
    private static final List<Character> CARD_LABEL_ORDER_PART2 =
            List.of('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A');

    private static final int FIVE_OF_A_KIND = 7;
    private static final int FOUR_OF_A_KIND = 6;
    private static final int FULL_HOUSE = 5;
    private static final int THREE_OF_A_KIND = 4;
    private static final int TWO_PAIRS = 3;
    private static final int ONE_PAIR = 2;
    private static final int HIGHEST_CARD = 1;

    private static final Map<Integer, Integer> SCORE_MAPPING_FOR_TOP_AND_JOKER = Map.of(
            5, FIVE_OF_A_KIND,
            4, FOUR_OF_A_KIND,
            3, THREE_OF_A_KIND,
            2, ONE_PAIR);


    public static void main(String[] args) throws Exception {
        solvePart1();
        solvePart2();
    }

    private static void solvePart1() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day7.class.getResource("day7.txt").toURI()))) {
            List<Hand> hands = stream.map(line -> new Hand(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]), false))
                    .sorted()
                    .toList();
            System.out.println(calculateResult(hands));
        }
    }

    private static void solvePart2() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(Day7.class.getResource("day7.txt").toURI()))) {
            List<Hand> hands = stream.map(line -> new Hand(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]), true))
                    .sorted()
                    .toList();
            System.out.println(calculateResult(hands));
        }
    }

    private static long calculateResult(List<Hand> hands) {
        long result = 0L;
        int index = 0;
        for (int i = hands.size(); i > 0; i--) {
            result += (long) hands.get(index).getBid() * i;
            index++;
        }
        return result;
    }

    private static class Hand implements Comparable<Hand> {
        private final int score;
        private final String hand;
        private final int bid;
        private final boolean useJoker;

        public Hand(String hand, int bid, boolean useJoker) {
            this.hand = hand;
            this.bid = bid;
            this.useJoker = useJoker;
            this.score = calculateScore(hand);
        }

        public int getScore() {
            return this.score;
        }

        public String getHand() {
            return this.hand;
        }

        public int getBid() {
            return this.bid;
        }

        private int calculateScore(String line) {
            return this.useJoker ? calculateScorePart2(line) : calculateScorePart1(line);
        }

        private static int calculateScorePart1(String line) {
            Map<Character, Integer> occurrences = new HashMap<>();
            for (int i = 0; i < line.length(); i++) {
                occurrences.compute(line.charAt(i), (k, v) -> v != null ? ++v : 1);
            }
            List<Map.Entry<Character, Integer>> entryList = new ArrayList<>(occurrences.entrySet());
            entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            for (int i = 0; i <= 5; i++) {
                switch (entryList.get(i).getValue()) {
                    case 5:
                        return FIVE_OF_A_KIND;
                    case 4:
                        return FOUR_OF_A_KIND;
                    case 3:
                        return (i != 5 && entryList.get(i + 1).getValue() == 2) ? FULL_HOUSE : THREE_OF_A_KIND;
                    case 2:
                        return (i != 5 && entryList.get(i + 1).getValue() == 2) ? TWO_PAIRS : ONE_PAIR;
                    case 1:
                        return HIGHEST_CARD;
                }
            }
            return 0;
        }

        private static int calculateScorePart2(String line) {
            Map<Character, Integer> occurrences = new HashMap<>();
            int nbrOfJokers = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == 'J') {
                    nbrOfJokers++;
                } else {
                    occurrences.compute(line.charAt(i), (k, v) -> v != null ? ++v : 1);
                }
            }
            if (nbrOfJokers == 5) {
                return 7;
            }

            List<Map.Entry<Character, Integer>> entryList = new ArrayList<>(occurrences.entrySet());
            entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            switch (entryList.get(0).getValue()) {
                case 5:
                    return FIVE_OF_A_KIND;
                case 4:
                    if (nbrOfJokers > 0) {
                        return SCORE_MAPPING_FOR_TOP_AND_JOKER.get(nbrOfJokers + 4);
                    }
                    return FOUR_OF_A_KIND;
                case 3:
                    if (entryList.size() > 1 && entryList.get(1).getValue() == 2) {
                        return FULL_HOUSE;
                    }
                    if (nbrOfJokers > 0) {
                        return SCORE_MAPPING_FOR_TOP_AND_JOKER.get(nbrOfJokers + 3);
                    }
                    return THREE_OF_A_KIND;
                case 2:
                    if (entryList.size() > 1 && entryList.get(1).getValue() == 2) {
                        if (nbrOfJokers == 1) {
                            return FULL_HOUSE;
                        }
                        return TWO_PAIRS;
                    }
                    if (nbrOfJokers > 0) {
                        return SCORE_MAPPING_FOR_TOP_AND_JOKER.get(nbrOfJokers + 2);
                    }
                    return ONE_PAIR;
                case 1:
                    if (nbrOfJokers > 0) {
                        return SCORE_MAPPING_FOR_TOP_AND_JOKER.get(nbrOfJokers + 1);
                    }
                    return HIGHEST_CARD;
            }
            return 0;
        }

        @Override
        public int compareTo(Hand otherHand) {
            int otherHandScore = otherHand.getScore();
            if (this.score < otherHandScore) {
                return 1;
            } else if (this.score > otherHandScore) {
                return -1;
            }
            String otherHandString = otherHand.getHand();
            for (int i = 0; i <= 5; i++) {
                int valueThis = useJoker ? CARD_LABEL_ORDER_PART2.indexOf(this.hand.charAt(i)) :
                        CARD_LABEL_ORDER_PART1.indexOf(this.hand.charAt(i));
                int valueOther = useJoker ? CARD_LABEL_ORDER_PART2.indexOf(otherHandString.charAt(i)) :
                        CARD_LABEL_ORDER_PART1.indexOf(otherHandString.charAt(i));
                if (valueThis > valueOther) {
                    return -1;
                } else if (valueThis < valueOther) {
                    return 1;
                }
            }
            return 0;
        }
    }

}
