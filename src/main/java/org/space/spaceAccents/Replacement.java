package org.space.spaceAccents;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replacement: uses a compiled Pattern and a literal replacement string.
 * Replacement is applied in a case-aware manner:
 * - если весь матч в верхнем регистре -> возвращаем replacement.toUpperCase()
 * - если первая буква матча заглавна -> Capitalize(replacement)
 * - иначе -> replacement.toLowerCase()
 *
 * Замечание: replacement хранится как literal (поддержка групп не реализована).
 */
public class Replacement {
    private final Pattern pattern;
    private final String replacement;

    public Replacement(String fromRegex, String replacement) {
        // Компилируем с UNICODE_CASE и CASE_INSENSITIVE,
        // чтобы матчи по-русски тоже были корректны.
        this.pattern = Pattern.compile(fromRegex, Pattern.UNICODE_CASE);
        this.replacement = replacement;
    }

    public String apply(String input) {
        Matcher m = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String match = m.group();
            String rep = adaptCase(replacement, match);
            m.appendReplacement(sb, Matcher.quoteReplacement(rep));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String adaptCase(String repl, String match) {
        if (match.codePoints().allMatch(cp -> !Character.isLetter(cp) || Character.isUpperCase(cp))) {
            return repl.toUpperCase(Locale.ROOT);
        }
        int firstCp = match.codePointAt(0);
        if (Character.isUpperCase(firstCp)) {
            return capitalize(repl);
        }
        return repl.toLowerCase(Locale.ROOT);
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) return s;
        int first = s.offsetByCodePoints(0, 1);
        String firstChar = s.substring(0, first);
        String rest = s.substring(first);
        return firstChar.toUpperCase(Locale.ROOT) + rest;
    }
}