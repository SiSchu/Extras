package net.eloxad.extras.utils;

import net.kyori.adventure.text.Component;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;

public class LoreFormatter {

    public static List<TextComponent> formatLore(int maxLength, List<TextComponent> components) {
        List<TextComponent> allLines = new ArrayList<>();
        for (TextComponent comp : components) {
            if (comp.equals(Component.empty())) {
                allLines.add(Component.empty()); 
                continue;
            }
            List<StyledWord> styledWords = flattenComponent(comp);
            allLines.addAll(chunkIntoLines(styledWords, maxLength, comp.style()));
        }
        return allLines;
    }

    private static List<StyledWord> flattenComponent(TextComponent component) {
        List<StyledWord> words = new ArrayList<>();
        addStyledWords(words, component.content(), component.style());
        for (Component child : component.children()) {
            if (child instanceof TextComponent textChild) {
                words.addAll(flattenComponent(textChild));
            }
        }
        return words;
    }

    private static void addStyledWords(List<StyledWord> words, String content, Style style) {
        if (content == null || content.isEmpty()) return;
        String[] splitted = content.split(" ");
        for (String w : splitted) {
            words.add(new StyledWord(w + " ", style));
        }
    }

    private static List<TextComponent> chunkIntoLines(List<StyledWord> styledWords, int maxLength, Style baseStyle) {
        List<TextComponent> lines = new ArrayList<>();
        StringBuilder currentLineBuffer = new StringBuilder();
        List<StyledWord> currentLineWords = new ArrayList<>();
        for (StyledWord sw : styledWords) {
            if (currentLineBuffer.length() + sw.text().length() > maxLength) {
                if (!currentLineWords.isEmpty()) {
                    lines.add(buildLine(currentLineWords, baseStyle));
                    currentLineWords.clear();
                }
                currentLineBuffer.setLength(0);
            }
            currentLineBuffer.append(sw.text());
            currentLineWords.add(sw);
        }
        if (!currentLineWords.isEmpty()) {
            lines.add(buildLine(currentLineWords, baseStyle));
        }
        return lines;
    }

    private static TextComponent buildLine(List<StyledWord> lineWords, Style baseStyle) {
        TextComponent.Builder builder = Component.text().style(baseStyle);
        for (StyledWord sw : lineWords) {
            builder.append(Component.text(sw.text(), sw.style()));
        }
        return builder.build();
    }

    private record StyledWord(String text, Style style) {}
}