package basic;

public class Card {

    public final static int ACE = 1;
    public final static int JACK = 11;
    public final static int QUEEN = 12;
    public final static int KING = 13;
    private final char[] rankName = { 'A', '2', '3', '4', '5', '6', '7',
                                      '8', '9', 'X', 'J', 'Q', 'K' };

    public final static int CLUBS = 1;
    public final static int DIAMONDS = 2;
    public final static int HEARTS = 3;
    public final static int SPADES = 4;
    private final char[] suitName = { 'C', 'D', 'H', 'S' };

    private int rank;
    private int suit;
    private boolean faceUp;
    
    public Card( int rank, int suit ) {
        this( rank, suit, false );
    }
    
    public Card( int rank, int suit, boolean faceUp ) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = faceUp;
    }
    
    public void orient( boolean faceUp ) {
        this.faceUp = faceUp;
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public String toString() {
        String result;
        if ( faceUp ) {
            result = "" + rankName[ rank - 1 ] + suitName[ suit - 1 ];
        }
        else {
            result = "??";
        }
        return result;
    }

}
