package basic;

import java.util.Scanner;

/**
 * A foundation for simple solitaire card play. Students use
 * this foundation to build full games using various
 * techniques.
 *
 * @author James Heliotis
 */
public class Solitaire {
    
    /**
     * The playing area, containing card piles
     */
    private basic.CardTable table;
    
    /**
     * How many draw piles there are
     */
    private int numPiles;
    
    /**
     * The pile from which the draw piles get their cards
     */
    private basic.CardPile deck;
    
    /**
     * The piles from which drawn cards are taken
     */
    private basic.CardPile[] drawPiles;
    
    /**
     * The pile where drawn cards go
     */
    private basic.CardPile discard;

    public static final String DEAL_CMD;
    public static final String QUIT_CMD;

    private boolean gameOver;

    private boolean legalPick;
    
    static {
        DEAL_CMD = "deal";
        QUIT_CMD = "quit";
    }

    /**
     * Set up the playing area and all the card piles.
     * 
     * @param numberOfPiles the number of draw piles to create
     */
    public Solitaire( int numberOfPiles ) {
        __init__(numberOfPiles);
    }
    
    public void __init__(int numberOfPiles) {
        gameOver = false;
        legalPick = false;
        
        numPiles = numberOfPiles;
        table = new basic.CardTable( numberOfPiles + 4 );
        
        // Create the card piles.
        deck = new basic.CardPile( 1 );
        deck.shuffle();
        drawPiles = new basic.CardPile[ numPiles ];
        for ( int i = 0; i < numberOfPiles; ++i ) {
            drawPiles[ i ] = new basic.CardPile();
        }
        discard = new basic.CardPile();

        // Put the piles on the table.
        table.placePile( deck, 0 );
        for ( int j = 2; j < numberOfPiles + 2; ++j ) {
            table.placePile( drawPiles[ j - 2 ], j );
        }
        table.placePile( discard, numberOfPiles + 3 );
    }

    /**
     * Play the Baroness game.
     *
     * @param args a one-element array containing the string rep of
     *             the number of piles to create
     */
    public void play() {

        // Initially put a single card in each draw pile.
        deal();

        // Create input stream
        Scanner in = new Scanner( System.in );
        // Loop until playOneStep says to stop.
        System.out.println( table );
        while ( !gameOver ) {
            playOneStep( in );
            System.out.println();
            System.out.println( table );
        }

        System.out.println( "The game is over." );
        in.close();
    }

    /**
     * Have the player play one turn in the game:<br/>
     * - Pick ONE card, or<br/>
     * - Deal another set of cards onto the piles, or<br/>
     * - quit the game.
     */
    private void playOneStep( Scanner in ) {

        if ( allEmpty( table, 2, numPiles ) && deck.empty() ) {
            // Nothing left to do.
            gameOver = true;
        }
        else {
            // Ask player what s/he wants to do.
            System.out.print( "Type " + DEAL_CMD + ", " + QUIT_CMD +
                              ", or a pile number: " );
            System.out.flush();
            int pileNum = 0;
            boolean gotNumber = true;
            String response = in.next();
            try {
                pileNum = Integer.parseInt( response );
            }
            catch( NumberFormatException nfe ) {
                gotNumber = false;
            }
            // If the player types in a card pile position,
            if ( gotNumber ) {
                if ( !isTableauPile( pileNum ) ) {
                    System.out.println( "Sorry, pile #" + pileNum +
                                " is not valid. Please try again." );
                }
                else {
                    pickCardAt( pileNum );
                    if ( !legalPick ) {
                        System.out.println( "Sorry, pile #" + pileNum +
                                " is not legal. Please try again." );
                    }
                }
            }
            // Otherwise if the player types "deal",
            else if ( response.equals( "deal" ) ) {
                deal();
            }
            // Otherwise if the player types "quit",
            else if ( response.equals( "quit" ) ) {
                System.out.println( "The user aborted the game." );
                gameOver = true;
                return;
            }
            // Otherwise,
            else {
                // Tell the player to try again.
                System.out.println( "Sorry, \"" + response +
                   "\" is an invalid command. Please try again." );
            }
        }
    }

    private boolean isTableauPile( int pileNum ) {
        return pileNum >= 2 && pileNum <= numPiles + 1;
    }

    private void pickCardAt( int pileNum ) {
        basic.CardPile pile = table.getPile( pileNum );
        if ( pile.empty() ) {
            legalPick = false;
        }
        else {
            basic.Card c = pile.remove();
            c.orient( false );
            discard.add( c );
            legalPick = true;
        }
    }

    private void deal() {
        // Deal one card to each pile.
        for ( int i = 2; i < numPiles + 2; ++i ) {
            if ( !deck.empty() ) {
                basic.Card c = deck.remove();
                c.orient( true );
                table.getPile( i ).add( c );
            }
        }
    }

    /**
     * Are all the piles empty?
     *
     * @param t the CardTable containing the piles
     * @param start the location of the first pile to check
     * @param count the number of sequential piles to check
     * @return true iff all specified piles have no cards.
     */
    private boolean allEmpty( basic.CardTable t, int start, int count ) {
        boolean result = true;
        for ( int i = start; i < start + count; ++i ) {
            basic.CardPile pile = t.getPile( i );
            result = result && ( pile == null || pile.empty() );
        }
        return result;
    }

}
