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
public aspect BaronessAspect {

	private static basic.Solitaire basicSolitaire;
	private static rules.Solitaire rulesSolitaire;
	
    pointcut basicSolitaireConstructor(int numberOfPiles) : 
    	call(basic.Solitaire.new(int)) &&
    	args(numberOfPiles) &&
    	!within(BaronessAspect);
	
    pointcut rulesSolitaireConstructor(int numberOfPiles) : 
    	call(rules.Solitaire.new(int)) &&
    	args(numberOfPiles) &&
    	!within(BaronessAspect);

    before (int numberOfPiles) : basicSolitaireConstructor(numberOfPiles) {
 		if (BaronessAspect.basicSolitaire == null) {
			BaronessAspect.basicSolitaire = new basic.Solitaire(numberOfPiles);
			BaronessAspect.rulesSolitaire = new rules.Solitaire(numberOfPiles);
		}
    }

	before(int numberOfPiles) : rulesSolitaireConstructor(numberOfPiles) {
		if (BaronessAspect.rulesSolitaire == null) {
			BaronessAspect.basicSolitaire = new basic.Solitaire(numberOfPiles);
			BaronessAspect.rulesSolitaire = new rules.Solitaire(numberOfPiles);
		}
	}

	basic.Solitaire around() : basicSolitaireConstructor(int) {
		return BaronessAspect.basicSolitaire;
	}
	
	rules.Solitaire around() : rulesSolitaireConstructor(int) {
		return BaronessAspect.rulesSolitaire;
	}

}
