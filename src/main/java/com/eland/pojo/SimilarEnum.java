package com.eland.pojo;

public class SimilarEnum {
    public enum ageEnum {
        RANGE1("18 - 24"), RANGE2("25 - 34"), RANGE3("35 - 44"), RANGE4("45 - 54"), RANGE5("55 - 64"), RANGE6("65+");

        private final String value;

        ageEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    public enum genderEnum {
        Male,Female;
    }
}