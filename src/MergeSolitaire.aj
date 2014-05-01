import java.util.Map;
import java.util.WeakHashMap;
import java.util.Scanner;


public privileged aspect MergeSolitaire {

private int constructingA = 0;
private int constructingA2 = 0;

private int constructingB = 0;
private int constructingB2 = 0;

private final Map<basic.Solitaire, rules.Solitaire> basicTorulesMapping = new WeakHashMap<>();
private final Map<rules.Solitaire, basic.Solitaire> rulesTobasicMapping = new WeakHashMap<>();

before(int numberOfPiles): execution(basic.Solitaire.new(int)) && args(numberOfPiles) {
    constructingA++;
}

after(int numberOfPiles) returning: execution(basic.Solitaire.new(int)) && args(numberOfPiles) {
    basic.Solitaire basicSolitaire = null;
    rules.Solitaire rulesSolitaire = null;
    if ((constructingA2 == 0) && (constructingB == 0)) {
        constructingA2++;
        basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
        rulesSolitaire = new rules.Solitaire(numberOfPiles);
        assert basicSolitaire != null; assert rulesSolitaire != null;
        basicTorulesMapping.put(basicSolitaire, rulesSolitaire);
        rulesTobasicMapping.put(rulesSolitaire, basicSolitaire);
        constructingA2--;
    }
    constructingA--;
    
    if (basicSolitaire != null) basicSolitaire.__init__(numberOfPiles);
    if (rulesSolitaire != null) rulesSolitaire.__init__(numberOfPiles);
}

before(int numberOfPiles): execution(rules.Solitaire.new(int)) && args(numberOfPiles) {
    constructingB++;
}

after(int numberOfPiles) returning: execution(rules.Solitaire.new(int)) && args(numberOfPiles) {
    rules.Solitaire rulesSolitaire = null;
    basic.Solitaire basicSolitaire = null;
    if ((constructingB2 == 0) && (constructingA == 0)) {
        constructingB2++;
        rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
        basicSolitaire = new basic.Solitaire(numberOfPiles);
        assert rulesSolitaire != null; assert basicSolitaire != null;
        rulesTobasicMapping.put(rulesSolitaire, basicSolitaire);
        basicTorulesMapping.put(basicSolitaire, rulesSolitaire);
        constructingB2--;
    }
    constructingB--;
    
    if (rulesSolitaire != null) rulesSolitaire.__init__(numberOfPiles);
    if (basicSolitaire != null) basicSolitaire.__init__(numberOfPiles);
}

// Merge basic.Solitaire.table and rules.Solitaire.table
void around(basic.CardTable table): set(basic.CardTable basic.Solitaire.table) && args(table) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.table = table;
    
    if (constructingA == 0) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.table = table;
    }
}
void around(basic.CardTable table): set(basic.CardTable rules.Solitaire.table) && args(table) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.table = table;
    
    if (constructingB == 0) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.table = table;
    }
}

// Merge basic.Solitaire.numPiles and rules.Solitaire.numPiles
void around(int numPiles): set(int basic.Solitaire.numPiles) && args(numPiles) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.numPiles = numPiles;
    
    if (constructingA == 0) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.numPiles = numPiles;
    }
}
void around(int numPiles): set(int rules.Solitaire.numPiles) && args(numPiles) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.numPiles = numPiles;
    
    if (constructingB == 0) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.numPiles = numPiles;
    }
}

// Merge basic.Solitaire.deck and rules.Solitaire.deck
void around(basic.CardPile deck): set(basic.CardPile basic.Solitaire.deck) && args(deck) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.deck = deck;
    
    if (constructingA == 0) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.deck = deck;
    }
}
void around(basic.CardPile deck): set(basic.CardPile rules.Solitaire.deck) && args(deck) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.deck = deck;
    
    if (constructingB == 0) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.deck = deck;
    }
}

// Merge basic.Solitaire.discard and rules.Solitaire.discard
void around(basic.CardPile discard): set(basic.CardPile basic.Solitaire.discard) && args(discard) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.discard = discard;
    
    if (constructingA == 0) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.discard = discard;
    }
}
void around(basic.CardPile discard): set(basic.CardPile rules.Solitaire.discard) && args(discard) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.discard = discard;
    
    if (constructingB == 0) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.discard = discard;
    }
}

// Merge basic.Solitaire.gameOver and rules.Solitaire.gameOver
void around(boolean gameOver): set(boolean basic.Solitaire.gameOver) && args(gameOver) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.gameOver = gameOver;
    
    if (constructingA == 0) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.gameOver = gameOver;
    }
}
void around(boolean gameOver): set(boolean rules.Solitaire.gameOver) && args(gameOver) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.gameOver = gameOver;
    
    if (constructingB == 0) {
        assert rulesTobasicMapping.containsKey(rulesSolitaire);
        basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
        basicSolitaire.gameOver = gameOver;
    }
}

// Merge basic.Solitaire.legalPick and rules.Solitaire.legalPick
void around(boolean legalPick): set(boolean basic.Solitaire.legalPick) && args(legalPick) && !within(MergeSolitaire) {
    basic.Solitaire basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
    basicSolitaire.legalPick = legalPick;
    
    if (constructingA == 0) {
        assert basicTorulesMapping.containsKey(basicSolitaire);
        rules.Solitaire rulesSolitaire = basicTorulesMapping.get(basicSolitaire);
        rulesSolitaire.legalPick = legalPick;
    }
}
void around(boolean legalPick): set(boolean rules.Solitaire.legalPick) && args(legalPick) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    rulesSolitaire.legalPick = legalPick;
    
    if (constructingB == 0) {
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
