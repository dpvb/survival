package dev.dpvb.survive.game;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.util.messages.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleManager {
    private static final RuleManager instance = new RuleManager();
    final List<Rule> rules = new ArrayList<>();

    private RuleManager() {}

    public void load() {
        if (rules.size() > 0) rules.clear();
        int naturalIndex = 1;
        for (String ruleText : Survive.Configuration.getRulesList()) {
            rules.add(new Rule(naturalIndex, ruleText));
        }
    }

    public List<Rule> getRules() {
        return rules;
    }

    public static RuleManager getInstance() {
        return instance;
    }
}
