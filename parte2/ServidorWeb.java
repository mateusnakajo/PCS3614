import java.net.*;
import java.io.*;
import java.util.*;

public class ServidorWeb {
  public static void main(String[] args) {
    boolean DEBUG = true;
    int porta = 3322; 		//porta aonde o servidor vai aguardar por pedido de conexao.
    ServerSocket sw; 	//Socket servidor
	 
	//de um valor para a porta!
    
    try {
      sw = new ServerSocket(porta);
      if(DEBUG) {
        System.out.println("Porta "+porta+" aberta!");
      }

      //crie aqui o socket Servidor!!!!!!!!(objeto = new ServerSocket(parametros))

      while(true) {
        ConexaoWeb cw = new ConexaoWeb(sw.accept());
        cw.TrataConexao();
  	  }
    }
    catch(IOException e) {
      System.err.println("Servidor foi abortardo");
    }  
  }
}