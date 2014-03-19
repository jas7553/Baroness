package basic;

public class SolitaireFactory {

	public Solitaire makeSolitaire(int numberOfPiles) {
		Solitaire solitaire = new Solitaire(numberOfPiles);
		return solitaire;
	}

}
