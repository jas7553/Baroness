package basic;

/**
 * Play a Baroness-style solitaire game.
 * 
 * @author James Heliotis
 */
public class Baroness {

    /**
     * The actual artifacts for playing a solitaire game
     */
    private static Solitaire game;

    /**
     * Using the command line argument to dictate the number of
     * piles, set up a Baroness-style play area and let the
     * user specify Baroness-style actions.
     * 
     * @param args the number of draw piles to create
     */
    public static void main( String[] args ) {
        if ( args.length != 1 ) {
            System.err.println( "Usage: Baroness #piles" );
            System.exit( 1 );
        }
        int numPiles = Integer.parseInt( args[ 0 ] );
        game = new Solitaire( numPiles );
        game.play();
    }

}
