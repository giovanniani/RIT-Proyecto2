def main(ruta):
    print(ruta)     #aqui iria el contenido del script

    print("*ok*")   #si imprime *ok*,  desde java se captura ese print y 
                    #indica que todo funciono correctamente

try:
    libraryRoute=sys.argv[1]
    stopRoute=sys.argv[2]
    dicRoute=sys.argv[3]
    
    if __name__ == '__main__':
        main(libraryRoute)
        stopRoute
        dicRoute
    else:
        raise ValueError()    
        
    
except:
    print("*error*")        #indica que hubo un error
    
