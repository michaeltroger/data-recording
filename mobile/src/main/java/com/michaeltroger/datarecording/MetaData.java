package com.michaeltroger.datarecording;

public class MetaData {

    private String classLabel;
    private String personId;
    private String location;

    public MetaData(final String classLabel, final String personId, final String location) {

        this.classLabel = classLabel;
        this.personId = personId;
        this.location = location;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public String getPersonId() {
        return personId;
    }

    public String getLocation() {
        return location;
    }

    public void setClassLabel(final String classLabel) {
        this.classLabel = classLabel;
    }

    public void setPersonId(final String personId) {
        this.personId = personId;
    }

    public void setLocation(final String location) {
        this.location = location;
    }
}
