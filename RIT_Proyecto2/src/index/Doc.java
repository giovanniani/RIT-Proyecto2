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

/**
 *
 * @author Greivin
 */
public class Doc {
    public String texto;
    public String ref;
    public String orifinalPath;
    public String sourcePath;
    public Doc(String json)
    {
        parseJson(json);
    }
    public Doc(File file) throws IOException
    {
        parseJson(new String(Files.readAllBytes(Paths.get(file.getPath()))));
    }
    public void parseJson(String json){
        
    }
}
