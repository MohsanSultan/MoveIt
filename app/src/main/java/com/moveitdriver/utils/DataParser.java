package com.moveitdriver.utils;

import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude) );
                            hm.put("lng", Double.toString((list.get(l)).longitude) );
                            hm.put("distance", (String) ((JSONObject)jLegs.get(j)).getJSONObject("distance").get("text"));
                            hm.put("duration", (String) ((JSONObject)jLegs.get(j)).getJSONObject("duration").get("text"));
                            hm.put("summary", ((JSONObject)jRoutes.get(i)).getString("summary"));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;
    }


    public ArrayList<HashMap<String,DirectionData>> parseDirection(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        ArrayList<HashMap<String, DirectionData>> listDirections = new ArrayList<HashMap<String, DirectionData>>();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        //polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        // Get "html_instructions" tag to get the directions
                        HashMap<String, DirectionData> hmDirec = new HashMap<String, DirectionData>();
                        JSONObject jObjChild = (JSONObject) jSteps.get(k);
                        String start_lat = (jObjChild).getJSONObject("start_location").get("lat").toString();
                        String start_lng = (jObjChild).getJSONObject("start_location").get("lng").toString();
                        String end_lat = (jObjChild).getJSONObject("end_location").get("lat").toString();
                        String end_lng = (jObjChild).getJSONObject("end_location").get("lng").toString();
                        String distance = (jObjChild).getJSONObject("distance").get("text").toString();
                        String duration = (jObjChild).getJSONObject("duration").get("text").toString();
                        String html_instructions = Html.fromHtml((String) ((JSONObject) jSteps.get(k)).get("html_instructions")).toString();
                        String direction="";
                        if((jObjChild).has("maneuver")){
                            direction=(jObjChild).get("maneuver").toString();
                        }else{
                            direction="Head";
                        }

                        DirectionData d1= new DirectionData(start_lat, start_lng, end_lat, end_lng,  distance, duration,html_instructions,direction, list);
                        //adding values to map
                        hmDirec.put("polyine", d1);
                        listDirections.add(hmDirec);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return listDirections;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public class DirectionData{
        private String start_lat;
        private String start_lng;
        private String end_lat;
        private String end_lng;
        private String distance;
        private String duration;
        private String html_instructions;
        private String directions;
        private List<LatLng> listPoly;
        public DirectionData(String start_lat, String start_lng, String end_lat, String end_lng, String distance, String duration, String html_instructions, String direction, List<LatLng> listPoly){
            this.start_lat = start_lat;
            this.start_lng = start_lng;
            this.end_lat = end_lat;
            this.end_lng = end_lng;
            this.distance=distance;
            this.duration=duration;
            this.html_instructions = html_instructions;
            this.directions = direction;
            this.listPoly = listPoly;
        }

        public String getStart_lat() {
            return start_lat;
        }

        public void setStart_lat(String start_lat) {
            this.start_lat = start_lat;
        }

        public String getStart_lng() {
            return start_lng;
        }

        public void setStart_lng(String start_lng) {
            this.start_lng = start_lng;
        }

        public String getEnd_lat() {
            return end_lat;
        }

        public void setEnd_lat(String end_lat) {
            this.end_lat = end_lat;
        }

        public String getEnd_lng() {
            return end_lng;
        }

        public void setEnd_lng(String end_lng) {
            this.end_lng = end_lng;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }


        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }


        public String getHtml_instructions() {
            return html_instructions;
        }

        public void setHtml_instructions(String html_instructions) {
            this.html_instructions = html_instructions;
        }
        public String getDirection() {
            return directions;
        }

        public void setDirection(String directions) {
            this.directions = directions;
        }
        public List<LatLng> getListPoly() {
            return listPoly;
        }

        public void setListPoly(List<LatLng> listPoly) {
            this.listPoly = listPoly;
        }
    }
}

