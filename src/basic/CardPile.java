package basic;

import java.util.ArrayList;
import java.util.Collections;

public class CardPile {

    private ArrayList< Card > cards;
    
    /**
     * Create an empty deck of cards
     */
    public CardPile() {
        cards = new ArrayList< Card >( 100 );
    }
    
    /**
     * Create a multiple of complete 52-card decks.
     *
     * @param n number of decks
     */
    public CardPile( int n ) {
        this();
        for ( int i = 0; i < n; ++i ) {
            for ( int rank = Card.ACE; rank <= Card.KING; ++rank ) {
                for ( int suit = Card.CLUBS; suit <= Card.SPADES; ++suit ) {
                    add( new Card( rank, suit ) );
                }
            }
        }
    }
    
    /**
     * Insert a new card into the pile. The card that was at
     * that position will now be above the new card.
     *
     * @param c card to be inserted
     * @param pos position at which to insert the card
     * @pre pos is in [0,size()]
     */
    public void add( Card c, int pos ) {
        cards.add( pos, c );
    }
    
    /**
     * Retrieve the card at the given position from the pile.
     * 
     * @param pos position of card to be removed
     * 
     * @return the removed card
     * @pre pos is in [0,size())
     */
    public Card remove( int pos ) {
        Card result = cards.remove( pos );
        return result;
    }
    
    /**
     * Insert a new card at the top of the pile.
     *
     * @param c card to be inserted
     */
    public void add( Card c ) {
        add( c, size() );
    }
    
    /**
     * Retrieve the card from the top of the pile.
     * 
     * @return the removed card
     */
    public Card remove() {
        return remove( size() - 1 );
    }
    
    /**
     * Look at the value of the card on the top of
     * the pile, regardless of its orientation.
     * 
     * @return the top card value
     */
    public Card peek() {
        return cards.get( size() - 1 );
    }
    
    /**
     * Randomly rearrange the cards in this pile.
     */
    public void shuffle() {
    	Collections.shuffle( cards );
    }

    /**
     * How many cards?
     * 
     * @return the number of cards in the pile
     */
    public int size() {
        return cards.size();
    }

    /**
     * Is the pile empty?
     *
     * @return true iff the pile has no cards
     */
    public boolean empty() {
        boolean result = size() == 0;
        return result;
    }

    /**
     * Display the pile.
     *
     * @return a string that displays the top card in
     * the pile, or "[]" if empty.
     */
    public String toString() {
        String result;
        result = empty() ? "[]" : cards.get( size()-1 ).toString();
        return result;
    }

}
