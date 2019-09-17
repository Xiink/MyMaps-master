package com.example.user.mymaps;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NgocTri on 12/11/2017.
 */

public class DirectionsParser {
    /**
     * Returns a list of lists containing latitude and longitude from a JSONObject
     */
    public List<List<HashMap<String, String >>> parse(JSONObject jObject) {

        List<List<HashMap<String, String >>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        String AllMessage = "";

        try {

            jRoutes = jObject.getJSONArray("routes");

            // Loop for all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
                //Loop for all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    //Loop for all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String instructions= "";
                        String polyline = "";
                        String howlong;
                        String howlong2;
                        int text;
                        String Turn;
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");

                        instructions = (String) (((JSONObject) jSteps.get(0)).get("html_instructions"));//抓取路線資訊
                        text = (int) ((JSONObject) ((JSONObject) jSteps.get(0)).get("distance")).get("value");//抓取距離
                        AllMessage +=  k+"."+(((JSONObject) jSteps.get(k)).get("html_instructions"))+"\n"+"-------------------------------------"+"\n";
                        howlong = "\n"+instructions;
                        //取得左右轉
                       if(jSteps.length()>1){
                            Turn =  (String) (((JSONObject) jSteps.get(1)).get("html_instructions"));
                        }else{
                            Turn =  (String) (((JSONObject) jSteps.get(0)).get("html_instructions"));
                        }
                        howlong2 = "\n"+Turn;
                        List list = decodePolyline(polyline);
                        //Loop for all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            hm.put("howlong",howlong);
                            hm.put("Turn",howlong2);
                            hm.put("Km",Integer.toString(text));
                            hm.put("all",AllMessage);
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return routes;
    }
    /**
     * Method to decode polyline
     * Source : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    /**使用Java從Google Maps Direction API解碼折線*/
    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
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
}