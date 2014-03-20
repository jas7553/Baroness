/*
package basic: Basics.Setup
package rules: Rules.Baroness
*/

/*
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
public privileged aspect BaronessAspect {
	
    private basic.Solitaire basicSolitaire;
    private rules.Solitaire rulesSolitaire;
    
    // SINGLETON CONSTRUCTION
    pointcut basicSolitaireConstructor(int numberOfPiles) : 
        call(basic.Solitaire.new(int)) &&
        args(numberOfPiles) &&
        !within(BaronessAspect);
    
    pointcut rulesSolitaireConstructor(int numberOfPiles) : 
        call(rules.Solitaire.new(int)) &&
        args(numberOfPiles) &&
        !within(BaronessAspect);

    basic.Solitaire around() : basicSolitaireConstructor(int) {
    	proceed();
        return this.basicSolitaire;
    }
    
    rules.Solitaire around() : rulesSolitaireConstructor(int) {
    	proceed();
        return this.rulesSolitaire;
    }

    before (int numberOfPiles) : basicSolitaireConstructor(numberOfPiles) {
        if (this.basicSolitaire == null) {
        	this.rulesSolitaire = new rules.Solitaire(numberOfPiles);
        	this.basicSolitaire = new basic.Solitaire(numberOfPiles);
        }
    }

    before(int numberOfPiles) : rulesSolitaireConstructor(numberOfPiles) {
        if (this.rulesSolitaire == null) {
        	this.basicSolitaire = new basic.Solitaire(numberOfPiles);
        	this.rulesSolitaire = new rules.Solitaire(numberOfPiles);
        }
    }
    
    // Merge CardTable table
    before(basic.CardTable newval): set(basic.CardTable basic.Solitaire.table) && args(newval) && !within(BaronessAspect) {
    	if (this.rulesSolitaire != null) {
    		this.rulesSolitaire.table = newval;
    	}
    }
    
    before(basic.CardTable newval): set(basic.CardTable rules.Solitaire.table) && args(newval) && !within(BaronessAspect) {
    	if (this.basicSolitaire != null) {
    		this.basicSolitaire.table = newval;
    	}
    }

    // Merge int numPiles
    before(int newval): set(int basic.Solitaire.numPiles) && args(newval) && !within(BaronessAspect) {
    	if (this.rulesSolitaire != null) {
    		this.rulesSolitaire.numPiles = newval;
    	}
    }
    
    before(int newval): set(int rules.Solitaire.numPiles) && args(newval) && !within(BaronessAspect) {
    	if (this.basicSolitaire != null) {
    		this.basicSolitaire.numPiles = newval;
    	}
    }
    
    // Merge CardPile deck
    before(basic.CardPile newval): set(basic.CardPile basic.Solitaire.deck) && args(newval) && !within(BaronessAspect) {
    	if (this.rulesSolitaire != null) {
    		this.rulesSolitaire.deck = newval;
    	}
    }
    
    before(basic.CardPile newval): set(basic.CardPile rules.Solitaire.deck) && args(newval) && !within(BaronessAspect) {
    	if (this.basicSolitaire != null) {
    		this.basicSolitaire.deck = newval;
    	}
    }
    
    // Merge CardPile[] drawPiles
    // (**** "rules.Solitare" HAS NO FIELD "drawPiles", NO MERGE REQUIRED ****)
    
    // Merge CardPile discard
    before(basic.CardPile newval): set(basic.CardPile basic.Solitaire.discard) && args(newval) && !within(BaronessAspect) {
    	if (this.rulesSolitaire != null) {
    		this.rulesSolitaire.discard = newval;
    	}
    }
    
    before(basic.CardPile newval): set(basic.CardPile rules.Solitaire.discard) && args(newval) && !within(BaronessAspect) {
    	if (this.basicSolitaire != null) {
    		this.basicSolitaire.discard = newval;
    	}
    }

    // Merge boolean gameOver
    before(boolean newval): set(boolean basic.Solitaire.gameOver) && args(newval) && !within(BaronessAspect) {
    	if (this.rulesSolitaire != null) {
    		this.rulesSolitaire.gameOver = newval;
    	}
    }
    
    before(boolean newval): set(boolean rules.Solitaire.discard) && args(newval) && !within(BaronessAspect) {
    	if (this.basicSolitaire != null) {
    		this.basicSolitaire.gameOver = newval;
    	}
    }

    // Merge boolean gameOver
    before(boolean newval): set(boolean basic.Solitaire.legalPick) && args(newval) && !within(BaronessAspect) {
    	if (this.rulesSolitaire != null) {
    		this.rulesSolitaire.legalPick = newval;
    	}
    }
    
    before(boolean newval): set(boolean rules.Solitaire.legalPick) && args(newval) && !within(BaronessAspect) {
    	if (this.basicSolitaire != null) {
    		this.basicSolitaire.legalPick = newval;
    	}
    }
    
    // override basic.Solitaire.pickCardAt with rules.Solitaire.pickCardAt
    void around(int i): call(void basic.Solitaire.pickCardAt(int)) && args(i) {
		this.rulesSolitaire.pickCardAt(i);
    }

    // override rules.Solitaire.allEmpty with basic.Solitaire.allEmpty
    boolean around(basic.CardTable t, int start, int count) : call(boolean rules.Solitaire.allEmpty(basic.CardTable, int, int)) && args(t, start, count) {
		return this.basicSolitaire.allEmpty(t, start, count);
    }
}
