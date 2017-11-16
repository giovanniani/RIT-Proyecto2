def main(ruta):
    print(ruta)     #aqui iria el contenido del script

    print("*ok*")   #si imprime *ok*,  desde java se captura ese print y 
                    #indica que todo funciono correctamente

try:
    route=sys.argv[1]
    
    if __name__ == '__main__':
        main(route)
        
    else:
        print("*error*")    #indica que hubo un error
except:
    print("*error*")        #indica que hubo un error
    
