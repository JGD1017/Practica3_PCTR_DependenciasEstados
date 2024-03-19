package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{

	private static final int AFORO_MAX = 50; //Aforo máximo
	private static final int AFORO_MIN = 0; //Aforo mínimo
	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	
	
	public Parque() {
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
	}


	@Override
	public synchronized void entrarAlParque(String puerta) throws InterruptedException{	
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		//Comprobamos que se puede entrar
		comprobarAntesDeEntrar();
				
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		
		//Comprobamos las invariantes
		checkInvariante();
		
		//Notificamos a los otros hilos
		notifyAll();
	}
	
	
	@Override
	public synchronized void salirDelParque(String puerta) throws InterruptedException {
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		// Comprobamos que se puede salir
		comprobarAntesDeSalir(puerta);
				
		// Decrementamos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)-1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");
		
		// Comprobamos las invariantes
		checkInvariante();
		
		// Notificamos a los demás hilos
		notifyAll();
	}
	
	
	
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parque";
		assert contadorPersonasTotales <= AFORO_MAX : "INV: El contador del parque debe ser igual o inferior al aforo máximo";
		assert contadorPersonasTotales >= AFORO_MIN : "INV: El contador del parque debe ser igual o superior al aforo mínimo";
		
	}

	protected void comprobarAntesDeEntrar() throws InterruptedException {
		// Para entrar es necesario que el aforo sea inferior al máximo
		while (contadorPersonasTotales == AFORO_MAX) {
			wait();
		}
		
	}

	protected void comprobarAntesDeSalir(String puerta) throws InterruptedException {
		// Para salir es necesario que el aforo total y el de la puerta sean mayores que el mínimo 
		while (contadorPersonasTotales == AFORO_MIN ||
				contadoresPersonasPuerta.get(puerta) == AFORO_MIN) {
			wait();
		}
	}


}
