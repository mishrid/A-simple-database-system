package db61b;

import java.util.HashMap;

/**
 * Represents a single 'where' condition in a 'select' command.
 *
 * @author Mishri Daga
 */
class Condition {

    /**
     * The operands of this condition.  _col2 is null if the second operand
     * is a literal.
     */
    private Column _col1, _col2;
    /**
     * Second operand, if literal (otherwise null).
     */
    private String _val2;
    /**
     * Middle operand, the relationship between col1 and the other operand.
     */
    private String _relation;

    /**
     * A Condition representing COL1 RELATION COL2, where COL1 and COL2
     * are column designators. and RELATION is one of the
     * strings "<", ">", "<=", ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _relation = relation;
        _col2 = col2;
    }

    /**
     * A Condition representing COL1 RELATION 'VAL2', where COL1 is
     * a column designator, VAL2 is a literal value (without the
     * quotes), and RELATION is one of the strings "<", ">", "<=",
     * ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /**
     * A method that returns the name of the col1.
     */
    String column() {
        return _col1.getName();
    }

    /**
     * A method that returns the String relation.
     */
    String relation() {
        return _relation;
    }

    /**
     * Returns true if the Condition compares to a val2 else, false.
     */
    boolean secondTYPE() {
        if (_col2 == null) {
            return true;
        }
        return false;
    }

    /**
     * A method that returns the name of the col2.
     */
    String secondCol() {
        return _col1.getName();
    }

    /**
     * A method that returns the name of the val2.
     */
    String secondVal() {
        return _val2;
    }

    /**
     * Assuming that ROWS are row indices in the respective tables
     * from which my columns are selected, returns the result of
     * performing the test I denote.
     */
    boolean test(Integer... rows) {
        String second;
        if (_col2 != null) {
            second = (_col2.getFrom(rows));
        } else {
            second = _val2;
        }

        HashMap<String, Boolean> dictionary = new HashMap<>();
        String first = _col1.getFrom(rows);

        dictionary.put("<", (first.compareTo(second) < 0));
        dictionary.put(">", (first.compareTo(second) > 0));
        dictionary.put("<=", (first.compareTo(second) <= 0));
        dictionary.put(">=", (first.compareTo(second) >= 0));
        dictionary.put("=", (first.compareTo(second) == 0));
        dictionary.put("!=", (!first.contentEquals(second)));

        return dictionary.get(_relation);

    }
}
