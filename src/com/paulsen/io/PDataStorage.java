package com.paulsen.io;

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

//	public static void main(String[] args) {
//		PDataStorage ds = new PDataStorage();

//		ds.add("teststring", "asdfe4[]");
//		ds.save("testfile.paulsen");

//		ds.read("testfile.paulsen");
//		String s = ds.getString("teststring");
//		System.out.println(s);
//	}

	public void save(String path) {
		PFile file = new PFile(path);
		String s = "";

		for (DataVariable v : variables)
			s += v;

		file.writeFile(s);
	}

	/**
	 * Reads dataStorage-file and stores all Data in this object
	 * 
	 * @param path
	 */
	public void read(String path) {
		if (PFolder.isFolder(path))
			return;

		String file = new PFile(path).getFileAsString();

		boolean hasBeenDataType = false, hasBeenVariableName = false;

		DataType currentDataType = DataType.STRING;
		String variableName = "";
		String data = "";

		loop: for (int i = 0; i < file.length(); i++) {
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
					System.err.println("[DataStorage]::could not identify datatype '" + c + "' at pos " + i);
					break loop;
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
					addData(currentDataType, variableName.replace("^<<^", "[").replace("^>>^", "]"),
							data.replace("^<<^", "[").replace("^>>^", "]"));
					variableName = "";
					data = "";
					hasBeenDataType = false;
					hasBeenVariableName = false;
					i++; // jump over second ]
				}
			}
		}
	}

	public boolean addData(DataType type, String name, String data) {
		try {
			switch (type) {
			case BOOLEAN:
				add(name, Boolean.valueOf(data));
				break;
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
				add(name, String.valueOf(data));
				break;
			default:
				System.err.println("[DataStorage]::UNKNOWN datatype!");
				return false;
			}
		} catch (Exception e) {
			System.err.println("[DataStorage]::Some error occured when adding data!");
			e.printStackTrace();
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

	public Object get(DataType type, String name) {
		for (DataVariable v : variables) {
			if (v.dt == type && v.name.equals(name))
				return v.obj;
		}
		return null;
	}

	public String getString(String name) {
		return (String) get(DataType.STRING, name);
	}

	public long getLong(String name) {
		return (long) get(DataType.LONG, name);
	}

	public int getInteger(String name) {
		return (int) get(DataType.INTEGER, name);
	}

	public float getFloat(String name) {
		return (float) get(DataType.FLOAT, name);
	}

	public boolean getBoolean(String name) {
		return (boolean) get(DataType.BOOLEAN, name);
	}

	public void clear() {
		variables.clear();
	}

}
