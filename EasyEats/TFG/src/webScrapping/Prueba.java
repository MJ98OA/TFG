package webScrapping;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.util.Base64;

public class Prueba {
	public static void main(String[] args) throws IOException {
		Base64.Decoder decoder = Base64.getDecoder(); 
		MongoClient mc = new MongoClient();
		MongoDatabase mdb = mc.getDatabase("Restaurantes");
		MongoCollection<org.bson.Document> datosRestaurante = mdb.getCollection("Datos");
		int contador=0;

		File fich = new File("restaurantes.txt");
		Scanner scan = new Scanner(fich);
		
		String nombreRestaurante="";
		String precios="";
		String estrellas="";
		String coordenadas="";
		double latitud=0.0;
		double longitud=0.0;
		
		while(scan.hasNext()) {
		
			org.bson.Document documento = new org.bson.Document();
			
			Document doc = Jsoup.connect(scan.nextLine()).get();
			Element elemento = doc.getElementsByClass("fHibz").first();
			nombreRestaurante=elemento.text();
			System.out.println(nombreRestaurante);
			
			elemento = doc.getElementsByClass("cfvAV").first();
			precios=elemento.text();
			System.out.println(precios);
			
			
			elemento = doc.getElementsByClass("fdsdx").first();
			estrellas=elemento.text();
			System.out.println(estrellas);
			

			elemento = doc.getElementsByClass("dOGcA Ci Wc _S C dkdrG").first();
			String coordenadasCifradas=elemento.attr("data-encoded-url");
			
			

			coordenadas = new String(decoder.decode(coordenadasCifradas));  
		    String s1 = coordenadas.substring(coordenadas.indexOf("@")+1);
		    
		    coordenadas =obtenercoordenadas(s1,'_');
		   
		    System.out.println(coordenadas);
		    
		    String s = coordenadas;
		    String[] parts = s.split(","); 
		    latitud = Double.parseDouble(parts[0]);
		    longitud = Double.parseDouble(parts[1]);
		    
		    
		    System.out.println("////////////////////////////////////////////////////////");
		    
		    

		    
		    documento.append("idN", contador)
		    		.append("Nombre", nombreRestaurante)
		    		.append("Precio Medio", precios)
		    		.append("Estrellas", estrellas)
		    		.append("Latitud", latitud)
		    		.append("Longitud", longitud);
		    
		    
		    org.bson.Document reptido =(org.bson.Document) datosRestaurante.find(new org.bson.Document("Nombre",nombreRestaurante)).first();
		    
		    if(reptido==null){
		    	datosRestaurante.insertOne(documento);
		    	contador++;
		    }
		    		
		    
		}
		
		scan.close();
		
		System.out.println("he salido");
			
	}
	
	
	public static String obtenercoordenadas(String coordenada, char delimitador) {
		
		String resultado="";
		boolean centinela=false;
		int i=0;
		
		centinela=coordenada.charAt(i)==delimitador;
		while(centinela==false) {
			
			resultado+=coordenada.charAt(i);
			i+=1;
			centinela=coordenada.charAt(i)==delimitador;
		}
		
		
		return resultado;
	}
	
}
