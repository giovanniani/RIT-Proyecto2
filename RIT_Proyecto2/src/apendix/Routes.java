/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apendix;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Greivin
 */
public final class Routes {

    private static final Path currentRelativePath = Paths.get("").toAbsolutePath();;
    public static String mainDir=currentRelativePath.toString()+"\\";
    public static String proyectDir=currentRelativePath.getParent().toString()+"\\";
    public static String scriptPath=proyectDir+"RIT_Proyecto2\\src\\apendix\\proyecto.py\";";
    public static String chromePath="C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
    public static String indexDir = "C:\\lucene-3.6.2\\ProductIndex";
    public static String dataDir = "C:\\lucene-3.6.2\\ProductDataPrueba";

    public static void setMainDir(String mainDir) {
        Routes.mainDir = mainDir;
    }

    public static void setProyectDir(String proyectDir) {
        Routes.proyectDir = proyectDir;
    }

    public static void setChromePath(String chromePath) {
        Routes.chromePath = chromePath;
    }

    public static void setIndexDir(String indexDir) {
        Routes.indexDir = indexDir;
    }

    public static void setDataDir(String dataDir) {
        Routes.dataDir = dataDir;
    }
    
}
