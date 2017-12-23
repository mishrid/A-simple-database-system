package db61b;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class TableTesting {
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TableTesting.class));
    }

    @Test
    public void basics() {
        String[] contents = new String[]{"dogs", "cats"};
        Table table1 = new Table(contents);
        assertEquals(2, table1.columns());
        assertEquals(0, table1.size());
        String[] row1 = new String[]{"horse", "cat"};
        table1.add(row1);
        String[] row2 = new String[]{"animal", "pig"};
        table1.add(row2);
        assertEquals(2, table1.size());
        assertEquals("animal", table1.get(0, 0));
        table1.print();
        assertEquals(table1.getTitle(0), "dogs");
    }

    @Test
    public void multipleIdenticalRows() {
        String[] contents = new String[]{"dogs", "cats"};
        Table table1 = new Table(contents);
        String[] row1 = new String[]{"1", "2"};
        String[] row2 = new String[]{"1", "2"};
        String[] row3 = new String[]{"3", "4"};
        table1.add(row1);
        assertEquals(1, table1.size());
        table1.add(row1);
        assertEquals(1, table1.size());
        table1.add(row2);
        assertEquals(1, table1.size());
        table1.add(row3);
        assertEquals(2, table1.size());
        table1.print();
    }

    @Test
    public void addMethod2() {
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
        Table table2 = new Table(contents);
        ArrayList<Column> sampleColumns = new ArrayList<>();
        Column column1 = new Column("dog-name", table1);
        Column column2 = new Column("type", table1);
        Column column3 = new Column("color", table1);
        sampleColumns.add(column1);
        sampleColumns.add(column2);
        sampleColumns.add(column3);
        table2.add(sampleColumns, 0, 1, 2, 5);
        table1.print();
        table2.print();

    }

    @Test
    public void addMethod3() {
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
        List<String[]> rows = table1.stringRows();
        System.out.println(rows);
        Table table2 = new Table(contents);
        table2.add(table1, "dog-name");
        table2.add(table1, "type");
        table2.add(table1, "color");
        table2.print();
    }


    @Test
    public void catenate() {
        String[] contents = new String[]{"dog-name", "type", "color"};
        String[] contents2 = new String[]{"dog-name", "age", "weight"};
        String[] result = Table.catenate(contents, contents2);
        String[] actual = new String[]{"dog-name",
            "type", "color", "dog-name", "age", "weight"};

        assertArrayEquals(result, actual);

    }

    @Test
    public void select() {
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
        List<String> messages = Arrays.asList("dog-name");
        Table table3 = table1.select(messages, new ArrayList<>());
        table3.print();
        String[] contents2 = new String[]{"dog-name", "age", "weight"};
        Table table2 = new Table(contents2);
        String[] row12 = new String[]{"fernando", "10", "100"};
        String[] row22 = new String[]{"buddy", "9", "200"};
        table2.add(row12);
        table2.add(row22);
        List<String> messages2 = Arrays.asList("dog-name", "age");
        Table table4 = table1.select(table2, messages2, new ArrayList<>());
        table4.print();
    }


    @Test
    public void compareTo() {
        HashMap<String, Boolean> dictionary = new HashMap<>();
        String first = " 2004";
        String second = "2004";

        dictionary.put("<", (first.compareTo(second) < 0));
        dictionary.put(">", (first.compareTo(second) > 0));
        dictionary.put("<=", (first.compareTo(second) <= 0));
        dictionary.put(">=", (first.compareTo(second) >= 0));
        dictionary.put("=", (first.compareTo(second) == 0));
        dictionary.put("!=", (!first.contentEquals(second)));

        System.out.println(dictionary.get("<"));

    }



}
