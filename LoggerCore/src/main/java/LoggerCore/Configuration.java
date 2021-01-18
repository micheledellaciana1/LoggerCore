package LoggerCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Configuration {

    private ArrayList<ConfigAttribute> _attribute;
    private String _name;

    public boolean verbose = true;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public Configuration(String Name, String file_path) {
        _attribute = new ArrayList<ConfigAttribute>();
        _name = Name;

        try {
            File fileConfig = new File(file_path);
            Scanner reader;
            reader = new Scanner(fileConfig);

            while (reader.hasNextLine()) {
                String[] splitted = reader.nextLine().split(":");
                _attribute.add(new ConfigAttribute(splitted[0], splitted[1]));
            }

            reader.close();

        } catch (FileNotFoundException e) {
            if (verbose)
                e.printStackTrace();
        }
    }

    public String search(String Name) {
        try {
            for (int i = 0; i < _attribute.size(); i++)
                if (_attribute.get(i).getName().equals(Name))
                    return _attribute.get(i).getValue();
        } catch (Exception e) {
        }

        System.out.println("Attribute <" + Name + "> not present in + <" + Name + "> Configuration");
        return null;
    }

    public Double searchDouble(String Name) {
        try {
            for (int i = 0; i < _attribute.size(); i++)
                if (_attribute.get(i).getName().equals(Name))
                    return Double.valueOf(_attribute.get(i).getValue());
        } catch (Exception e) {
        }

        System.out.println("Attribute <" + Name + "> not present in + <" + Name + "> Configuration");
        return null;
    }

    public Integer searchInteger(String Name) {
        try {
            for (int i = 0; i < _attribute.size(); i++)
                if (_attribute.get(i).getName().equals(Name))
                    return Integer.valueOf(_attribute.get(i).getValue());
        } catch (Exception e) {
        }

        System.out.println("Attribute <" + Name + "> not present in + <" + Name + "> Configuration");
        return null;
    }

    public Boolean searchBoolean(String Name) {
        try {
            for (int i = 0; i < _attribute.size(); i++)
                if (_attribute.get(i).getName().equals(Name))
                    return Boolean.valueOf(_attribute.get(i).getValue());
        } catch (Exception e) {
        }

        System.out.println("Attribute <" + Name + "> not present in + <" + Name + "> Configuration");
        return null;
    }

    public class ConfigAttribute {
        private String _name;
        private String _value;

        ConfigAttribute(String name, String value) {
            _name = name;
            _value = value;
        }

        public String getName() {
            return _name;
        }

        public String getValue() {
            return _value;
        }

        public void setName(String name) {
            _name = name;
        }

        public void setValue(String value) {
            _value = value;
        }
    }
}
