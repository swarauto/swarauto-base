package com.swarauto.game.profile;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommonConfig {
    private int maxRefills = 0;
    private int maxRuns = Integer.MAX_VALUE;
    private RunePickingRarity runePickingMinRarity = RunePickingRarity.RARITY_ALL;
    private RunePickingGrade runePickingMinGrade = RunePickingGrade.GRADE_ALL;
    private boolean recordSoldRunes = true;

    public boolean isSelectivePickRunes() {
        return !isPickAllRunes() && !isSellAllRunes();
    }

    public boolean isSellAllRunes() {
        return runePickingMinRarity == RunePickingRarity.RARITY_NONE
                || runePickingMinGrade == RunePickingGrade.GRADE_NONE;
    }

    public boolean isPickAllRunes() {
        return runePickingMinRarity == RunePickingRarity.RARITY_ALL
                && runePickingMinGrade == RunePickingGrade.GRADE_ALL;
    }

    @Getter
    public enum RunePickingRarity {
        RARITY_ALL(0, "KEEP ALL"),
        RARITY_MAGIC_AND_ABOVE(1, "Magic+"),
        RARITY_RARE_AND_ABOVE(2, "Rare+"),
        RARITY_HERO_AND_ABOVE(3, "Hero+"),
        RARITY_LEGEND(4, "Legend"),
        RARITY_NONE(5, "SELL ALL");
        private final int value;
        private final String text;
        private static List<String> texts;

        RunePickingRarity(int value, String text) {
            this.value = value;
            this.text = text;
        }

        public static RunePickingRarity byRarity(String rarity) {
            if (rarity == null || rarity.isEmpty()) return null;

            if (rarity.equalsIgnoreCase("normal")) return RARITY_ALL;
            if (rarity.equalsIgnoreCase("magic")) return RARITY_MAGIC_AND_ABOVE;
            if (rarity.equalsIgnoreCase("rare")) return RARITY_RARE_AND_ABOVE;
            if (rarity.equalsIgnoreCase("hero")) return RARITY_HERO_AND_ABOVE;
            if (rarity.equalsIgnoreCase("legend")) return RARITY_LEGEND;

            return null;
        }

        public static RunePickingRarity byIndex(int index) {
            try {
                return values()[index];
            } catch (Exception ignored) {
                return null;
            }
        }

        public static List<String> getTexts() {
            if (texts == null) {
                texts = new ArrayList<String>();
                for (RunePickingRarity item : values()) {
                    texts.add(item.getText());
                }
            }
            return texts;
        }

        public static int indexOf(RunePickingRarity item) {
            RunePickingRarity[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (item.equals(values[i])) return i;
            }
            return 0;
        }
    }

    @Getter
    public enum RunePickingGrade {
        GRADE_ALL(0, "KEEP ALL"),
        GRADE_5STAR_AND_ABOVE(1, "5-star+"),
        GRADE_6STAR(2, "6-star"),
        GRADE_NONE(3, "SELL ALL");

        private final int value;
        private final String text;
        private static List<String> texts;

        RunePickingGrade(int value, String text) {
            this.value = value;
            this.text = text;
        }

        public static List<String> getTexts() {
            if (texts == null) {
                texts = new ArrayList<String>();
                for (RunePickingGrade item : values()) {
                    texts.add(item.getText());
                }
            }
            return texts;
        }

        public static int indexOf(RunePickingGrade item) {
            RunePickingGrade[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (item.equals(values[i])) return i;
            }
            return 0;
        }

        public static RunePickingGrade byIndex(int index) {
            try {
                return values()[index];
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
