import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class ConexaoWeb {
    Socket socket;		//socket que vai tratar com o cliente.
    static String arqi = "index.html";	//se nao for passado um arquivo, o servidor fornecera a pagina index.html

    public ConexaoWeb(Socket s) {
	this.socket = s;
	System.out.println("Cliente conectado do IP " +
			   this.socket.getInetAddress().getHostAddress());

    }
    //metodo TrataConexao, aqui serao trocadas informacoes com o Browser...
	public void TrataConexao() {
	String metodo = "";	//String que vai guardar o metodo HTTP requerido
	String ct;		//String que guarda o tipo de arquivo: text/html;image/gif....
	String versao = "";	//String que guarda a versao do Protocolo.
	File arquivo;		//Objeto para os arquivos que vao ser enviados. 
	String nomeArq;		//String para o nome do arquivo.

	String raiz1 = "/home/mateus/Documents/Redes/parte2/html";	//String para o diretorio raiz (localhost).
	String raiz2 = "/home/mateus/Documents/Redes/parte2/html1";	//String para o diretorio raiz (127.0.0.1).
	String raiz = raiz1;

	String inicio;		//String para guardar o inicio da linha
	String senha_user = "";	//String para armazenar o nome e a senha do usuario
	Date now = new Date();
	DataOutputStream os = null;
	BufferedReader in = null;

	Boolean erro = false;
	Boolean autorizado = true;

	try {
	    in = new BufferedReader(new
				    InputStreamReader(this.socket.
						      getInputStream()));
	    os = new DataOutputStream(this.socket.getOutputStream());
	    String linha = in.readLine();
	    StringTokenizer st = new StringTokenizer(linha);
	    metodo = st.nextToken();
	    nomeArq = st.nextToken();
	    versao = st.nextToken();


	    if (nomeArq.endsWith("/")) {
		nomeArq += "index.html";
	    } else if (nomeArq.startsWith("/restrito")) {
		nomeArq += "/pagina.html";
		autorizado = false;
	    }

	    if (metodo.equals("GET")) {

		String str;
		while ((str = in.readLine()) != null && !str.equals("")) {
		    if (str.startsWith("Host")) {
			st = new StringTokenizer(str);
			st.nextToken();
			String host = st.nextToken();
			raiz =
			    host.startsWith("localhost") ? raiz1 : raiz2;
		    } else if (str.startsWith("Authorization")) {
			st = new StringTokenizer(str);
      st.nextToken();
			if (st.nextToken().equals("Basic")
			    && st.nextToken().
			    equals("YWRtaW46MTIzNDU2")) {
			    autorizado = true;
			}
		    }

		    System.out.println(str);
		}
    System.out.println();


		ct = TipoArquivo(nomeArq);
		try {
		    arquivo =
			new File(raiz,
				 nomeArq.substring(1, nomeArq.length()));
		    FileInputStream fis = new FileInputStream(arquivo);

		    byte[]dado = new byte[(int) arquivo.length()];
		    fis.read(dado);
		    String dadoString = new String(dado,
						   StandardCharsets.UTF_8);

		    System.out.println("Método: " + metodo);
		    System.out.println("Nome arquivo: " + nomeArq);
		    System.out.println("Versao: " + versao);

		    if (autorizado) {
			os.writeBytes("HTTP/1.0 200 OK\n");
			os.writeBytes("Server: LocalHost\n");
			os.writeBytes("MIME-version: 1.0\n");
			os.writeBytes("Content-type: text/html\n");
			os.writeBytes
			    ("Content-lenght: " + dadoString.length() +
			     "\n\n");
			os.writeBytes(dadoString);
		    } else {
			os.writeBytes("HTTP/1.0 401 Unauthorized\n");
			os.writeBytes("Server: LocalHost\n");
			os.writeBytes("MIME-version: 1.0\n");
			os.writeBytes("Content-type: text/html\n");
			os.writeBytes
			    ("WWW-Authenticate: Basic realm='System Administrator'\n");
		    }

		}
		catch(IOException e1) {
		    try {
			String dadoString = "Error 404: file not found";
			os.writeBytes("HTTP/1.0 404 NOT FOUND\n");
			os.writeBytes("Server: LocalHost\n");
			os.writeBytes("MIME-version: 1.0\n");
			os.writeBytes("Content-type: text/html\n");
			os.writeBytes
			    ("Content-lenght: " + dadoString.length() +
			     "\n\n");
			os.writeBytes(dadoString);
		    }
		    catch(IOException e2) {
		    }
		}

	    }
	}
	catch(IOException e) {
	    System.out.println("ERRO");
	}

	//Fecha o socket.
	try {
	    socket.close();
	}
	catch(IOException e) {
	    System.out.println(e.getMessage());
	}
    }

//Funcao que retorna o tipo do arquivo.

    public String TipoArquivo(String nome) {
	if (nome.endsWith(".html") || nome.endsWith(".htm"))
	    return "text/html";
	else if (nome.endsWith(".txt") || nome.endsWith(".java"))
	    return "text/plain";
	else if (nome.endsWith(".gif"))
	    return "image/gif";
	else if (nome.endsWith(".class"))
	    return "application/octed-stream";
	else if (nome.endsWith(".jpg") || nome.endsWith(".jpeg"))
	    return "image/jpeg";
	else
	    return "text/plain";
    }
}
