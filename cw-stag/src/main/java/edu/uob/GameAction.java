package edu.uob;

import java.util.HashSet;
import java.util.Set;

public class GameAction {
    private Set<String> triggers;
    private Set<String> subjects;
    private Set<String> consumed;
    private Set<String> produced;
    private String narration;
    public GameAction() {
        triggers = new HashSet<>();
        subjects = new HashSet<>();
        consumed = new HashSet<>();
        produced = new HashSet<>();
    }

    public void addTrigger(String trigger) {
        triggers.add(trigger);
    }

    public void addSubject(String subject) {
        subjects.add(subject);
    }

    public void addConsumed(String entity) {
        consumed.add(entity);
    }

    public void addProduced(String entity) {
        produced.add(entity);
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Set<String> getTriggers() {
        return triggers;
    }

    public Set<String> getSubjects() {
        return subjects;
    }

    public Set<String> getConsumed() {
        return consumed;
    }

    public Set<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("triggers: ");
        for (String s:triggers) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nsubjects: ");
        for (String s:subjects) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nconsumed: ");
        for (String s:consumed) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nproduced: ");
        for (String s:produced) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nnarration: ").append(narration);
        return str.toString();
    }
}
