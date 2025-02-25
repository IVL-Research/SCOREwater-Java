/*
 * Copyright (c) 2022, Civity BV Zeist
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the copyright holder nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package nl.civity.fiware.scorewater.turbinator.domain.json;

import java.util.Set;
import java.util.TreeSet;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import nl.civity.fiware.scorewater.turbinator.domain.TurbinatorLocation;
import nl.civity.fiware.scorewater.turbinator.domain.TurbinatorMeasurement;
import org.json.JSONObject;

/**
 *
 * @author basvanmeulebrouk
 */
public class TurbinatorLocationJson {
    
    public static Set<TurbinatorLocation> fromJsonString(String data) {
        JSONObject jsonObject = new JSONObject(data);
        return fromJsonObject(jsonObject);
    }
    
    public static Set<TurbinatorLocation> fromJsonObject(JSONObject jsonObject) {
        Set<TurbinatorLocation> result = new TreeSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");
        String formattedString = ZonedDateTime.now().format(formatter);
        ZonedDateTime recordingTimestamp = ZonedDateTime.parse(formattedString, formatter);
        System.out.print(recordingTimestamp);

        if (jsonObject.has("values")){
            Set<TurbinatorMeasurement> turbinatorMeasurements = TurbinatorMeasurementJson.fromJsonObject(jsonObject);
            for (TurbinatorMeasurement turbinatorMeasurement : turbinatorMeasurements) {
                recordingTimestamp = turbinatorMeasurement.getPrimaryKey().getRecordingTimestamp();
                break;
            }
        } else if ((jsonObject.has("DT"))){
            recordingTimestamp = ZonedDateTime.parse(jsonObject.getString("DT"));
        } else{
        }

        if (jsonObject.has("lon") && jsonObject.has("lat")) {
            
            String entityId = jsonObject.getString("id");
            Double fw = jsonObject.getDouble("FW");
            if (jsonObject.has("batlvl")){
                Integer batlvl = jsonObject.getInt("batlvl");
                try {
                    Double lon = jsonObject.getDouble("lon");
                    Double lat = jsonObject.getDouble("lat");
                    result.add(new TurbinatorLocation(entityId, recordingTimestamp, fw, lon, lat, batlvl));
                } catch (org.json.JSONException e) {
                    result.add(new TurbinatorLocation(entityId, recordingTimestamp, fw, batlvl));
                }                    
            } else {
                try {
                    Double lon = jsonObject.getDouble("lon");
                    Double lat = jsonObject.getDouble("lat");
                    result.add(new TurbinatorLocation(entityId, recordingTimestamp, fw, lon, lat));
                } catch (org.json.JSONException e) {
                    result.add(new TurbinatorLocation(entityId, recordingTimestamp, fw));
                }

            }
        } else if (jsonObject.has("batlvl")){

            String entityId = jsonObject.getString("id");
            Double fw = jsonObject.getDouble("FW");
            Integer batlvl = jsonObject.getInt("batlvl");

            // Assuming the first measurement in the list contains the correct timestamp
            result.add(new TurbinatorLocation(entityId, recordingTimestamp, fw, batlvl));

        } else {

            String entityId = jsonObject.getString("id");
            Double fw = jsonObject.getDouble("FW");

            result.add(new TurbinatorLocation(entityId, recordingTimestamp, fw));
        } 
        return result;
    }
}