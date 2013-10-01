package com.skirrs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.util.Log;
 
public class JSONParser {
 
    static InputStream is    =  null;
    static JSONObject  jObj  =  null;
    static String      json  =  "";
    
    /*
     * JSON tags
     */
    public static final String TAG_STATUS              = "status";
    public static final String TAG_FIRST_NAME          = "first_name";
    public static final String TAG_LAST_NAME           = "last_name";
    public static final String TAG_PHONE_NUM           = "phone_number";
    public static final String TAG_USER_ID             = "user_id";
    public static final String TAG_RESULTS             = "results";
    public static final String TAG_GEOMETRY            = "geometry";
    public static final String TAG_LOCATION            = "location";
    public static final String TAG_FORMATTED_ADDRESS   = "formatted_address";
    public static final String TAG_SOURCE              = "source";
    public static final String TAG_DESTINATION         = "destination";
    public static final String TAG_DEPARTURE_DATE_TIME = "departure_date_time";
    public static final String TAG_SEATS               = "seats_offered";
    public static final String TAG_PRICE               = "price";
    public static final String TAG_ADDR_COMPONENTS     = "address_components";
    public static final String TAG_TYPES               = "types";
    public static final String TAG_LONG_NAME           = "long_name";
    
    public static final String TAG_ADMIN_AREA_1        = "administrative_area_level_1";
    public static final String TAG_ADMIN_AREA_2        = "administrative_area_level_2";
    public static final String TAG_COUNTRY             = "country";
    public static final String TAG_ROUTE               = "route";
    public static final String TAG_LOCALITY            = "locality";
    public static final String TAG_SUB_LOCALITY        = "sublocality";
    public static final String TAG_ESTABLISHMENT       = "establishment";
    
 
    /**
     * 
     */
    public JSONParser() {
 
    }
 
    /**
     * 
     * @param url
     * @param method
     * @param params
     * @return
     */
    public JSONObject makeHttpRequest( String url,
    								   String method,
    								   List<NameValuePair> params ) {
 
        try {
 
            if ( method == "POST" ) {
            	
                /*
                 * request method is POST
                 */
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost( url );
                httpPost.setEntity( new UrlEncodedFormEntity( params ) );

                HttpResponse httpResponse = httpClient.execute( httpPost );
                HttpEntity httpEntity = httpResponse.getEntity();
                
                is = httpEntity.getContent();
 
            } else if ( method == "GET" ) {
            	
                /*
                 * request method is GET
                 */
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format( params, "utf-8" );
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet( url );
 
                HttpResponse httpResponse = httpClient.execute( httpGet );
                HttpEntity httpEntity = httpResponse.getEntity();
                
                is = httpEntity.getContent();
            }           
 
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        } catch ( ClientProtocolException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
 
        try {
        	
            BufferedReader reader = new BufferedReader(	
            							new InputStreamReader(
            								is, "iso-8859-1" ), 8 );
            
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ( ( line = reader.readLine() ) != null ) {
                sb.append( line + "\n" );
            }
            
            is.close();
            json = sb.toString();

        } catch ( Exception e ) {
            Log.e( "Buffer Error", "Error converting result " + e.toString() );
        }
 
        /*
         * try to parse the string into a JSON object
         */
        try {
        	jObj = null;
            jObj = new JSONObject( json );
        } catch ( JSONException e ) {
            Log.e( "JSON Parser", "Error parsing data " + e.toString() + " in json string: " + json );
        }
 
        /*
         *  return JSON object
         */
        return jObj;
 
    }
    
}
