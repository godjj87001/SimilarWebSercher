import java.util.*;

public class Test {

    static Map similarWebInfoMap = new TreeMap<String, SimilarWebInfo>();

    public static void main(String[] args) {

        HashMap dataMap;
        HashMap typeMap;
        HashMap tagMap;

        int domainSn = 0;
        String type = "gender";
        String tag = "male";
        double value = 10.15;
        String siteUrl = "yahoo.com";

        putMap(domainSn, type, tag, value, siteUrl);
        getMap(siteUrl);

        type = "gender";
        tag = "female";
        value = 10.1;
        siteUrl = "yahoo.com";
        putMap(domainSn, type, tag, value, siteUrl);
        getMap(siteUrl);


        domainSn=1;
        type = "gender";
        tag = "female";
        value = 5.88;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);

        domainSn=1;
        type = "gender";
        tag = "male";
        value = 9.88;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);
        getMap(siteUrl);

        domainSn=1;
        type = "gender";
        tag = "male";
        value = 9.4;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);
        getMap(siteUrl);

        domainSn=1;
        type = "age";
        tag = "65+";
        value = 6.55;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);

        domainSn=1;
        type = "age";
        tag = "18 - 24";
        value = 19.21;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);
        getMap(siteUrl);


        domainSn=1;
        type = "age";
        tag = "25 - 34";
        value = 2.35;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);

        domainSn=1;
        type = "age";
        tag = "55 - 64";
        value = 5.6;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);

        domainSn=1;
        type = "age";
        tag = "45 - 54";
        value = 19.21;
        siteUrl = "google.com";
        putMap(domainSn, type, tag, value, siteUrl);
        getMap(siteUrl);

        SimilarWebInfo similarWebInfo = (SimilarWebInfo) similarWebInfoMap.get(siteUrl);


        dataMap = similarWebInfo.getDataMap();
        ArrayList<String> sexTagList = new ArrayList();
        sexTagList.add("male");
        sexTagList.add("female");

        ArrayList<String> ageTagList = new ArrayList();
        ageTagList.add("18 - 24");
        ageTagList.add("25 - 34");
        ageTagList.add("35 - 44");
        ageTagList.add("45 - 54");
        ageTagList.add("55 - 64");
        ageTagList.add("65+");
        ArrayList ageInfoList = new ArrayList<>();
        typeMap = (HashMap) dataMap.get("gender");
        for (String sex:sexTagList){
            Double tagValue = (Double) typeMap.get(sex);
            ageInfoList.add(tagValue);
        }
        String sexInfo = ageInfoList.toString();
        System.out.println(sexInfo);
        typeMap = (HashMap) dataMap.get("age");
        ageInfoList.clear();
        for (String age:ageTagList){
            Double tagValue = (Double) typeMap.get(age);
            ageInfoList.add(tagValue);
        }

        System.out.println(ageInfoList);
        double ageMerge= Double.parseDouble(ageInfoList.get(ageInfoList.size()-1).toString())
                +Double.parseDouble(ageInfoList.get(ageInfoList.size()-1).toString());

        ageInfoList.remove(ageInfoList.size()-1);
        ageInfoList.remove(ageInfoList.size()-1);
        ageInfoList.add(ageMerge);
        System.out.println(ageInfoList);







//        System.out.println(femaleValue);



//        System.out.println(similarWebInfo.getDataMap());

        siteUrl = "yahoo.com";
        similarWebInfo = (SimilarWebInfo) similarWebInfoMap.get(siteUrl);
        tagMap = similarWebInfo.getTagMap();
        dataMap = similarWebInfo.getDataMap();
//        System.out.println(dataMap.values());
//        System.out.println(tagMap.values());


//        System.out.println(dataMap.values().toArray()[0]);



//        System.out.println(tagMap.get("female"));


    }


    public static void getMap(String siteUrl) {
        SimilarWebInfo similarWebInfo = (SimilarWebInfo) similarWebInfoMap.get(siteUrl);
//        System.out.println(similarWebInfo.dataMap.values());
        HashMap dataMap;
        HashMap typeMap;
        HashMap tagMap;
        tagMap = similarWebInfo.getTagMap();
        typeMap = similarWebInfo.getTypeMap();
        dataMap = similarWebInfo.getDataMap();

    }


//    public static void putMap(int domainSn, String type, String tag, double value, String siteUrl) {
//        SimilarWebInfo similarWebInfo = (SimilarWebInfo) similarWebInfoMap.getOrDefault(siteUrl, new SimilarWebInfo());
//        HashMap<String, Double> tagMap = similarWebInfo.getTagMap();
//        HashMap typeMap = similarWebInfo.getTypeMap();
//        HashMap<String, HashMap<Integer, HashMap<String, Double>>> dataMap = similarWebInfo.getDataMap();
//
//        tagMap.put(tag, value);
//        typeMap.put(type, tagMap);
//        dataMap.put(type, typeMap);
//
//        similarWebInfo.setTagMap(tagMap);
//        similarWebInfo.setTypeMap(typeMap);
////        similarWebInfo.setDataMap(dataMap);
//        similarWebInfo.setSn(domainSn);
//        similarWebInfo.setSiteUrl(siteUrl);
//        similarWebInfoMap.put(siteUrl, similarWebInfo);
//    }

    public static void putMap(int domainSn, String type, String tag, double value, String siteUrl) {
        SimilarWebInfo similarWebInfo = (SimilarWebInfo) similarWebInfoMap.getOrDefault(siteUrl, new SimilarWebInfo());
        HashMap<String, HashMap<String, Double>> dataMap = similarWebInfo.getDataMap();
        HashMap<String, Double> tagMap = dataMap.getOrDefault(type, new HashMap<>());
        tagMap.put(tag, value);
        dataMap.put(type, tagMap);

        similarWebInfo.setTagMap(tagMap);
        similarWebInfo.setDataMap(dataMap);
        similarWebInfo.setSn(domainSn);
        similarWebInfo.setSiteUrl(siteUrl);
        similarWebInfoMap.put(siteUrl, similarWebInfo);
    }

    public static class SimilarWebInfo {

        HashMap tagMap = new HashMap<>();
        HashMap typeMap = new HashMap<>();
        HashMap dataMap = new HashMap<>();
        public int sn;
        public String siteUrl;
        public String type;
        public String tag;
        public double value;

        public HashMap<String, Double> getTagMap() {

            return tagMap;
        }


        public HashMap getTypeMap() {
            return typeMap;
        }

        public void setTypeMap(HashMap typeMap) {
            this.typeMap = typeMap;
        }

        public HashMap getDataMap() {
            return dataMap;
        }

        public void setDataMap(HashMap dataMap) {
            this.dataMap = dataMap;
        }


        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public void setTagMap(HashMap tagMap) {
            this.tagMap = tagMap;
        }

        public int getSn() {
            return sn;
        }

        public void setSn(int sn) {
            this.sn = sn;
        }

        public String getSiteUrl() {
            return siteUrl;
        }

        public void setSiteUrl(String siteUrl) {
            this.siteUrl = siteUrl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }


        public SimilarWebInfo() {
        }

    }
}
