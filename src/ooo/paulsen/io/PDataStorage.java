package ooo.paulsen.io;

import java.util.ArrayList;

public class PDataStorage {

    public enum DataType {
        STRING, LONG, INTEGER, FLOAT, BOOLEAN
    }

    public class DataVariable {

        private String name;
        private Object obj;
        private DataType dt;

        public DataVariable(String name, String d) {
            this.name = name;
            obj = d;
            dt = DataType.STRING;
        }

        public DataVariable(String name, long d) {
            this.name = name;
            obj = d;
            dt = DataType.LONG;
        }

        public DataVariable(String name, int d) {
            this.name = name;
            obj = d;
            dt = DataType.INTEGER;
        }

        public DataVariable(String name, float d) {
            this.name = name;
            obj = d;
            dt = DataType.FLOAT;
        }

        public DataVariable(String name, boolean d) {
            this.name = name;
            obj = d;
            dt = DataType.BOOLEAN;
        }

        public String getName() {
            return name;
        }

        public DataType getDataType() {
            return dt;
        }

        @Override
        public String toString() {
            String s = "";
            switch (dt) {
                case BOOLEAN:
                    s += "b";
                    break;
                case FLOAT:
                    s += "f";
                    break;
                case INTEGER:
                    s += "i";
                    break;
                case LONG:
                    s += "l";
                    break;
                case STRING:
                    s += "s";
                    break;
                default:
                    System.err.println("[DataStorage]::UNKNOWN datatype!");
                    break;
            }

            s += "[" + (name.replace("[", "^<<^").replace("]", "^>>^")) + "["
                    + (String.valueOf(obj).replace("[", "^<<^").replace("]", "^>>^")) + "]]";
            return s;
        }

    }

    private ArrayList<DataVariable> variables = new ArrayList<>();

//    public static void main(String[] args) {
//
//        PDataStorage ds = new PDataStorage();
//
//        ds.add("string", "asdöfljk");
//        ds.add("int", 0);
//        ds.add("long", 0L);
//        ds.add("float", 0.0f);
//        ds.add("bool", false);
//        ds.save("testfile.paulsen");
//
//        ds = new PDataStorage();
//        ds.read("testfile.paulsen");
//        System.out.println(ds.getString("string"));
//        System.out.println(ds.getInteger("int"));
//        System.out.println(ds.getLong("long"));
//        System.out.println(ds.getFloat("float"));
//        System.out.println(ds.getBoolean("bool"));
//    }

    public void save(String path) {
        PFile file = new PFile(path);
        String s = "";

        for (DataVariable v : variables)
            s += v;

        file.writeFile(s);
    }

    /**
     * Reads dataStorage-file and stores all Data in this object.
     * <br>
     * Ignores Variables with no valid value
     *
     * @param path
     * @throws IllegalArgumentException when file does not meet the expectations of this format
     */
    public void read(String path) throws IllegalArgumentException {
        if (PFolder.isFolder(path))
            return;

        String file = new PFile(path).getFileAsString();

        boolean hasBeenDataType = false, hasBeenVariableName = false;

        DataType currentDataType = DataType.STRING;
        String variableName = "";
        String data = "";

        for (int i = 0; i < file.length(); i++) {
            char c = file.charAt(i);

            if (!hasBeenDataType) {
                switch (c) {
                    case 's':
                        currentDataType = DataType.STRING;
                        break;
                    case 'l':
                        currentDataType = DataType.LONG;
                        break;
                    case 'i':
                        currentDataType = DataType.INTEGER;
                        break;
                    case 'f':
                        currentDataType = DataType.FLOAT;
                        break;
                    case 'b':
                        currentDataType = DataType.BOOLEAN;
                        break;
                    default:
                        throw new IllegalArgumentException("could not identify datatype '\" + c + \"' at pos " + i);
                }
                hasBeenDataType = true;
                i++; // jump over [
            } else if (!hasBeenVariableName) {
                if (c != '[') {
                    variableName += c;
                } else {
                    hasBeenVariableName = true;
                }
            } else { // data
                if (c != ']') {
                    data += c;
                } else {
                    try {
                        addData(currentDataType, variableName.replace("^<<^", "[").replace("^>>^", "]"),
                                data.replace("^<<^", "[").replace("^>>^", "]"));
                    } catch (IllegalArgumentException e) {
                    } catch (Exception e) {
                        throw e;
                    }
                    variableName = "";
                    data = "";
                    hasBeenDataType = false;
                    hasBeenVariableName = false;
                    i++; // jump over second ]
                }
            }
        }
    }

    public boolean addData(DataType type, String name, String data) throws IllegalArgumentException {
        try {
            switch (type) {
                case BOOLEAN:
                    if (data != null) {
                        if (data.toLowerCase().equals("true"))
                            add(name, true);
                        else if (data.toLowerCase().equals("false"))
                            add(name, false);
                        else
                            throw new IllegalArgumentException("Could not find \"" + type + " " + name + "\"");
                        break;
                    }
                case FLOAT:
                    add(name, Float.valueOf(data));
                    break;
                case INTEGER:
                    add(name, Integer.valueOf(data));
                    break;
                case LONG:
                    add(name, Long.valueOf(data));
                    break;
                case STRING:
                    add(name, data == null ? "" : String.valueOf(data));
                    break;
                default:
                    System.err.println("[DataStorage]::UNKNOWN datatype!");
                    return false;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Can not parse the data into this DataType!");
        }
        return true;
    }

    public void add(String name, String data) {
        variables.add(new DataVariable(name, data));
    }

    public void add(String name, long data) {
        variables.add(new DataVariable(name, data));
    }

    public void add(String name, int data) {
        variables.add(new DataVariable(name, data));
    }

    public void add(String name, float data) {
        variables.add(new DataVariable(name, data));
    }

    public void add(String name, boolean data) {
        variables.add(new DataVariable(name, data));
    }

    public Object get(DataType type, String name) throws IllegalArgumentException {
        for (DataVariable v : variables) {
            if (v.dt == type && v.name.equals(name))
                return v.obj;
        }
        throw new IllegalArgumentException("Could not find \"" + type + " " + name + "\"");
    }

    public String getString(String name) throws IllegalArgumentException {
        return (String) get(DataType.STRING, name);
    }

    public long getLong(String name) throws IllegalArgumentException {
        return (long) get(DataType.LONG, name);
    }

    public int getInteger(String name) throws IllegalArgumentException {
        return (int) get(DataType.INTEGER, name);
    }

    public float getFloat(String name) throws IllegalArgumentException {
        return (float) get(DataType.FLOAT, name);
    }

    public boolean getBoolean(String name) throws IllegalArgumentException {
        return (boolean) get(DataType.BOOLEAN, name);
    }

    public void clear() {
        variables.clear();
    }

}
