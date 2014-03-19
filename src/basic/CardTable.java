package basic;

public class CardTable {

    private CardPile piles[];
    
    /**
     * Create a table (tableau?) of locations for card piles.
     *
     * @param positions how many piles will fit on the table
     */
    public CardTable( int positions ) {
        piles = new CardPile[ positions ];
    }
    
    /**
     * Place a card pile on the table.
     * 
     * @param newPile the pile to be placed on the table
     * @param pos the position at which the pile is to be placed
     */
    public void placePile( CardPile newPile, int pos ) {
        piles[ pos ] = newPile;
    }
    
    /**
     * Remove a pile from the table.
     * The pile is left intact, not just on the table anymore.
     * <small>
     * Of course, if nothing else in the program is referring to that
     * pile, it will be collected as garbage.
     * </small>
     * 
     * @param pos coordinates of pile to be removed
     */
    public void removePile( int pos ) {
        piles[ pos ] = null;
    }
    
    /**
     * Look up the pile placed at the given coordinates on the table.
     * 
     * @param pos the position containing the desired pile
     * @return the pile at the given position, or null if no pile was
     *         placed there
     */
    public CardPile getPile( int pos ) {
        return piles[ pos ];
    }

    /**
     * Display the table.
     *
     * @return a sequence of strings representing the
     *         string representation of each card pile on
     *         the table, or "  " if there is no card pile
     *         at the position. The sequence is surrounded
     *         by a box:
     *<pre>
     *+----------------+
     *| AH 1C    3D [] |
     *+--------------- +
     *</pre>
     */
    public String toString() {
        String result;
        result = "+-";
        for ( int i = 0; i < piles.length; ++i ) {
            result += "---";
        }
        result += "-+\n| ";
        for ( int i = 0; i < piles.length; ++i ) {
            if ( piles[ i ] == null ) {
            	result += "   ";
            }
            else {
            	result += piles[ i ].toString() + " ";
            }
        }
        result += " +\n+-";
        for ( int i = 0; i < piles.length; ++i ) {
            result += "---";
        }
        result += "-+\n";
        result += "  ";
        for ( int i = 0; i < piles.length; ++i ) {
            result += ( i < 10 ) ? ( " " + i + " " ) : ( i + " " );
        }
        return result;
    }

}
