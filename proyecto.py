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
    
def examineFile(pFile):
    ref = set([])        #campo donde se guardan las referencias
    text = set([])       #campo donde se guardan los terminos
    texto = ""      #almacena todo el texto normal de la página, incluidas las referencias
    f = open(pFile, "r")
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
    return text
        
def removeStopWords(pPath,pList):
    stopWords = []
    dic2 = {}
    f = open(pPath + "/" + "stopwords.txt", "r")
    for line in f.readlines():
        line = line.decode('utf8')
        stopWords.append(line.rstrip("\n\r"))
    for data in stopWords:
        if data in pList:
            pList.remove(data)
        else:
            pass
    f.close()
    return pList

def stemmer(pList):
    stemmer = snowballstemmer.stemmer('spanish')
    stemmedWords = set([])
    for word in pList:
        stemmed = stemmer.stemWord(word)
        stemmedWords.add(word)
    return stemmedWords

def saveDictionary(pFilePath,pList,pDicPath,pIndex):
    name = pFilePath.split("/")
    name = name[len(name)-1].split(".")[0]
    name = "D"+ str(pIndex) + "-" + name
    f = open(pDicPath + "/" + name + "-dicionary.txt", "w+")
    f.write(name + "\n")
    for i in range(0,len(pList)-2,1):
        data = list(pList)[i]
        data = data.encode('utf-8')
        f.write(data + ", ")
    data = list(pList)[len(pList)-1]
    data = data.encode('utf-8')
    f.write(data)
    f.close()
        
def main(pPath):
    sWPath = input(u"Ingrese la ubicación del archivo de stopwords: \n")
    dicPath = input(u"Ingrese la ubicación donde se guardará los diccionarios de palabras: \n")
    files = getPaths(pPath)
    for fileDir in files:
        text = examineFile(fileDir)
        textSW = removeStopWords(sWPath,text)
        stemmed = stemmer(textSW)
        saveDictionary(fileDir,stemmed,dicPath,files.index(fileDir))
        

    
