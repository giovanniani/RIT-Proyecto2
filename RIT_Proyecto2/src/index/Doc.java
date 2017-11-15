/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Greivin
 */
public class Doc {
    public String texto="";
    public String ref="";
    public String originalPath="";
    public String sourcePath="";
    public JSONObject json;
    public Doc(String json)
    {
        parseJson(json);
    }
    public Doc(File file) throws IOException
    {
        sourcePath=file.getCanonicalPath();
        parseJson(new String(Files.readAllBytes(Paths.get(file.getPath()))));
    }
    public void parseJson(String jsonText){
        json = (JSONObject) JSONValue.parse(jsonText);
        try{
            texto=(String) json.get("words");
            ref=(String) json.get("ref");
            originalPath=(String) json.get("rute");
        }
        catch(Exception e){System.out.println("Mensaje de error en el parseo del archivo JSON.\n"+e.getMessage());}
    }
}
