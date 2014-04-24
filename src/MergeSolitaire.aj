import java.util.Map;
import java.util.WeakHashMap;
import java.util.Scanner;


public privileged aspect MergeSolitaire {

private basic.Solitaire basicSolitaire;
private rules.Solitaire rulesSolitaire;

private Map<basic.Solitaire, rules.Solitaire> basicTorulesMapping = new WeakHashMap<>();
private Map<rules.Solitaire, basic.Solitaire> rulesTobasicMapping = new WeakHashMap<>();

pointcut basicSolitaireConstructor(int numberOfPiles) :
    call(basic.Solitaire.new(int)) &&
    args(numberOfPiles) &&
    !within(MergeSolitaire);

pointcut rulesSolitaireConstructor(int numberOfPiles) :
    call(rules.Solitaire.new(int)) &&
    args(numberOfPiles) &&
    !within(MergeSolitaire);

before(int numberOfPiles) : basicSolitaireConstructor(numberOfPiles) {
    basicSolitaire = (basic.Solitaire) thisJoinPoint.getTarget();
}
after(int numberOfPiles) : basicSolitaireConstructor(numberOfPiles) {
    rulesTobasicMapping.put(rulesSolitaire, new basic.Solitaire(numberOfPiles));
}

before(int numberOfPiles) : rulesSolitaireConstructor(numberOfPiles) {
    rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
}
after(int numberOfPiles) : rulesSolitaireConstructor(numberOfPiles) {
    basicTorulesMapping.put(basicSolitaire, new rules.Solitaire(numberOfPiles));
}

// Replace rules.Solitaire.table with basic.Solitaire.table
basic.CardTable around(): get(basic.CardTable rules.Solitaire.table) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.table;
}
void around(basic.CardTable newval): set(basic.CardTable rules.Solitaire.table) && args(newval) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.table = newval;
}

// Replace rules.Solitaire.numPiles with basic.Solitaire.numPiles
int around(): get(int rules.Solitaire.numPiles) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.numPiles;
}
void around(int newval): set(int rules.Solitaire.numPiles) && args(newval) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.numPiles = newval;
}

// Replace rules.Solitaire.deck with basic.Solitaire.deck
basic.CardPile around(): get(basic.CardPile rules.Solitaire.deck) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.deck;
}
void around(basic.CardPile newval): set(basic.CardPile rules.Solitaire.deck) && args(newval) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.deck = newval;
}

// Replace rules.Solitaire.discard with basic.Solitaire.discard
basic.CardPile around(): get(basic.CardPile rules.Solitaire.discard) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.discard;
}
void around(basic.CardPile newval): set(basic.CardPile rules.Solitaire.discard) && args(newval) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.discard = newval;
}

// Replace rules.Solitaire.gameOver with basic.Solitaire.gameOver
boolean around(): get(boolean rules.Solitaire.gameOver) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.gameOver;
}
void around(boolean newval): set(boolean rules.Solitaire.gameOver) && args(newval) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.gameOver = newval;
}

// Replace rules.Solitaire.legalPick with basic.Solitaire.legalPick
boolean around(): get(boolean rules.Solitaire.legalPick) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    return basicSolitaire.legalPick;
}
void around(boolean newval): set(boolean rules.Solitaire.legalPick) && args(newval) && !within(MergeSolitaire) {
    rules.Solitaire rulesSolitaire = (rules.Solitaire) thisJoinPoint.getTarget();
    basic.Solitaire basicSolitaire = rulesTobasicMapping.get(rulesSolitaire);
    basicSolitaire.legalPick = newval;
}

// override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt
void around(int pileNum): call(void basic.Solitaire.pickCardAt(int)) && args(pileNum) {
    this.rulesSolitaire.pickCardAt(pileNum);
}

// override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty
boolean around(basic.CardTable t, int start, int count): call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {
    return this.basicSolitaire.allEmpty(t, start, count);
}

after(Scanner in): execution(void basic.Solitaire.playOneStep(..)) && args(in) && !within(MergeSolitaire) {
    this.rulesSolitaire.playOneStep(in);
}

}
