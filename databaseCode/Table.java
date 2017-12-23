package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.PrintStream;

import static db61b.Utils.error;

/**
 * A single table in a database.
 *
 * @author P. N. Hilfinger
 */
class Table {
    /**
     * My column titles.
     */
    private final String[] _titles;
    /**
     * My columns. Row i consists of _columns[k].get(i) for all k.
     */
    private final ValueList[] _columns;
    /**
     * Rows in the database are supposed to be sorted. To do so, we
     * have a list whose kth element is the index in each column
     * of the value of that column for the kth row in lexicographic order.
     * That is, the first row (smallest in lexicographic order)
     * is at position _index.get(0) in _columns[0], _columns[1], ...
     * and the kth row in lexicographic order in at position _index.get(k).
     * When a new row is inserted, insert its index at the appropriate
     * place in this list.
     * (Alternatively, we could simply keep each column in the proper order
     * so that we would not need _index.  But that would mean that inserting
     * a new row would require rearranging _rowSize lists (each list in
     * _columns) rather than just one.
     */
    private final ArrayList<Integer> _index = new ArrayList<>();
    /**
     * My number of columns (redundant, but convenient).
     */
    private final int _rowSize;
    /**
     * My number of rows (redundant, but convenient).
     */
    private int _size;

    /**
     * A new Table whose columns are given by COLUMNTITLES, which may
     * not contain duplicate names.
     */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                            columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;
        _columns = new ValueList[_titles.length];

