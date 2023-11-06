package de.paulsenik.jpl.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PDataStorage {

  private final Map<String, Object> variables = new HashMap<>();

  public void save(String fileLocation) {
    PFile file = new PFile(fileLocation);
    JSONArray array = new JSONArray();

    for (String key : variables.keySet()) {
      JSONObject obj = new JSONObject();
      obj.put(key, variables.get(key));
      array.put(obj);
    }

    file.writeFile(array.toString(2));
  }

  /**
   * Reads dataStorage-file and stores all Data in this object.
   * <br>
   * Ignores Variables with no valid value
   *
   * @throws IllegalArgumentException when file does not meet the expectations of this format
   */
  public void read(String fileLocation) throws JSONException, IOException {
    if (PFolder.isFolder(fileLocation)) {
      return;
    }

    String file = new PFile(fileLocation).getFileAsString();

    JSONArray json = new JSONArray(file);

    for (Object obj : json) {
      JSONObject o = (JSONObject) obj;
      try {
        String key = o.keySet().iterator().next();
        variables.put(key, o.get(key));
      } catch (NoSuchElementException e) {
        // Ignore empty Elements
        //throw new JSONException("No valid JSON-Key!");
      }
    }
  }

  public Set<String> getVariableKeys() {
    return variables.keySet();
  }

  public void put(String name, String data) {
    variables.put(name, data);
  }

  public void put(String name, long data) {
    variables.put(name, data);
  }

  public void put(String name, int data) {
    variables.put(name, data);
  }

  public void put(String name, float data) {
    variables.put(name, data);
  }

  public void put(String name, double data) {
    variables.put(name, data);
  }

  public void put(String name, boolean data) {
    variables.put(name, data);
  }

  public Object get(String name) {
    return variables.get(name);
  }

  public String getString(String name) throws ClassCastException, NullPointerException {
    return variables.get(name).toString();
  }

  public Long getLong(String name) throws ClassCastException, NullPointerException {
    return Long.valueOf(variables.get(name).toString());
  }

  public Integer getInteger(String name) throws ClassCastException, NullPointerException {
    return Integer.valueOf(variables.get(name).toString());
  }

  public Float getFloat(String name) throws ClassCastException, NullPointerException {
    return Float.valueOf(variables.get(name).toString());
  }

  public Double getDouble(String name) throws ClassCastException, NullPointerException {
    return Double.valueOf(variables.get(name).toString());
  }

  public Boolean getBoolean(String name) throws ClassCastException, NullPointerException {
    return Boolean.valueOf(variables.get(name).toString());
  }

  public void clear() {
    variables.clear();
  }

}
