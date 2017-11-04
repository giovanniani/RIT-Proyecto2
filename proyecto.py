#geovanni villalobos
# -*- coding: utf-8 -*-
#python 2.7.13


import codecs
import snowballstemmer
import re
from os import listdir
from os.path import isfile, join
import os
import time

def getPaths(pPath):    #recibe la ruta del fichero original y devuelve todas las rutas de los archivos ,htm
    allPaths = []
    for x in os.walk(pPath):
        onlyFiles = [f for f in listdir(x[0]) if isfile(join(x[0], f))]
        for fil in onlyFiles:
            if fil.endswith(".htm"):
                allPaths.append(x[0] + "/" + fil)
    return allPaths
    
def examineFiles(pFilesList,pPath):
    ref = set([])        #campo donde se guardan las referencias
    text = set([])       #campo donde se guardan los terminos
    texto = ""      #almacena todo el texto normal de la página, incluidas las referencias
    for file in pFilesList:
        print(file)
        f = open(file, "r")
        document = f.read()     
        document = document.decode('utf8')
        document = document.lower()
        match = re.findall(u'<p>(.+)<\/p>',document)    #encuentra todo lo que esta dentro de <p>...</p>
        f.close()
        if match:
            for mActual in match:
                                #viene separacion de texto de referencia y texto normal
                matchP = re.findall(u'<a [\)\(#\w\ =\"\/%a-zA-ZáéíóúüÁÉÍÓÚÜñÑ,\n\-\_]+>([\d\(\ =\"\/%a-zA-ZáéíóúüÁÉÍÓÚÜñÑ,]+)<\/a>|(?!<)([\)\(#\ =%a-zA-ZáéíóúüÁÉÍÓÚÜñÑ\,\n\-\_\"\d]+)(?!>)',mActual)
                if matchP:
                    for match2 in matchP:
                        if len(match2[0]) > 0:
                            ref.add(match2[0])
                            texto = texto + " " + match2[0]
                        else:
                            pass
                        if len(match2[1]) > 0:
                            texto = texto + " " + match2[1]
                        else:
                            pass
        match = re.findall(u'([a-zA-ZáéíóúüÁÉÍÓÚÜñÑ]+)',texto)       #luego de obtenido el texto normal de la pagina se viene la depuración del mismo
        if match:
            for word in match:
                text.add(word)
        print(text)
        print("text-------------------------------------------------")
        newText = removeStopWords(pPath,text)
        print(newText)
        print("newText----------------------------------------------")
        
def removeStopWords(pPath,pList):
    stopWords = []
    dic2 = {}
    f = open(pPath + "/" + "stopwords.txt", "r")
    for line in f.readlines():
        line = line.decode('utf8')
        stopWords.append(line.rstrip("\n\r"))
    for data in stopWords:
        if data in pList:
            print(data)
            pList.remove(data)
        else:
            pass
    f.close()
    return pList
    

def main(pPath):
    files = getPaths(pPath)
    examineFiles(files,pPath)
