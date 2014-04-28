import java.util.Map;
import java.util.WeakHashMap;
import java.util.Scanner;


public privileged aspect MergeSolitaire {

private boolean constructingA = false;
private boolean constructingA2 = false;

private boolean constructingB = false;
private boolean constructingB2 = false;

private final Map<basic.Solitaire, rules.Solitaire> basicTorulesMapping = new WeakHashMap<>();
private final Map<rules.Solitaire, basic.Solitaire> rulesTobasicMapping = new WeakHashMap<>();

before(): execution(basic.Solitaire.new(..)) {
    constructingA = true;
}

after(basic.Solitaire newlyCreatedObject) returning: this(newlyCreatedObject) && execution(basic.Solitaire.new(..)) {
    if (!constructingA2 && !constructingB) {
        constructingA2 = true;
        basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
        rules.Solitaire rulesSolitaire = new rules.Solitaire(basicSolitaire);
        assert basicSolitaire != null; assert rulesSolitaire != null;
        basicTorulesMapping.put(basicSolitaire, rulesSolitaire);
        constructingA2 = false;
    }
    constructingA = false;
}

before(): execution(rules.Solitaire.new(..)) {
    constructingB = true;
}

after(rules.Solitaire newlyCreatedObject) returning: this(newlyCreatedObject) && execution(rules.Solitaire.new(..)) {
    if (!constructingB2 && !constructingA) {
        constructingB2 = true;
        rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
        basic.Solitaire basicSolitaire = new basic.Solitaire(rulesSolitaire);
        assert rulesSolitaire != null; assert basicSolitaire != null;
        rulesTobasicMapping.put(rulesSolitaire, basicSolitaire);
        constructingB2 = false;
    }
    constructingB = false;
}

// Merge basic.Solitaire.table and rules.Solitaire.table
void around(basic.CardTable table): set(basic.CardTable basic.Solitaire.table) && args(table) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.table = table;
    
    if (!constructingA) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.table = table;
    }
}
void around(basic.CardTable table): set(basic.CardTable rules.Solitaire.table) && args(table) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.table = table;
    
    if (!constructingB) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.table = table;
    }
}

// Merge basic.Solitaire.numPiles and rules.Solitaire.numPiles
void around(int numPiles): set(int basic.Solitaire.numPiles) && args(numPiles) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.numPiles = numPiles;
    
    if (!constructingA) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.numPiles = numPiles;
    }
}
void around(int numPiles): set(int rules.Solitaire.numPiles) && args(numPiles) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.numPiles = numPiles;
    
    if (!constructingB) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.numPiles = numPiles;
    }
}

// Merge basic.Solitaire.deck and rules.Solitaire.deck
void around(basic.CardPile deck): set(basic.CardPile basic.Solitaire.deck) && args(deck) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.deck = deck;
    
    if (!constructingA) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.deck = deck;
    }
}
void around(basic.CardPile deck): set(basic.CardPile rules.Solitaire.deck) && args(deck) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.deck = deck;
    
    if (!constructingB) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.deck = deck;
    }
}

// Merge basic.Solitaire.discard and rules.Solitaire.discard
void around(basic.CardPile discard): set(basic.CardPile basic.Solitaire.discard) && args(discard) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.discard = discard;
    
    if (!constructingA) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.discard = discard;
    }
}
void around(basic.CardPile discard): set(basic.CardPile rules.Solitaire.discard) && args(discard) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.discard = discard;
    
    if (!constructingB) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.discard = discard;
    }
}

// Merge basic.Solitaire.gameOver and rules.Solitaire.gameOver
void around(boolean gameOver): set(boolean basic.Solitaire.gameOver) && args(gameOver) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.gameOver = gameOver;
    
    if (!constructingA) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.gameOver = gameOver;
    }
}
void around(boolean gameOver): set(boolean rules.Solitaire.gameOver) && args(gameOver) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.gameOver = gameOver;
    
    if (!constructingB) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.gameOver = gameOver;
    }
}

// Merge basic.Solitaire.legalPick and rules.Solitaire.legalPick
void around(boolean legalPick): set(boolean basic.Solitaire.legalPick) && args(legalPick) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.legalPick = legalPick;
    
    if (!constructingA) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.legalPick = legalPick;
    }
}
void around(boolean legalPick): set(boolean rules.Solitaire.legalPick) && args(legalPick) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.legalPick = legalPick;
    
    if (!constructingB) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.legalPick = legalPick;
    }
}

// override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt
void around(int pileNum): call(void basic.Solitaire.pickCardAt(int)) && args(pileNum) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
    rulesSolitaire.pickCardAt(pileNum);
}

// override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty
boolean around(basic.CardTable t, int start, int count): call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.allEmpty(t, start, count);
}

// Merge basic.Solitaire.playOneStep and rules.Solitaire.playOneStep
after(Scanner in): call(void basic.Solitaire.playOneStep(Scanner)) && args(in) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
    rulesSolitaire.playOneStep(in);
}
after(Scanner in): call(void rules.Solitaire.playOneStep(Scanner)) && args(in) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.playOneStep(in);
}

}
