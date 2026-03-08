package org.space.spaceAccents;

import java.util.List;

public class Accent {
    private final String id;
    private final String display;
    private final List<Replacement> replacements;

    public Accent(String id, String display, List<Replacement> replacements) {
        this.id = id;
        this.display = display;
        this.replacements = replacements;
    }

    public String getId() { return id; }
    public String getDisplay() { return display; }

    public String apply(String input) {
        String s = input;
        for (Replacement r : replacements) {
            s = r.apply(s);
        }
        return s;
    }
}