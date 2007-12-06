package org.zlibrary.core.options.config;

import java.util.*;

/*package*/final class ZLDeltaConfig {

	private final ZLDeletedValuesSet myDeletedValues = new ZLDeletedValuesSet();

	private final Set<String> myDeletedGroups = new HashSet<String>();

	private final ZLSimpleConfig mySetValues = new ZLSimpleConfig();

	public ZLDeltaConfig() {
	}

	public Set<String> getDeletedGroups() {
		return Collections.unmodifiableSet(myDeletedGroups);
	}

	public ZLDeletedValuesSet getDeletedValues() {
		return myDeletedValues;
	}

	public ZLSimpleConfig getSetValues() {
		return mySetValues;
	}

	/**
	 * 
	 * @param group
	 * @param name
	 * @param defaultValue
	 * @return defaultValue - when this value is not set or deleted
	 * new value (from setValues) - when it was set
	 * null - when it was deleted
	 */
	public String getValue(String group, String name, String defaultValue) {
		String value = mySetValues.getValue(group, name, defaultValue);
		if ((value == null) || (value.equals(defaultValue))) {
			if (myDeletedValues.contains(group, name)) {
				System.out.println("contains");
				return null;
			} else {
				return defaultValue;
			}
		} else {
			return value;
		}
	}

	public void setValue(String group, String name, String value,
			String category) {
		mySetValues.setValue(group, name, value, category);
	}
/*
	public void setCategory(String group, String name, String cat) {
		mySetValues.setCategory(group, name, cat);
	}*/

	public void unsetValue(String group, String name) {
		// TODO ��� �����??
		myDeletedValues.add(group, name);
		//System.out.println(group + " - - - -" + name);
		//System.out.println(myDeletedValues.getAll());
		mySetValues.unsetValue(group, name);
		//System.out.println(mySetValues.getValue(group, name, "NOVALUE"));
	}

	public void removeGroup(String group) {
		myDeletedGroups.add(group);
		mySetValues.removeGroup(group);
	}

	public void clear() {
		myDeletedValues.clear();
		myDeletedGroups.clear();
		mySetValues.clear();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("<config>\n");
		for (String group : myDeletedGroups) {
			sb.append("    <group name=\"" + group + "\"/>\n");
		}

		Set<ZLGroup> setGroups = mySetValues.getGroups();
		Set<String> writtenGroups = new HashSet<String>();

		for (ZLGroup group : setGroups) {
			sb.append("  <group name=\"" + group.getName() + "\">\n");
			writtenGroups.add(group.getName());
			for (ZLOptionInfo option : group.getOptions()) {
				sb.append("    <option name=\"" + option.getName() + "\" ");
				sb.append("value=\"" + option.getValue() + "\" ");
				sb.append("category=\"" + option.getCategory() + "\"/>\n");
			}
			for (ZLOptionID option : myDeletedValues.getAll()) {
				if (option.getGroup().equals(group)) {
					sb.append("    <option name=\"" + option.getName() + "\"/>\n");
				}
			}
			sb.append("  </group>\n");
		}

		for (String group : myDeletedValues.getGroups()) {
			sb.append("  <group name=\"" + group + "\">\n");
			for (ZLOptionID option : myDeletedValues.getAll()) {
				if (option.getGroup().equals(group)) {
					sb.append("      <option name=\"" + option.getName() + "\"/>\n");
				}
			}
			sb.append("  </group>\n");
		}

		sb.append("</config>");

		return sb.toString();
	}
}
