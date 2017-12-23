package db61b;

import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;
import java.util.List;

/**
 * The suite of all JUnit tests for the qirkat package.
 *
 * @author P. N. Hilfinger
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */

    @Test
    public void lexicographicalOrder() {
        String[] contents = new String[]{"dog-name", "type", "color"};
        Table table1 = new Table(contents);
        String[] row7 = new String[]{"fernando", "bulldog", "tan"};
        String[] row1 = new String[]{"buddy", "golden retriever", "tan"};
        String[] row2 = new String[]{"allan", "lab", "black"};
        String[] row3 = new String[]{"carter", "lab", "tan"};
        String[] row4 = new String[]{"allan", "lab", "white"};
        String[] row5 = new String[]{"fernando", "frenchie", "white"};
        String[] row6 = new String[]{"fernando", "frenchie", "brown"};
        table1.add(row1);
        table1.add(row2);
        table1.add(row3);
        table1.add(row4);
        table1.add(row5);
        table1.add(row6);
        table1.add(row7);
        table1.print();
    }

    @Test
    public void readTable() {
        Table tableRead = Table.readTable(
                "/Users/mishridaga/repo/proj1/testing/enrolled.db");
        tableRead.print();
        tableRead.writeTable("enrolled.mishri");
        Table table3 = Table.readTable("enrolled.mishri");
        table3.print();
    }

    @Test
    public void where() {
        Table students = Table.readTable(
                "/Users/mishridaga/repo/proj1/testing/students");
        Table enrolled = Table.readTable(
                "/Users/mishridaga/repo/proj1/testing/enrolled");
        List<String> columnnames = new ArrayList<>();
        columnnames.add("SID");
        Condition condition1 = new Condition(
                new Column("YearEnter", students), "<", "2004");
        List<Condition> conditions = new ArrayList<>();
        conditions.add(condition1);
        Table table3 = enrolled.select(students, columnnames, conditions);
        table3.print();
    }

    @Test
    public void where2() {
        Table noDuplicates = Table.readTable("no_duplicates");
        List<String> columnnames = new ArrayList<>();
        columnnames.add("SID");
        Condition condition1 = new Condition(
                new Column("YearEnter", noDuplicates),
                "<", "2004");
        List<Condition> conditions = new ArrayList<>();
        conditions.add(condition1);
        Table table3 = noDuplicates.select(columnnames, conditions);
        table3.print();
    }

    @Test
    public void debugLexicographicaly() {
        Table generic1 = Table.readTable(
                "/Users/mishridaga/repo/proj1/testing/generic1");
        Table generic3 = Table.readTable(
                "/Users/mishridaga/repo/proj1/testing/generic3");
        List<String> columnNames = new ArrayList<>();
        columnNames.add("Col1");
        columnNames.add("Col2");
        columnNames.add("Col3");
        columnNames.add("Col4");
        columnNames.add("Col5");
        Table result = generic1.select(
                generic3, columnNames, new ArrayList<>());
        Table result2 = generic3.select(
                generic1, columnNames, new ArrayList<>());
        result2.print();
        result.print();
    }

    @Test
    public void testSelect() {
        Table blank = Table.readTable(
                "/Users/mishridaga/repo/proj1/testing/blank");
        blank.add(new String[]{"this is", "one blank", "test"});
        blank.add(new String[]{"this  is", "two blanks", "test"});
        Column test = new Column("First", blank);
        Condition tester = new Condition(
                test, "=", "this is");
        List<Condition> conditions = new ArrayList<>();
        conditions.add(tester);
        List<String> columnNames = new ArrayList<>();
        columnNames.add("Second");
        Table table = blank.select(columnNames, conditions);
        table.print();
    }

    public static void main(String[] ignored) {
        textui.runClasses();
    }

}