        for (int i = 0; i < _columns.length; i++) {
            _columns[i] = new ValueList();
        }
    }

    /**
     * A new Table whose columns are give by COLUMNTITLES.
     */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /**
     * Read the contents of the file NAME.db, and return as a Table.
     * Format errors in the .db file cause a DBException.
     */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;

        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String rowList = input.readLine();
            while (rowList != null) {
                String[] row = rowList.split(",");
                table.add(row);
                rowList = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /**
     * Returns String[] A joined with String[] B  merged into one String[].
     */

    static String[] catenate(String[] A, String[] B) {
        String[] result = new String[A.length + B.length];
        for (int a = 0; a < A.length; a += 1) {
            result[a] = A[a];
        }
        for (int b = 0; b < B.length; b += 1) {
            result[b + A.length] = B[b];
        }
        return result;
    }

    /**
     * Return true if the columns COMMON1 from ROW1 and COMMON2 from
     * ROW2 all have identical values.  Assumes that COMMON1 and
     * COMMON2 have the same number of elements and the same names,
     * that the columns in COMMON1 apply to this table, those in
     * COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     * into those tables. If COMMON1 and COMMON2 are empty equijoin will return
     * true in order to help create the complete dot product in SELECT methods
     */

    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        if (common1.isEmpty() && common2.isEmpty()) {
            return true;
        }

        for (int i = 0; i < common1.size(); i++) {
            String common1Value = common1.get(i).getFrom(row1);
            String common2Value = common2.get(i).getFrom(row2);
            if (!common1Value.contentEquals(common2Value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the number of columns in this table.
     */
    public int columns() {
        return _titles.length;
    }

    /**
     * Return the title of the Kth column.  Requires 0 <= K < columns().
     */
    public String getTitle(int k) {
        if (columns() < k || k < 0) {
            throw error("column number: %s does not exist", k);
        }
        return _titles[k];
    }

    /**
     * Return the number of the column whose title is TITLE, or -1 if
     * there isn't one.
     */
    public int findColumn(String title) {
        int index = 0;
        for (String x : _titles) {
            if (x.equals(title)) {
                return index;
            } else {
                index = index + 1;
            }
        }
        return -1;
    }

    /**
     * Return the number of rows in this table.
     */
    public int size() {
        return _columns[0].size();
    }

    /**
     * Return the value of column number COL (0 <= COL < columns())
     * of record number ROW (0 <= ROW < size()).
     */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException e) {
            throw error("invalid row or column");
        }
    }

    /**
     * Return a List containing ValueLists of each row.
     */

    public ValueList[] rows() {
        ValueList[] allRows = new ValueList[size()];
        for (int r = 0; r < size(); r++) {
            ValueList row = new ValueList();
            for (int c = 0; c < columns(); c++) {
                row.add(get(r, c));
            }
            allRows[r] = row;
        }
        return allRows;
    }

    /**
     * Return a List containing String[] of each row.
     */

    public List<String[]> stringRows() {
        List<String[]> listOfRows;
        listOfRows = new ArrayList<>(size());
        for (int r = 0; r < size(); r++) {
            String[] row = new String[columns()];
            int index = 0;
            for (int c = 0; c < columns(); c++) {
                row[index] = get(r, c);
                index = index + 1;
            }
            listOfRows.add(row);
        }
        return listOfRows;
    }

    /**
     * Returns a String[] which is the Row at the given INDEX.
     */

    public String[] getRow(int index) {
        List<String[]> listOfRows = stringRows();
        return listOfRows.get(index);
    }

    /**
     * Add a new row whose column values are VALUES to me if no equal
     * row already exists.  Return true if anything was added,
     * false otherwise.
     */

    public boolean add(String[] values) {

        if (values.length != _titles.length) {
            throw error("Add requires a string of equal "
                    + "length to the number of columns in the table");
        }

        for (int r = 0; r < size(); r++) {
            String[] row = new String[columns()];
            for (int c = 0; c < columns(); c++) {
                row[c] = get(r, c);
            }
            if (Arrays.equals(row, values)) {
                return false;
            }
        }

        int insertionRow = rowInsertion(values);

        _index.add(insertionRow, size());

        for (int c = 0; c < columns(); c++) {
            _columns[c].add(insertionRow, values[c]);

        }


        return true;
    }

    /** Takes in a row ROWORIGINAL and returns the index at
     * which it should be inserted in a given table. */

    public int rowInsertion(String[] rowOriginal) {
        List<String[]> listOfRows = this.stringRows();
        String insert = catenate(rowOriginal);
        int insertionPoint = 0;
        for (String[] row : listOfRows) {
            String comparison = catenate(row);
            if (insert.compareTo(comparison) > 0) {
                insertionPoint = insertionPoint + 1;
            }

        }
        return insertionPoint;
    }
    /** Takes in a ROW and returns the row as one string. */

    public String catenate(String[] row) {
        String result = new String();
        for (String item : row) {
            result = result + item;
        }
        return result;
    }

    /**
     * Add a new row whose column values are extracted by COLUMNS from
     * the rows indexed by ROWS, if no equal row already exists.
     * Return true if anything was added, false otherwise. See
     * Column.getFrom(Integer...) for a description of how Columns
     * extract values.
     */

    public boolean add(List<Column> columns, Integer... rows) {

        for (int r : rows) {
            String[] insertion = new String[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                insertion[i] = columns.get(i).getFrom(r);
            }
            add(insertion);

        }
        if (rows.length > 0) {
            return true;
        }

        return false;
    }

    /**
     * Add any number of columns by their NAMES from
     * its ORIGIN table to a new table.
     */

    public void add(Table origin, String... names) {
        for (String name : names) {
            this._columns[this.findColumn(name)] = getColumn(name, origin);
        }
    }

    /**
     * Returns an entire Column in the form of a ValueList
     * with the given NAME from the given origin TABLE.
     */

    public ValueList getColumn(String name, Table table) {
        int index = table.findColumn(name);
        return table._columns[index];

    }

    /**
     * Write the contents of TABLE into the file NAME.db. Any I/O errors
     * cause a DBException.
     */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            output = new PrintStream(name + ".db");

            for (int k = 0; k < _titles.length; k++) {
                if (k != _titles.length - 1) {
                    output.print(getTitle(k) + ",");
                } else {
                    output.print(getTitle(k) + "\r\n");
                }
            }

            ValueList[] allRows = rows();

            for (ValueList row : allRows) {
                output.print(row.toString().replace
                        ("[", "").replace("]", "") + "\r\n");
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * Print my contents on the standard output, separated by spaces
     * and indented by two spaces.
     */

    void print() {
        if (this._columns[0].size() == 0) {
            System.out.println();
        } else {

            if (_index.isEmpty()) {
                Table newTable = new Table(_titles);
                for (String[] row : stringRows()) {
                    newTable.add(row);
                }
                newTable.print();
                return;
            }

            List<String> listOfRows = new ArrayList<>(size());

            for (int r = 0; r < size(); r++) {
                String row = new String();
                for (int c = 0; c < columns(); c++) {
                    row = row + " " + (get(r, c));
                }
                listOfRows.add(row);
            }

            for (String X : listOfRows) {
                System.out.println(" " + X);
            }

        }
    }

    /**
     * Return a new Table whose columns are COLUMNNAMES, selected from
     * rows of this table that satisfy CONDITIONS.
     */
    Table select(List<String> columnNames, List<Condition> conditions) {

        List<String> titles = new ArrayList<>();
        for (String t : _titles) {
            titles.add(t);
        }

        for (String column : columnNames) {
            if (!titles.contains(column)) {
                throw error("the specified column does not exist");
            }
        }


        Table result = new Table(columnNames);
        Iterator<String> iterator = columnNames.iterator();
        while (iterator.hasNext()) {
            result.add(this, iterator.next());
        }

        if (conditions.isEmpty()) {
            return result;
        }

        List<String[]> rows = result.stringRows();

        Table nextStep = new Table(columnNames);
        for (int r = 0; r < result.size(); r++) {
            boolean flag = true;
            for (Condition condition : conditions) {
                flag = flag && condition.test(r);
            }
            if (flag) {
                nextStep.add(rows.get(r));
            }
        }

        return nextStep;

    }

    /** returns commonTitles between this and TABLE2.*/
    public List<String> commonTitles(Table table2) {
        List<String> commonTitles = new ArrayList<>();
        for (String title : _titles) {
            for (String title2 : table2._titles) {
                if (title.contentEquals(title2)) {
                    commonTitles.add(title);
                }
            }
        }
        return commonTitles;
    }

    /** returns uniqueTitles between this and TABLE2.*/
    public List<String> uniqueTitles(Table table2) {
        List<String> uniqueTitles = new ArrayList<>();
        for (String title : _titles) {
            for (String title2 : table2._titles) {
                if (!uniqueTitles.contains(title)) {
                    uniqueTitles.add(title);
                }
                if (!uniqueTitles.contains(title2)) {
                    uniqueTitles.add(title2);
                }
            }
        }
        return uniqueTitles;
    }


    /**
     * Return a new Table whose columns are COLUMNNAMES, selected
     * from pairs of rows from this table and from TABLE2 that match
     * on all columns with identical names and satisfy CONDITIONS.
     */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        List<String> commonTitles = commonTitles(table2);
        List<String> uniqueTitles = uniqueTitles(table2);

        String[] table2Titles = new String[table2._titles.length];
        int index = 0;
        for (String title : table2._titles) {
            if (commonTitles.contains(title)) {
                table2Titles[index] = (title + "2");
                index = index + 1;
            } else {
                table2Titles[index] = title;
                index = index + 1;
            }
        }
        Table all = new Table(catenate(_titles, table2Titles));
        List<Column> t1Common = new ArrayList<>();
        List<Column> t2Common = new ArrayList<>();
        for (String title : commonTitles) {
            t1Common.add(new Column(title, this));
            t2Common.add(new Column(title, table2));
        }
        for (int r = 0; r < size(); r++) {
            for (int r2 = 0; r2 < table2.size(); r2++) {
                if (equijoin(t1Common, t2Common, r, r2)) {
                    String[] row = catenate(getRow(r), table2.getRow(r2));
                    all.add(row);
                }
            }
        }
        Table noDuplicates = new Table(uniqueTitles);
        for (String title : uniqueTitles) {
            noDuplicates.add(all, title);
        }
        List<Condition> conditionsNew = new ArrayList<>();
        for (Condition c : conditions) {
            if (c.secondTYPE()) {
                Condition new1 = new Condition(
                        new Column(c.column(), noDuplicates),
                        c.relation(), c.secondVal());
                conditionsNew.add(new1);
            } else {
                Condition new1 = new Condition(
                        new Column(c.column(), noDuplicates), c.relation(),
                        new Column(c.secondCol(), noDuplicates));
                conditionsNew.add(new1);
            }
        }
        Table result = noDuplicates.select(columnNames, conditionsNew);
        return result;
    }

    /**
     * A class that is essentially ArrayList<String>.  For technical reasons,
     * we need to encapsulate ArrayList<String> like this because the
     * underlying design of Java does not properly distinguish between
     * different kinds of ArrayList at runtime (e.g., if you have a
     * variable of type Object that was created from an ArrayList, there is
     * no way to determine in general whether it is an ArrayList<String>,
     * ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     * compiler warnings.  The trick of defining a new type avoids this
     * issue.
     */
    private static class ValueList extends ArrayList<String> {
    }
}
