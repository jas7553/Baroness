import java.util.Scanner;

/*
Hyper/J configuration logic-

package basic: Basics.Setup
package rules: Rules.Baroness

hyperslices:
  Basics.Setup,
  Rules.Baroness;

relationships:
  mergeByName;

  override
    action Basics.Setup.Solitaire.pickCardAt with
      action Rules.Baroness.Solitaire.pickCardAt;

  override
    action Rules.Baroness.Solitaire.allEmpty with
      action Basics.Setup.Solitaire.allEmpty;
*/
/**
 * @author Jason A Smith <jas7553>
 */
public privileged aspect MergeSolitaire {

    private basic.Solitaire basicSolitaire;
    private rules.Solitaire rulesSolitaire;

    pointcut basicSolitaireConstructor(int numberOfPiles) :
        call(basic.Solitaire.new(int)) &&
        args(numberOfPiles) &&
        !within(MergeSolitaire);

    pointcut rulesSolitaireConstructor(int numberOfPiles) :
        call(rules.Solitaire.new(int)) &&
        args(numberOfPiles) &&
        !within(MergeSolitaire);

    basic.Solitaire around() : basicSolitaireConstructor(int) {
        proceed();
        return this.basicSolitaire;
    }

    rules.Solitaire around() : rulesSolitaireConstructor(int) {
        proceed();
        return this.rulesSolitaire;
    }

    before(int numberOfPiles) : basicSolitaireConstructor(numberOfPiles) {
        if (this.basicSolitaire == null) {
            this.basicSolitaire = new basic.Solitaire(numberOfPiles);
            this.rulesSolitaire = new rules.Solitaire(numberOfPiles);
        }
    }

    before(int numberOfPiles) : rulesSolitaireConstructor(numberOfPiles) {
        if (this.rulesSolitaire == null) {
            this.rulesSolitaire = new rules.Solitaire(numberOfPiles);
            this.basicSolitaire = new basic.Solitaire(numberOfPiles);
        }
    }

    // Replace rules.Solitaire.table with basic.Solitaire.table
    basic.CardTable around(): get(basic.CardTable rules.Solitaire.table) && !within(MergeSolitaire) {
        return this.basicSolitaire.table;
    }
    void around(basic.CardTable newval): set(basic.CardTable rules.Solitaire.table) && args(newval) && !within(MergeSolitaire) {
        this.basicSolitaire.table = newval;
    }

    // Replace rules.Solitaire.numPiles with basic.Solitaire.numPiles
    int around(): get(int rules.Solitaire.numPiles) && !within(MergeSolitaire) {
        return this.basicSolitaire.numPiles;
    }
    void around(int newval): set(int rules.Solitaire.numPiles) && args(newval) && !within(MergeSolitaire) {
        this.basicSolitaire.numPiles = newval;
    }

    // Replace rules.Solitaire.deck with basic.Solitaire.deck
    basic.CardPile around(): get(basic.CardPile rules.Solitaire.deck) && !within(MergeSolitaire) {
        return this.basicSolitaire.deck;
    }
    void around(basic.CardPile newval): set(basic.CardPile rules.Solitaire.deck) && args(newval) && !within(MergeSolitaire) {
        this.basicSolitaire.deck = newval;
    }

    // Replace rules.Solitaire.discard with basic.Solitaire.discard
    basic.CardPile around(): get(basic.CardPile rules.Solitaire.discard) && !within(MergeSolitaire) {
        return this.basicSolitaire.discard;
    }
    void around(basic.CardPile newval): set(basic.CardPile rules.Solitaire.discard) && args(newval) && !within(MergeSolitaire) {
        this.basicSolitaire.discard = newval;
    }

    // Replace rules.Solitaire.gameOver with basic.Solitaire.gameOver
    boolean around(): get(boolean rules.Solitaire.gameOver) && !within(MergeSolitaire) {
        return this.basicSolitaire.gameOver;
    }
    void around(boolean newval): set(boolean rules.Solitaire.gameOver) && args(newval) && !within(MergeSolitaire) {
        this.basicSolitaire.gameOver = newval;
    }

    // Replace rules.Solitaire.legalPick with basic.Solitaire.legalPick
    boolean around(): get(boolean rules.Solitaire.legalPick) && !within(MergeSolitaire) {
        return this.basicSolitaire.legalPick;
    }
    void around(boolean newval): set(boolean rules.Solitaire.legalPick) && args(newval) && !within(MergeSolitaire) {
        this.basicSolitaire.legalPick = newval;
    }

    // override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt
    void around(int pileNum): call(void basic.Solitaire.pickCardAt(int)) && args(pileNum) {
        this.rulesSolitaire.pickCardAt(pileNum);
    }

    // override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty
    boolean around(basic.CardTable t, int start, int count): call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {
        return this.basicSolitaire.allEmpty(t, start, count);
    }

    // merge private void playOneStep( Scanner in ), first basic.Solitaire, then rules.Solitaire
    after(Scanner in): execution(void basic.Solitaire.playOneStep(..)) && args(in) && !within(MergeSolitaire) {
        this.rulesSolitaire.playOneStep(in);
    }
}
