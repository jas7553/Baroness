package rules;

import java.util.Scanner;

/**
 * A foundation for simple solitaire card play. Students use
 * this foundation to build full games using various
 * techniques.
 *
 * @author James Heliotis
 * @author Jason A Smith <jas7553>
 */
public class Solitaire {

    private static final int TARGET_SUM = 14;

    // From basic.Solitaire
    private basic.CardTable table;
    private basic.CardPile discard;
    @SuppressWarnings("unused")
    private boolean legalPick;
    @SuppressWarnings("unused")
    private boolean gameOver;
    private basic.CardPile deck;
    private int numPiles;

    // For rules.Solitaire
    private boolean flippedOne = false;
    private int firstPileNum;

    public Solitaire( int numberOfPiles ) {
    }
    
    public void __init__( int numberOfPiles ) {
        legalPick = false;
        gameOver = false;
    }

    @SuppressWarnings("unused")
    private void pickCardAt( int pileNum ) {
        basic.CardPile pile = table.getPile( pileNum );
        if ( pile.empty() ) {
            // cannot pick an empty pile
            legalPick = false;
        }
        else if ( !flippedOne ) {
            // the first pile is picked, wait for the next
            flippedOne = true;
            firstPileNum = pileNum;
            System.out.println( "First pick: " + pile.peek() + ". Pick another." );
            legalPick = true;
        }
        else {
            // two piles are picked, make sure they're valid
            flippedOne = false;
            if ( pileNum == firstPileNum ) {
                // the piles picked must be distinct
                System.out.println( "You cannot pick the same pile twice." );
                legalPick = false;
            }
            else {
                basic.Card c1 = table.getPile( firstPileNum ).peek();
                basic.Card c2 = pile.peek();
                if ( c1.getRank() + c2.getRank() == TARGET_SUM ) {
                    // the user picked valid piles, so discard the top cards
                    table.getPile( firstPileNum ).remove();
                    pile.remove();
                    c1.orient( false );
                    c2.orient( false );
                    discard.add( c1 );
                    discard.add( c2 );
                    legalPick = true;
                }
                else {
                    // although distinct, the piles' sum is not the target
                    System.out.println( "Cards must sum to " + TARGET_SUM + "." );
                    legalPick = false;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void playOneStep( Scanner in ) {
        if ( deck.empty() && allEmpty( table, 2, numPiles ) ) {
            System.out.println("You've won!");
            gameOver = true;
        }
        else if ( noMoreMovesLeft() ) {
            System.out.println("You have no more options.");
            gameOver = true;
        }
    }

    private boolean noMoreMovesLeft() {
        if ( noCombinationMakesTargetSum() && deck.empty() ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean iAmALousyNoGoodCheater = true;

    private boolean noCombinationMakesTargetSum() {
        for ( int i = 2; i < 2 + numPiles; i++ ) {
            for ( int j = 2; j < 2 + numPiles; j++ ) {
                if ( i == j ) {
                    continue;
                }

                basic.CardPile pile1 = table.getPile( i );
                basic.CardPile pile2 = table.getPile( j );
                if ( pile1.empty() || pile2.empty() ) {
                    continue;
                }

                if ( pile1.peek().getRank() + pile2.peek().getRank() == 14 ) {
                    if ( iAmALousyNoGoodCheater ) {
                        System.out.println( "Hint: Pile #" + i + " and Pile #" + j );
                    }
                    return false;
                }
            }
        }

        return true;
    }
    
    private boolean allEmpty( basic.CardTable t, int start, int count ) {
        throw new UnsupportedOperationException();
    }

}
