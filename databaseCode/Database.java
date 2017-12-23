package db61b;

import java.util.HashMap;

/**
 * A collection of Tables, indexed by name.
 *
 * @author Mishri Daga
 */
class Database {
    /**
     * A Hashmap that contains all the tables in the database
     * and their associated names.
     */
    private HashMap<String, Table> _tableDatabase;

    /**
     * An empty database.
     */
    public Database() {
        _tableDatabase = new HashMap<>();
    }

    /**
     * Return the Table whose name is NAME stored in this database, or null
     * if there is no such table.
     */
    public Table get(String name) {
        if (_tableDatabase.containsKey(name)) {
            return _tableDatabase.get(name);
        }
        return null;
    }

    /**
     * Set or replace the table named NAME in THIS to TABLE.  TABLE and
     * NAME must not be null, and NAME must be a valid name for a table.
     */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        _tableDatabase.put(name, table);
    }
}

