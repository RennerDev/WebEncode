package eu.rd9.webencode.services;

import eu.rd9.webencode.data.Rule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by renne on 18.12.2016.
 */
public class RulesService {

    private static RulesService rulesService = null;

    public static RulesService getInstance() {
        if (rulesService == null)
            rulesService = new RulesService();

        return rulesService;
    }

    public Rule getRuleForFile(String file) {
        List<Rule> rules = DatabaseService.getInstance().getRules();
        for (Rule rule : rules) {
            Pattern p = Pattern.compile(rule.Wirldcard);
            Matcher m = p.matcher(file);
            if (m.find()) {
                return rule;
            }
        }

        return null;
    }

}
